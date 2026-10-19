package dev.swaggerstudio.macerouletteog.config;

import dev.swaggerstudio.macerouletteog.MaceRouletteOG;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MessageManager {

    private final MaceRouletteOG plugin;
    private final MiniMessage miniMessage;

    public MessageManager(MaceRouletteOG plugin) {
        this.plugin = plugin;
        this.miniMessage = MiniMessage.miniMessage();
    }

    /**
     * Parses a raw MiniMessage string with optional tag resolvers.
     */
    public Component parse(String rawMessage, TagResolver... resolvers) {
        if (rawMessage == null) return Component.empty();
        return miniMessage.deserialize(rawMessage, resolvers);
    }

    /**
     * Sends a message from messages.yml to a command sender with prefix and tags.
     */
    public void send(CommandSender sender, String path, TagResolver... resolvers) {
        String prefix = plugin.getConfigManager().getMessages().getString("prefix", "");
        String rawMessage = plugin.getConfigManager().getMessages().getString(path, path);

        Component component = miniMessage.deserialize(prefix + rawMessage, resolvers);
        sender.sendMessage(component);
    }

    /**
     * Sends a raw message string directly parsed with MiniMessage.
     */
    public void sendRaw(CommandSender sender, String rawMessage, TagResolver... resolvers) {
        sender.sendMessage(miniMessage.deserialize(rawMessage, resolvers));
    }
}