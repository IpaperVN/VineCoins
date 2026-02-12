package me.ipapervn.VineCoins;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class VineCoinsManager {

    private final VineCoins plugin;
    private File dataFile;
    private FileConfiguration dataConfig;

    public VineCoinsManager(@NotNull VineCoins plugin) {
        this.plugin = plugin;
        loadData();
    }

    /**
     * Khởi tạo và load file data.yml
     */
    public void loadData() {
        File folder = plugin.getDataFolder();
        if (!folder.exists() && !folder.mkdirs()) {
            plugin.getLogger().severe("Không thể tạo thư mục plugin! Vui lòng kiểm tra quyền ghi file.");
            return;
        }

        dataFile = new File(folder, "data.yml");

        if (!dataFile.exists()) {
            try {
                if (dataFile.createNewFile()) {
                    plugin.getLogger().info("Đã khởi tạo file dữ liệu data.yml mới.");
                }
            } catch (IOException e) {
                plugin.getLogger().severe("Không thể tạo file data.yml! Lỗi: " + e.getMessage());
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    /**
     * Lưu toàn bộ dữ liệu hiện tại xuống đĩa (Disk)
     */
    public void saveData() {
        if (dataConfig == null || dataFile == null) return;
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Không thể lưu dữ liệu vào data.yml!");
        }
    }

    /**
     * Lấy số điểm của người chơi
     */
    public int getCoins(@NotNull UUID uuid) {
        // Mặc định trả về 0 nếu chưa có dữ liệu
        return dataConfig.getInt("players." + uuid, 0);
    }

    /**
     * Đặt trực tiếp số điểm cho người chơi
     */
    public void setCoins(@NotNull UUID uuid, int amount) {
        dataConfig.set("players." + uuid, amount);
        saveData(); // Lưu ngay lập tức để tránh mất dữ liệu khi crash
    }

    /**
     * Cộng thêm điểm
     */
    public void addCoins(@NotNull UUID uuid, int amount) {
        setCoins(uuid, getCoins(uuid) + amount);
    }

    /**
     * Trừ điểm người chơi
     *
     * @return true nếu trừ thành công, false nếu không đủ điểm
     */
    public boolean takeCoins(@NotNull UUID uuid, int amount) {
        int current = getCoins(uuid);
        if (current >= amount) {
            setCoins(uuid, current - amount);
            return true;
        }
        return false;
    }

    public void resetAllPlayers() {
        // Xóa toàn bộ node "players" trong config
        dataConfig.set("players", null);
        saveData();
        // Load lại để đảm bảo dữ liệu trong bộ nhớ được cập nhật
        reloadData();
    }

    /**
     * Reload lại dữ liệu từ file (dùng cho lệnh reload nếu cần)
     */
    public void reloadData() {
        // Nếu file không tồn tại, tạo mới
        if (dataFile == null) {
            dataFile = new File(plugin.getDataFolder(), "data.yml");
        }
        // Đọc lại cấu hình từ file
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        plugin.getLogger().info("Dữ liệu VineCoins đã được tải lại từ file!");
    }
}