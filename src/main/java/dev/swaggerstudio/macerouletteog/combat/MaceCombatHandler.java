package dev.swaggerstudio.macerouletteog.combat;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import dev.swaggerstudio.macerouletteog.arena.Arena;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;

public class MaceCombatHandler implements Listener {

    private final MaceRouletteOG plugin;

    public MaceCombatHandler(MaceRouletteOG plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMaceSmash(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;
        if (!(event.getDamager() instanceof Player damager)) return;

        Arena arena = plugin.getArenaManager().getPlayerArena(damager);
        if (arena == null || arena != plugin.getArenaManager().getPlayerArena(victim)) {
            return;
        }

        ItemStack item = damager.getInventory().getItemInMainHand();
        if (item.getType() != Material.MACE) return;

        if (damager.getFallDistance() <= 1.0f && !damager.isGliding()) {
            event.setCancelled(true);
            return;
        }

        plugin.getFoliaScheduler().runAtEntity(victim, () -> {
            victim.getWorld().playSound(victim.getLocation(), Sound.ITEM_MACE_SMASH_AIR, 1.0f, 1.0f);
        });

        arena.getAlivePlayers().remove(victim.getUniqueId());
        arena.removePlayer(victim);

        plugin.getMessageManager().send(damager, "game.wielder-success",
                net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("wielder", damager.getName()),
                net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("victim", victim.getName())
        );
    }
}