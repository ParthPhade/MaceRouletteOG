package dev.swaggerstudio.macerouletteog.modifier;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import dev.swaggerstudio.macerouletteog.modifier.impl.*;
import org.bukkit.configuration.ConfigurationSection;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class ModifierRegistry {

    private final MaceRouletteOG plugin;
    private final List<RoundModifier> modifiers = new ArrayList<>();
    private final Deque<String> recentModifiers = new ArrayDeque<>();

    public ModifierRegistry(MaceRouletteOG plugin) {
        this.plugin = plugin;
        loadModifiers();
    }

    public void loadModifiers() {
        modifiers.clear();
        ConfigurationSection section = plugin.getConfigManager().getModifiers().getConfigurationSection("modifiers");
        if (section == null) return;

        for (String key : section.getKeys(false)) {
            ConfigurationSection sub = section.getConfigurationSection(key);
            if (sub == null || !sub.getBoolean("enabled", true)) continue;

            // Register specific modifier types based on key
            switch (key) {
                case "tiny_player" -> modifiers.add(new TinyPlayerModifier(key, sub));
                case "titan_mode" -> modifiers.add(new TitanModifier(key, sub));
                case "moon_gravity" -> modifiers.add(new MoonGravityModifier(key, sub));
                case "miss_equals_die" -> modifiers.add(new MissEqualsDieModifier(key, sub));
                default -> plugin.getLogger().warning("Unknown modifier key in modifiers.yml: " + key);
            }
        }
    }

    public RoundModifier rollRandom() {
        if (modifiers.isEmpty()) return null;

        // Calculate total weight excluding recent ones if possible
        int totalWeight = modifiers.stream()
                .filter(m -> !recentModifiers.contains(m.getId()))
                .mapToInt(RoundModifier::getWeight)
                .sum();

        if (totalWeight <= 0) {
            recentModifiers.clear();
            totalWeight = modifiers.stream().mapToInt(RoundModifier::getWeight).sum();
        }

        int randomValue = ThreadLocalRandom.current().nextInt(totalWeight);
        int currentWeightSum = 0;

        for (RoundModifier mod : modifiers) {
            if (recentModifiers.contains(mod.getId())) continue;
            currentWeightSum += mod.getWeight();
            if (randomValue < currentWeightSum) {
                pushRecent(mod.getId());
                return mod;
            }
        }

        return modifiers.get(0);
    }

    private void pushRecent(String id) {
        if (recentModifiers.size() >= 2) {
            recentModifiers.pollLast();
        }
        recentModifiers.push(id);
    }
}