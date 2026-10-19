package dev.swaggerstudio.macerouletteog;

import dev.swaggerstudio.macerouletteog.arena.ArenaManager;
import dev.swaggerstudio.macerouletteog.arena.setup.SetupWandListener;
import dev.swaggerstudio.macerouletteog.combat.MaceCombatHandler;
import dev.swaggerstudio.macerouletteog.command.MaceCommand;
import dev.swaggerstudio.macerouletteog.command.SetupCommand;
import dev.swaggerstudio.macerouletteog.config.ConfigManager;
import dev.swaggerstudio.macerouletteog.config.MessageManager;
import dev.swaggerstudio.macerouletteog.hook.PapiExpansion;
import dev.swaggerstudio.macerouletteog.modifier.ModifierRegistry;
import dev.swaggerstudio.macerouletteog.scheduler.FoliaScheduler;
import dev.swaggerstudio.macerouletteog.storage.DatabaseManager;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public final class MaceRouletteOG extends JavaPlugin {

    private static MaceRouletteOG instance;

    private FoliaScheduler foliaScheduler;
    private ConfigManager configManager;
    private MessageManager messageManager;
    private ArenaManager arenaManager;
    private ModifierRegistry modifierRegistry;
    private DatabaseManager databaseManager;
    private SetupWandListener setupWandListener;

    private NamespacedKey tempDisplayKey;
    private NamespacedKey setupWandKey;

    @Override
    public void onEnable() {
        instance = this;
        long startTime = System.currentTimeMillis();

        this.tempDisplayKey = new NamespacedKey(this, "temp_display");
        this.setupWandKey = new NamespacedKey(this, "setup_wand");

        this.foliaScheduler = new FoliaScheduler(this);

        this.configManager = new ConfigManager(this);
        this.configManager.loadAll();
        this.messageManager = new MessageManager(this);

        this.databaseManager = new DatabaseManager(this);

        this.arenaManager = new ArenaManager(this);
        this.modifierRegistry = new ModifierRegistry(this);

        this.setupWandListener = new SetupWandListener(this);
        getServer().getPluginManager().registerEvents(setupWandListener, this);
        getServer().getPluginManager().registerEvents(new MaceCombatHandler(this), this);

        if (getCommand("maceroulette") != null) {
            MaceCommand maceCommand = new MaceCommand(this);
            getCommand("maceroulette").setExecutor(maceCommand);
            getCommand("maceroulette").setTabCompleter(maceCommand);
        }
        if (getCommand("mrogsetup") != null) {
            getCommand("mrogsetup").setExecutor(new SetupCommand(this, setupWandListener));
        }

        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new PapiExpansion(this).register();
            getLogger().info("PlaceholderAPI expansion registered successfully.");
        }

        getLogger().info("MaceRouletteOG enabled successfully in " + (System.currentTimeMillis() - startTime) + "ms! (Folia & Paper Ready)");
    }

    @Override
    public void onDisable() {
        if (databaseManager != null) {
            databaseManager.close();
        }
        getLogger().info("MaceRouletteOG disabled cleanly.");
    }

    public static MaceRouletteOG getInstance() {
        return instance;
    }

    public FoliaScheduler getFoliaScheduler() {
        return foliaScheduler;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public MessageManager getMessageManager() {
        return messageManager;
    }

    public ArenaManager getArenaManager() {
        return arenaManager;
    }

    public ModifierRegistry getModifierRegistry() {
        return modifierRegistry;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public NamespacedKey getTempDisplayKey() {
        return tempDisplayKey;
    }

    public NamespacedKey getSetupWandKey() {
        return setupWandKey;
    }
}