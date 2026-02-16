package me.ipapervn.vinecoins.utils;

import me.ipapervn.vinecoins.VineCoins;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class PermissionUtils {

    private static FileConfiguration permConfig;

    public static void setup(VineCoins plugin) {
        File file = new File(plugin.getDataFolder(), "permissions.yml");
        if (!file.exists()) {
            plugin.saveResource("permissions.yml", false);
        }
        permConfig = YamlConfiguration.loadConfiguration(file);
    }

    /**
     * Lấy quyền từ file permissions.yml
     * @param path Đường dẫn (ví dụ: "admin.reload")
     * @return Chuỗi permission (ví dụ: "vinecoins.admin.reload")
     */
    public static String getPerm(String path) {
        if (permConfig == null) return "vinecoins.default"; // Quyền dự phòng
        return permConfig.getString(path, "vinecoins.default");
    }

    public static void reload() {
        // Hàm này sẽ được gọi khi bạn dùng lệnh /vc reload
        // Giúp cập nhật quyền mới mà không cần restart
        if (permConfig != null) {
            File file = new File(VineCoins.getPlugin(VineCoins.class).getDataFolder(), "permissions.yml");
            permConfig = YamlConfiguration.loadConfiguration(file);
        }
    }
}
