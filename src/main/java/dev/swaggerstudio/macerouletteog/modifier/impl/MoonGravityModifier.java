package dev.swaggerstudio.macerouletteog.modifier.impl;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import dev.swaggerstudio.macerouletteog.arena.Arena;
import dev.swaggerstudio.macerouletteog.modifier.RoundModifier;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.UUID;

public class MoonGravityModifier extends RoundModifier {

    private final double gravityMultiplier;
    private final MaceRouletteOG plugin;

    public MoonGravityModifier(String id, ConfigurationSection section) {
        super(id, section);
        this.gravityMultiplier = section.getDouble("gravity-multiplier", 0.25);
        this.plugin = MaceRouletteOG.getInstance();
    }

    @Override
    public void onRoundStart(Arena arena) {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            plugin.getFoliaScheduler().runAtEntity(player, () -> {
                AttributeInstance attr = player.getAttribute(Attribute.GRAVITY);
                if (attr != null) {
                    attr.setBaseValue(0.08 * gravityMultiplier);
                }
            });
        }
    }

    @Override
    public void onRoundEnd(Arena arena) {
        for (UUID uuid : arena.getPlayers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null) continue;
            plugin.getFoliaScheduler().runAtEntity(player, () -> {
                AttributeInstance attr = player.getAttribute(Attribute.GRAVITY);
                if (attr != null) {
                    attr.setBaseValue(0.08);
                }
            });
        }
    }
}