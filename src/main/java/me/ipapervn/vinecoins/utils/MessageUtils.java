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
     * Gửi tin nhắn từ messages.yml cho người gửi lệnh (Player hoặc Console)
     */
    public static void sendMessage(CommandSender sender, String key, Object... replacements) {
        // 1. Lấy tin nhắn thô từ file messages.yml
        String msg = plugin.getMessagesConfig().getString("messages." + key);

        if (msg == null) return;

        // 2. Thực hiện thay thế Placeholder (Đây là bước bạn đang thiếu)
        // Cứ mỗi cặp (placeholder, giá trị) truyền vào sẽ được replace
        for (int i = 0; i < replacements.length; i += 2) {
            if (i + 1 < replacements.length) {
                String placeholder = replacements[i].toString();
                String value = replacements[i + 1].toString();
                msg = msg.replace(placeholder, value);
            }
        }

        // 3. Gửi tin nhắn đã được xử lý màu và placeholder
        sender.sendMessage(getPrefix() + color(msg));
    }


    public static void sendActionBar(Player player, String message) {
        if (player == null || message == null) return;
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(color(message)));
    }
}