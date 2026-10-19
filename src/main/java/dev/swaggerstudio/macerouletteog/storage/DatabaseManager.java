package dev.swaggerstudio.macerouletteog.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class DatabaseManager {

    private final MaceRouletteOG plugin;
    private HikariDataSource dataSource;

    public DatabaseManager(MaceRouletteOG plugin) {
        this.plugin = plugin;
        initDatabase();
    }

    private void initDatabase() {
        FileConfiguration config = plugin.getConfigManager().getMainConfig();
        String type = config.getString("database.type", "SQLITE").toUpperCase();

        HikariConfig hikari = new HikariConfig();
        hikari.setPoolName("MaceRouletteOG-Pool");

        if (type.equals("MYSQL")) {
            String host = config.getString("database.mysql.host", "localhost");
            int port = config.getInt("database.mysql.port", 3306);
            String db = config.getString("database.mysql.database", "maceroulette");
            String user = config.getString("database.mysql.username", "root");
            String pass = config.getString("database.mysql.password", "password");

            hikari.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&autoReconnect=true");
            hikari.setUsername(user);
            hikari.setPassword(pass);
        } else {
            File dbFile = new File(plugin.getDataFolder(), "database.db");
            hikari.setJdbcUrl("jdbc:sqlite:" + dbFile.getAbsolutePath());
            hikari.setDriverClassName("org.sqlite.JDBC");
        }

        hikari.setMaximumPoolSize(config.getInt("database.mysql.pool-size", 10));
        this.dataSource = new HikariDataSource(hikari);

        // Create table async
        plugin.getFoliaScheduler().runAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "CREATE TABLE IF NOT EXISTS mrog_stats (" +
                                 "uuid VARCHAR(36) PRIMARY KEY, " +
                                 "wins INT DEFAULT 0, " +
                                 "kills INT DEFAULT 0, " +
                                 "deaths INT DEFAULT 0, " +
                                 "rounds_survived INT DEFAULT 0)"
                 )) {
                stmt.execute();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to initialize database table: " + e.getMessage());
            }
        });
    }

    public CompletableFuture<PlayerStats> loadStats(UUID uuid) {
        CompletableFuture<PlayerStats> future = new CompletableFuture<>();
        plugin.getFoliaScheduler().runAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement("SELECT wins, kills, deaths, rounds_survived FROM mrog_stats WHERE uuid = ?")) {
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    future.complete(new PlayerStats(uuid, rs.getInt("wins"), rs.getInt("kills"), rs.getInt("deaths"), rs.getInt("rounds_survived")));
                } else {
                    future.complete(new PlayerStats(uuid, 0, 0, 0, 0));
                }
            } catch (SQLException e) {
                future.completeExceptionally(e);
            }
        });
        return future;
    }

    public void saveStats(PlayerStats stats) {
        plugin.getFoliaScheduler().runAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO mrog_stats (uuid, wins, kills, deaths, rounds_survived) VALUES (?, ?, ?, ?, ?) " +
                                 "ON CONFLICT(uuid) DO UPDATE SET wins = ?, kills = ?, deaths = ?, rounds_survived = ?"
                 )) {
                stmt.setString(1, stats.getUuid().toString());
                stmt.setInt(2, stats.getWins());
                stmt.setInt(3, stats.getKills());
                stmt.setInt(4, stats.getDeaths());
                stmt.setInt(5, stats.getRoundsSurvived());
                // Update params
                stmt.setInt(6, stats.getWins());
                stmt.setInt(7, stats.getKills());
                stmt.setInt(8, stats.getDeaths());
                stmt.setInt(9, stats.getRoundsSurvived());
                stmt.executeUpdate();
            } catch (SQLException e) {
                plugin.getLogger().severe("Failed to save player stats: " + e.getMessage());
            }
        });
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}