package dev.swaggerstudio.macerouletteog.command;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import dev.swaggerstudio.macerouletteog.arena.Arena;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MaceCommand implements CommandExecutor, TabCompleter {

    private final MaceRouletteOG plugin;

    public MaceCommand(MaceRouletteOG plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            plugin.getMessageManager().sendRaw(sender, "<yellow>Usage: /mr <join|leave|reload></yellow>");
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("reload")) {
            if (!sender.hasPermission("macerouletteog.admin")) {
                plugin.getMessageManager().send(sender, "commands.no-permission");
                return true;
            }
            plugin.getConfigManager().reloadAll();
            plugin.getMessageManager().sendRaw(sender, "<green>MaceRouletteOG configurations reloaded successfully!");
            return true;
        }

        if (!(sender instanceof Player player)) {
            plugin.getMessageManager().send(sender, "commands.player-only");
            return true;
        }

        if (sub.equals("join")) {
            if (args.length < 2) {
                plugin.getMessageManager().sendRaw(player, "<red>Usage: /mr join <arena></red>");
                return true;
            }
            Arena arena = plugin.getArenaManager().getArena(args[1]);
            if (arena == null) {
                plugin.getMessageManager().send(player, "commands.arena-not-found", net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("arena", args[1]));
                return true;
            }
            plugin.getArenaManager().assignPlayer(player, arena);
            plugin.getMessageManager().send(player, "commands.arena-joined", net.kyori.adventure.text.minimessage.tag.resolver.Placeholder.unparsed("arena", arena.getId()));
        } else if (sub.equals("leave")) {
            Arena arena = plugin.getArenaManager().getPlayerArena(player);
            if (arena == null) {
                plugin.getMessageManager().sendRaw(player, "<red>You are not currently in an arena match.</red>");
                return true;
            }
            plugin.getArenaManager().removePlayer(player);
            plugin.getMessageManager().send(player, "commands.arena-left");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.add("join");
            completions.add("leave");
            if (sender.hasPermission("macerouletteog.admin")) {
                completions.add("reload");
            }
        } else if (args.length == 2 && args[0].equalsIgnoreCase("join")) {
            completions.addAll(plugin.getArenaManager().getArenas().keySet());
        }
        return completions;
    }
}