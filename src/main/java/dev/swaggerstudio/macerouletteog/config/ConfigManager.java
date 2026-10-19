package dev.swaggerstudio.macerouletteog.config;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ConfigManager {

    private final MaceRouletteOG plugin;

    private FileConfiguration config;
    private FileConfiguration messages;
    private FileConfiguration modifiers;

    private File messagesFile;
    private File modifiersFile;

    public ConfigManager(MaceRouletteOG plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        // 1. Load default config.yml
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        // 2. Load messages.yml
        this.messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }
        this.messages = YamlConfiguration.loadConfiguration(messagesFile);

        // 3. Load modifiers.yml
        this.modifiersFile = new File(plugin.getDataFolder(), "modifiers.yml");
        if (!modifiersFile.exists()) {
            plugin.saveResource("modifiers.yml", false);
        }
        this.modifiers = YamlConfiguration.loadConfiguration(modifiersFile);
    }

    public void reloadAll() {
        plugin.reloadConfig();
        this.config = plugin.getConfig();

        if (messagesFile.exists()) {
            this.messages = YamlConfiguration.loadConfiguration(messagesFile);
        }
        if (modifiersFile.exists()) {
            this.modifiers = YamlConfiguration.loadConfiguration(modifiersFile);
        }
        plugin.getLogger().info("All configurations reloaded successfully.");
    }

    public FileConfiguration getMainConfig() {
        return config;
    }

    public FileConfiguration getMessages() {
        return messages;
    }

    public FileConfiguration getModifiers() {
        return modifiers;
    }
}