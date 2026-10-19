package dev.swaggerstudio.macerouletteog.hook;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PapiExpansion extends PlaceholderExpansion {

    private final MaceRouletteOG plugin;

    public PapiExpansion(MaceRouletteOG plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "macerouletteog";
    }

    @Override
    public @NotNull String getAuthor() {
        return "SwaggerStudio";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "";

        if (params.equalsIgnoreCase("arenas_count")) {
            return String.valueOf(plugin.getArenaManager().getArenas().size());
        }

        return null;
    }
}