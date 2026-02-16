package me.ipapervn.vinecoins.utils;

import me.ipapervn.vinecoins.VineCoins;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class MessageUtils {

    private static FileConfiguration messagesConfig;
    private static File messagesFile;

    /**
     * Khởi tạo file messages.yml
     * Gọi hàm này trong onEnable của file VineCoins.java
     */
    public static void setup(VineCoins plugin) {
        messagesFile = new File(plugin.getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            // Tạo file mới từ tài nguyên trong Jar nếu chưa tồn tại
            plugin.saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    /**
     * Lấy tin nhắn và dịch mã màu (& -> §)
     * @param path Đường dẫn trong file messages.yml
     * @return Tin nhắn đã dịch màu
     */
    public static String getMsg(String path) {
        if (messagesConfig == null) return "§8[§bVineCoins§8] "; // Trả về prefix tạm nếu chưa load file

        // Nếu là prefix mà không tìm thấy trong file, trả về giá trị mặc định đẹp mắt
        if (path.equals("prefix")) {
            return ChatColor.translateAlternateColorCodes('&',
                    messagesConfig.getString("prefix", "&8[&bVine&fCoins&8] "));
        }

        String message = messagesConfig.getString(path);
        if (message == null) return "§cMissing: " + path;

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    /**
     * Lấy tin nhắn thô (Raw) không dịch mã màu
     */
    public static String getRawMsg(String path) {
        if (messagesConfig == null) return path;
        return messagesConfig.getString(path, "Missing: " + path);
    }

    /**
     * Hàm dùng để Reload lại tin nhắn khi dùng lệnh /vc reload
     */

    public static void reload(VineCoins plugin) {
        if (messagesFile != null) {
            messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        }
    }
}