package me.ipapervn.vinecoins.utils;

import me.ipapervn.vinecoins.VineCoins;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.List;


public class MessageUtils {
    private static VineCoins plugin;
    public static void init(VineCoins instance) {
        plugin = instance; }
    public static List<String> getStringList(String path) {
        return plugin.getMessagesConfig().getStringList(path);
    }


    public static String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    /**
     * Lấy prefix từ file messages.yml
     */
    public static String getPrefix() {
        return VineCoins.getInstance().getMessage("prefix");
    }

    /**
     * Gửi tin nhắn từ messages.yml ra Console
     * @param path Đường dẫn trong file messages.yml
     */
    public static void logConfig(String path) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + VineCoins.getInstance().getMessage(path));
    }

    /**
     * Gửi tin nhắn văn bản thuần (kèm màu) ra Console
     */
    public static void logRaw(CommandSender sender, String text) {
        Bukkit.getConsoleSender().sendMessage(getPrefix() + color(text));
        sender.sendMessage(getPrefix() + color(text));
    }

    /**
     * Gửi tin nhắn từ messages.yml cho người gửi lệnh (Player hoặc Console)
     */
    public static void sendMessage(CommandSender sender, String path, String... placeholders) {
        String msg = VineCoins.getInstance().getMessage(path);

        // Nếu có placeholder, tiến hành thay thế từng cặp
        if (placeholders.length >= 2) {
            for (int i = 0; i < placeholders.length; i += 2) {
                if (i + 1 < placeholders.length) {
                    msg = msg.replace(placeholders[i], placeholders[i+1]);
                }
            }
        }

        sender.sendMessage(getPrefix() + color(msg));
    }


    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(message)));
    }
}