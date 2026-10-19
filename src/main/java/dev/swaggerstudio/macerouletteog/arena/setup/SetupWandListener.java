package dev.swaggerstudio.macerouletteog.arena.setup;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class SetupWandListener implements Listener {

    private final MaceRouletteOG plugin;
    // Tracks active setup sessions per admin UUID
    private final Map<UUID, SetupSession> activeSessions = new ConcurrentHashMap<>();

    public SetupWandListener(MaceRouletteOG plugin) {
        this.plugin = plugin;
    }

    public void startSession(Player player, String arenaName) {
        activeSessions.put(player.getUniqueId(), new SetupSession(arenaName));
        plugin.getMessageManager().send(player, "setup.wand-given");
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        SetupSession session = activeSessions.get(player.getUniqueId());
        if (session == null) return;

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta() || !item.getItemMeta().getPersistentDataContainer().has(plugin.getSetupWandKey())) {
            return;
        }

        event.setCancelled(true);
        Block clickedBlock = event.getClickedBlock();
        if (clickedBlock == null) return;

        Location loc = clickedBlock.getLocation();

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            session.setPos1(loc);
            plugin.getMessageManager().send(player, "setup.bound-pos1", Placeholder.unparsed("loc", formatLoc(loc)));
        } else if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (player.isSneaking()) {
                session.setCenter(loc.add(0.5, 1.0, 0.5));
                plugin.getMessageManager().send(player, "setup.center-set", Placeholder.unparsed("loc", formatLoc(loc)));
            } else {
                session.setPos2(loc);
                plugin.getMessageManager().send(player, "setup.bound-pos2", Placeholder.unparsed("loc", formatLoc(loc)));
            }
        }
    }

    private String formatLoc(Location loc) {
        return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
    }

    public SetupSession getSession(UUID uuid) {
        return activeSessions.get(uuid);
    }

    public void endSession(UUID uuid) {
        activeSessions.remove(uuid);
    }
}