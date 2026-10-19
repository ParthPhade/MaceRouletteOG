package dev.swaggerstudio.macerouletteog.scheduler;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class FoliaScheduler {

    private final MaceRouletteOG plugin;

    public FoliaScheduler(MaceRouletteOG plugin) {
        this.plugin = plugin;
    }

    /**
     * Runs a task immediately on the region thread owning the given Location.
     * Use this for modifying blocks or spawning Display Entities inside an Arena.
     */
    public void runAtLocation(Location location, Runnable runnable) {
        Bukkit.getRegionScheduler().execute(plugin, location, runnable);
    }

    /**
     * Runs a repeating task on the region thread owning the Arena's center Location.
     * Each Arena gets its own independent tick loop on Folia!
     */
    public ScheduledTask runTimerAtLocation(Location location, Consumer<ScheduledTask> task, long initialDelayTicks, long periodTicks) {
        return Bukkit.getRegionScheduler().runAtFixedRate(
                plugin,
                location,
                task,
                Math.max(1L, initialDelayTicks),
                Math.max(1L, periodTicks)
        );
    }

    /**
     * Runs a delayed task on the region thread owning the Location.
     */
    public ScheduledTask runLaterAtLocation(Location location, Consumer<ScheduledTask> task, long delayTicks) {
        return Bukkit.getRegionScheduler().runDelayed(
                plugin,
                location,
                task,
                Math.max(1L, delayTicks)
        );
    }

    /**
     * Runs a task safely on the thread owning a specific Entity/Player.
     * Use this whenever modifying Player inventory, health, scale attributes, or velocity.
     */
    public void runAtEntity(Entity entity, Runnable runnable) {
        entity.getScheduler().run(plugin, task -> runnable.run(), null);
    }

    /**
     * Runs a delayed task on the thread owning a specific Entity/Player.
     */
    public ScheduledTask runLaterAtEntity(Entity entity, Runnable runnable, long delayTicks) {
        return entity.getScheduler().runDelayed(
                plugin,
                task -> runnable.run(),
                null,
                Math.max(1L, delayTicks)
        );
    }

    /**
     * Runs database or heavy I/O work off the tick threads.
     */
    public void runAsync(Runnable runnable) {
        Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
    }

    /**
     * Runs a repeating async task (e.g., periodic stats auto-save).
     */
    public ScheduledTask runTimerAsync(Runnable runnable, long initialDelaySeconds, long periodSeconds) {
        return Bukkit.getAsyncScheduler().runAtFixedRate(
                plugin,
                task -> runnable.run(),
                initialDelaySeconds,
                periodSeconds,
                TimeUnit.SECONDS
        );
    }
}