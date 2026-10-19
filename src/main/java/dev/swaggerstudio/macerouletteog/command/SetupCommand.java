package dev.swaggerstudio.macerouletteog.command;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import dev.swaggerstudio.macerouletteog.arena.setup.SetupSession;
import dev.swaggerstudio.macerouletteog.arena.setup.SetupWandListener;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class SetupCommand implements CommandExecutor {

    private final MaceRouletteOG plugin;
    private final SetupWandListener wandListener;

    public SetupCommand(MaceRouletteOG plugin, SetupWandListener wandListener) {
        this.plugin = plugin;
        this.wandListener = wandListener;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Players only.");
            return true;
        }

        if (!player.hasPermission("macerouletteog.admin")) {
            plugin.getMessageManager().send(player, "commands.no-permission");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage("§cUsage: /mrogsetup <arenaName> [save]");
            return true;
        }

        String arenaName = args[1];

        if (args.length >= 3 && args[2].equalsIgnoreCase("save")) {
            SetupSession session = wandListener.getSession(player.getUniqueId());
            if (session == null || session.getCenter() == null || session.getPos1() == null || session.getPos2() == null) {
                player.sendMessage("§cError: You must configure Pos1, Pos2, and the Wheel Center using the Setup Wand first!");
                return true;
            }

            plugin.getArenaManager().saveArena(
                    arenaName,
                    session.getCenter(),
                    session.getLobby() != null ? session.getLobby() : player.getLocation(),
                    session.getSpectator() != null ? session.getSpectator() : player.getLocation(),
                    session.getPos1(),
                    session.getPos2()
            );

            wandListener.endSession(player.getUniqueId());
            plugin.getMessageManager().send(player, "setup.saved", net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("arena", arenaName));
            return true;
        }

        wandListener.startSession(player, arenaName);

        ItemStack wand = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = wand.getItemMeta();
        if (meta != null) {
            meta.displayName(plugin.getMessageManager().parse("<gold><bold>MaceRoulette Setup Wand</bold></gold>"));
            meta.getPersistentDataContainer().set(plugin.getSetupWandKey(), org.bukkit.persistence.PersistentDataType.BYTE, (byte) 1);
            wand.setItemMeta(meta);
        }

        player.getInventory().addItem(wand);
        player.sendMessage("§aSetup session started for arena: §e" + arenaName);
        return true;
    }
}