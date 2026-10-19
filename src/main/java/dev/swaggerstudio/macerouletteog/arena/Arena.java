package dev.swaggerstudio.macerouletteog.arena;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Arena {

    private final MaceRouletteOG plugin;
    private final String id;
    private final Location centerLocation;
    private final Location lobbyLocation;
    private final Location spectatorLocation;
    private final Location pos1;
    private final Location pos2;

    private final Set<UUID> players = ConcurrentHashMap.newKeySet();
    private final Set<UUID> alivePlayers = ConcurrentHashMap.newKeySet();
    private final BlockRollbackTracker rollbackTracker;

    private ArenaState state = ArenaState.WAITING;
    private ScheduledTask arenaTask;
    private int timerTicks = 0;
    private BossBar bossBar;

    public Arena(MaceRouletteOG plugin, String id, Location centerLocation, Location lobbyLocation, Location spectatorLocation, Location pos1, Location pos2) {
        this.plugin = plugin;
        this.id = id;
        this.centerLocation = centerLocation;
        this.lobbyLocation = lobbyLocation != null ? lobbyLocation : centerLocation;
        this.spectatorLocation = spectatorLocation != null ? spectatorLocation : centerLocation;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.rollbackTracker = new BlockRollbackTracker();

        this.bossBar = BossBar.bossBar(
                Component.text("MaceRouletteOG - Waiting for players"),
                1.0f,
                BossBar.Color.YELLOW,
                BossBar.Overlay.PROGRESS
        );
    }

    public void startLoop() {
        if (arenaTask != null) return;

        // Run independent tick loop on the exact chunk region owning the arena center
        this.arenaTask = plugin.getFoliaScheduler().runTimerAtLocation(centerLocation, task -> tick(), 1L, 1L);
    }

    public void stopLoop() {
        if (arenaTask != null) {
            arenaTask.cancel();
            arenaTask = null;
        }
    }

    private void tick() {
        timerTicks++;

        switch (state) {
            case WAITING -> {
                if (players.size() >= plugin.getConfigManager().getMainConfig().getInt("settings.min-players", 2)) {
                    transitionTo(ArenaState.STARTING);
                }
            }
            case STARTING -> {
                int countdownSeconds = plugin.getConfigManager().getMainConfig().getInt("settings.lobby-countdown", 10);
                int ticksNeeded = countdownSeconds * 20;

                if (players.size() < plugin.getConfigManager().getMainConfig().getInt("settings.min-players", 2)) {
                    transitionTo(ArenaState.WAITING);
                    return;
                }

                if (timerTicks >= ticksNeeded) {
                    transitionTo(ArenaState.SPINNING);
                }
            }
            case SPINNING -> {
                int spinDurationTicks = plugin.getConfigManager().getMainConfig().getInt("settings.spin-duration", 4) * 20;
                if (timerTicks >= spinDurationTicks) {
                    // TODO: Trigger Roulette Wheel selection & launch wielder(s)
                    transitionTo(ArenaState.ACTIVE_ROUND);
                }
            }
            case ACTIVE_ROUND -> {
                int roundDurationTicks = plugin.getConfigManager().getMainConfig().getInt("settings.round-duration", 15) * 20;
                if (timerTicks >= roundDurationTicks || alivePlayers.size() <= 1) {
                    transitionTo(ArenaState.RESETTING);
                }
            }
            case RESETTING -> {
                rollbackTracker.rollback();
                players.clear();
                alivePlayers.clear();
                transitionTo(ArenaState.WAITING);
                stopLoop();
            }
            case DISABLED -> {
                stopLoop();
            }
        }
    }

    public void addPlayer(Player player) {
        players.add(player.getUniqueId());
        alivePlayers.add(player.getUniqueId());
        player.hideBossBar(bossBar);
        player.showBossBar(bossBar);

        player.teleportAsync(lobbyLocation).thenAccept(success -> {
            if (success && state == ArenaState.WAITING) {
                startLoop();
            }
        });
    }

    public void removePlayer(Player player) {
        players.remove(player.getUniqueId());
        alivePlayers.remove(player.getUniqueId());
        player.hideBossBar(bossBar);
        player.teleportAsync(spectatorLocation);

        if (players.isEmpty() && state != ArenaState.WAITING) {
            transitionTo(ArenaState.RESETTING);
        }
    }

    private void transitionTo(ArenaState newState) {
        this.state = newState;
        this.timerTicks = 0;
    }

    public String getId() { return id; }
    public ArenaState getState() { return state; }
    public Set<UUID> getPlayers() { return players; }
    public Set<UUID> getAlivePlayers() { return alivePlayers; }
    public Location getCenterLocation() { return centerLocation; }
}