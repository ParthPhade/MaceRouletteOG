package dev.swaggerstudio.macerouletteog.arena;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ArenaManager {

    private final MaceRouletteOG plugin;
    private final Map<String, Arena> arenas = new ConcurrentHashMap<>();
    private final Map<UUID, Arena> playerArenaMap = new ConcurrentHashMap<>();
    private File arenasFile;
    private FileConfiguration arenasConfig;

    public ArenaManager(MaceRouletteOG plugin) {
        this.plugin = plugin;
        loadArenasFromFile();
    }

    public void loadArenasFromFile() {
        arenasFile = new File(plugin.getDataFolder(), "arenas.yml");
        if (!arenasFile.exists()) {
            plugin.saveResource("arenas.yml", false);
        }
        arenasConfig = YamlConfiguration.loadConfiguration(arenasFile);
        arenas.clear();

        ConfigurationSection section = arenasConfig.getConfigurationSection("arenas");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            ConfigurationSection arenaSec = section.getConfigurationSection(id);
            if (arenaSec == null) continue;

            Location center = arenaSec.getLocation("center");
            Location lobby = arenaSec.getLocation("lobby");
            Location spectator = arenaSec.getLocation("spectator");
            Location pos1 = arenaSec.getLocation("pos1");
            Location pos2 = arenaSec.getLocation("pos2");

            if (center != null) {
                Arena arena = new Arena(plugin, id, center, lobby, spectator, pos1, pos2);
                arenas.put(id.toLowerCase(), arena);
            }
        }
        plugin.getLogger().info("Loaded " + arenas.size() + " arenas from arenas.yml.");
    }

    public void saveArena(String id, Location center, Location lobby, Location spectator, Location pos1, Location pos2) {
        arenasConfig.set("arenas." + id + ".center", center);
        arenasConfig.set("arenas." + id + ".lobby", lobby);
        arenasConfig.set("arenas." + id + ".spectator", spectator);
        arenasConfig.set("arenas." + id + ".pos1", pos1);
        arenasConfig.set("arenas." + id + ".pos2", pos2);

        try {
            arenasConfig.save(arenasFile);
            Arena arena = new Arena(plugin, id, center, lobby, spectator, pos1, pos2);
            arenas.put(id.toLowerCase(), arena);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save arena " + id + " to arenas.yml: " + e.getMessage());
        }
    }

    public void registerArena(String id, Arena arena) {
        arenas.put(id.toLowerCase(), arena);
    }

    public Arena getArena(String id) {
        return arenas.get(id.toLowerCase());
    }

    public Arena getPlayerArena(Player player) {
        return playerArenaMap.get(player.getUniqueId());
    }

    public void assignPlayer(Player player, Arena arena) {
        playerArenaMap.put(player.getUniqueId(), arena);
        arena.addPlayer(player);
    }

    public void removePlayer(Player player) {
        Arena arena = playerArenaMap.remove(player.getUniqueId());
        if (arena != null) {
            arena.removePlayer(player);
        }
    }

    public Map<String, Arena> getArenas() {
        return Collections.unmodifiableMap(arenas);
    }
}