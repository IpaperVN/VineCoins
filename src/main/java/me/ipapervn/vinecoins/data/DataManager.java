package me.ipapervn.vinecoins.data;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.ipapervn.vinecoins.VineCoins;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class DataManager {
    private final VineCoins plugin;
    private final String storageType;

    // Cấu hình YAML
    private File dataFile;
    private FileConfiguration dataConfig;

    // Cấu hình Database
    private HikariDataSource dataSource;
    private final String tableName;

    public DataManager(VineCoins plugin) {
        this.plugin = plugin;
        this.storageType = plugin.getConfig().getString("storage-type", "YAML").toUpperCase();
        this.tableName = plugin.getConfig().getString("database.table-prefix", "vinecoins_") + "data";

        init();
    }

    private void init() {
        if (storageType.equals("MYSQL")) {
            setupDatabase();
        } else {
            loadYamlFile();
        }
    }
    public void loadData() {
        if (storageType.equals("YAML")) {
            // Code load file .yml
            plugin.getLogger().info("Da nạp du lieu tu file YAML.");
        } else {
            // Code kiem tra ket noi MySQL
            plugin.getLogger().info("Da thiet lap ket noi MySQL.");
        }
    }
    public void saveData() {
        if (storageType.equals("YAML")) {
            saveYaml(); // Hàm lưu file yml của bạn
        }
        // Neu la MySQL thi thuong da save truc tiep khi setBalance roi
    }
    // --- KHỞI TẠO YAML ---
    private void loadYamlFile() {
        File dataFolder = new File(plugin.getDataFolder(), "data");
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                plugin.getLogger().warning("Khong the tao thu muc 'data'!");
            }
        }

        dataFile = new File(dataFolder, "data.yml");
        if (!dataFile.exists()) {
            try {
                if (dataFile.createNewFile()) {
                    plugin.getLogger().info("Da tao moi tep data/data.yml");
                }
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Loi khi tao tep data.yml!", e);
            }
        }
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
    }

    // --- KHỞI TẠO DATABASE ---
    private void setupDatabase() {
        HikariConfig config = new HikariConfig();
        String host = plugin.getConfig().getString("database.host");
        String port = plugin.getConfig().getString("database.port");
        String db = plugin.getConfig().getString("database.database");

        config.setJdbcUrl("jdbc:mysql://" + host + ":" + port + "/" + db);
        config.setUsername(plugin.getConfig().getString("database.username"));
        config.setPassword(plugin.getConfig().getString("database.password"));

        // Tối ưu hóa kết nối
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        try {
            this.dataSource = new HikariDataSource(config);
            createTable();
            plugin.getLogger().info("Ket noi Database thanh cong!");
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Khong the ket noi Database! Vui long kiem tra lai config.", e);
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "coins DOUBLE DEFAULT 0, " +
                "mcoins DOUBLE DEFAULT 0)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Loi khi tao bang Database!", e);
        }
    }

    // --- CÁC HÀM GET/SET/TAKE/GIVE/RESET CHUNG ---
// Hàm Give (Cộng thêm tiền)
    public double getBalance(UUID uuid, boolean isMCoin) {
        String key = isMCoin ? "mcoins" : "coins";
        if (storageType.equals("MYSQL") && dataSource != null) {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT " + key + " FROM vinecoins_data WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) return rs.getDouble(key);
                }
            } catch (SQLException e) {
                plugin.getLogger().log(Level.SEVERE, "Loi SQL!", e);
            }
            return 0.0;
        }
        return dataConfig.getDouble(uuid.toString() + "." + key, 0.0);
    }

    public void setBalance(UUID uuid, double amount, boolean isMCoin) {
        String key = isMCoin ? "mcoins" : "coins";
        if (storageType.equals("MYSQL") && dataSource != null) {
            plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement(
                             "INSERT INTO " + tableName + " (uuid, " + key + ") VALUES (?, ?) ON DUPLICATE KEY UPDATE " + key + " = ?")) {
                    ps.setString(1, uuid.toString());
                    ps.setDouble(2, amount);
                    ps.setDouble(3, amount);
                    ps.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().log(Level.SEVERE, "Loi SQL!", e);
                }
            });
        } else {
            // Cập nhật vào bộ nhớ đệm
            dataConfig.set(uuid.toString() + "." + key, amount);
            // KHÔNG gọi saveYaml() ở đây để tránh lag server.
            // File sẽ được lưu bởi Task Auto-save 10 phút/lần hoặc khi tắt plugin.
        }
    }

    public void addBalance(UUID uuid, double amount, boolean isMCoin) {
        setBalance(uuid, getBalance(uuid, isMCoin) + amount, isMCoin);
    }

    public void takeBalance(UUID uuid, double amount, boolean isMCoin) {
        // 1. Lấy số dư hiện tại
        double current = getBalance(uuid, isMCoin);

        // 2. Tính toán (Không để âm)
        double result = current - amount;
        if (result < 0) result = 0;

        // 3. Cập nhật (Sử dụng hàm setBalance của bạn)
        setBalance(uuid, result, isMCoin);

        // 4. LOG ĐỂ KIỂM TRA (Debug)
        // plugin.getLogger().info("DEBUG: Player " + uuid + " bi tru " + amount + ". Con lai: " + result);
    }

    public void resetBalance(UUID uuid, boolean isMCoin) {
        setBalance(uuid, 0.0, isMCoin);
    }

    // Hàm reset TẤT CẢ (Hàm đang bị báo Never Used)
    public void resetBalance(UUID uuid) {
        resetBalance(uuid, false); // Reset Coin
        resetBalance(uuid, true);  // Reset MCoin
    }
    public void saveYaml() {
        if (dataConfig != null && dataFile != null) {
            try {
                dataConfig.save(dataFile);
            } catch (IOException e) {
                plugin.getLogger().log(Level.SEVERE, "Khong the luu data.yml!", e);
            }
        }
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}