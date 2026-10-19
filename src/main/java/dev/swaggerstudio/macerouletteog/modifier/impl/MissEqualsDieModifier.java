package dev.swaggerstudio.macerouletteog.modifier.impl;

import dev.swaggerstudio.macerouletteog.arena.Arena;
import dev.swaggerstudio.macerouletteog.modifier.RoundModifier;
import org.bukkit.configuration.ConfigurationSection;

public class MissEqualsDieModifier extends RoundModifier {

    public MissEqualsDieModifier(String id, ConfigurationSection section) {
        super(id, section);
    }

    @Override
    public void onRoundStart(Arena arena) {
        // Flags the arena state or metadata that if the wielder hits ground with fall > 5 and no hit, they self-eliminate
    }

    @Override
    public void onRoundEnd(Arena arena) {
        // Cleanup listener flags if necessary
    }
}