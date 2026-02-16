package me.ipapervn.vinecoins.currencies;

import me.ipapervn.vinecoins.VineCoins;
import java.io.File;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class CurrencyManager {

    private final VineCoins plugin;
    private final Map<UUID, Double> balances = new HashMap<>();
    private Connection connection;

    public CurrencyManager(VineCoins plugin) {
        this.plugin = plugin;
        initDatabase();
        loadAll();
    }

    private void initDatabase() {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            if (!dataFolder.mkdirs()) {
                plugin.getLogger().severe("KHÔNG THỂ tạo thư mục dữ liệu! Plugin sẽ không hoạt động đúng.");
                return;
            }
        }

        File dataFile = new File(dataFolder, "vinecoins.db");
        String url = "jdbc:sqlite:" + dataFile.getAbsolutePath();

        try {
            connection = DriverManager.getConnection(url);
            try (Statement statement = connection.createStatement()) {
                // Sử dụng WAL mode để SQLite hoạt động nhanh hơn và tránh lỗi "database is locked"
                statement.execute("PRAGMA journal_mode=WAL;");
                statement.execute("CREATE TABLE IF NOT EXISTS player_balances (" +
                        "uuid VARCHAR(36) PRIMARY KEY, " +
                        "balance DOUBLE NOT NULL DEFAULT 0)");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Lỗi nghiêm trọng khi khởi tạo SQLite!", e);
        }
    }

    // --- CÁC PHƯƠNG THỨC XỬ LÝ (TRÊN RAM) ---

    public double getBalance(UUID uuid) {
        return balances.getOrDefault(uuid, plugin.getConfig().getDouble("settings.default-balance", 0.0));
    }

    public void setBalance(UUID uuid, double amount) {
        balances.put(uuid, Math.max(0, amount));
    }

    public void addBalance(UUID uuid, double amount) {
        if (amount > 0) setBalance(uuid, getBalance(uuid) + amount);
    }

    public void takeBalance(UUID uuid, double amount) {
        if (amount > 0) setBalance(uuid, getBalance(uuid) - amount);
    }

    public boolean hasEnough(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }

    // --- CÁC PHƯƠNG THỨC LƯU TRỮ (SQLITE) ---

    /**
     * Tải dữ liệu từ SQL vào RAM khi khởi động
     */
    public void loadAll() {
        if (connection == null) return;

        String sql = "SELECT * FROM player_balances";
        try (PreparedStatement pstmt = connection.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            balances.clear();
            while (rs.next()) {
                try {
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    balances.put(uuid, rs.getDouble("balance"));
                } catch (IllegalArgumentException ignored) {}
            }
            plugin.getLogger().info("Đã tải " + balances.size() + " tài khoản từ SQLite.");
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Không thể tải dữ liệu từ database!", e);
        }
    }

    /**
     * Ghi đè toàn bộ dữ liệu từ RAM xuống SQL (Tối ưu bằng Transaction)
     */
    public void saveAll() {
        if (connection == null || balances.isEmpty()) return;

        String sql = "INSERT OR REPLACE INTO player_balances (uuid, balance) VALUES (?, ?)";
        try {
            connection.setAutoCommit(false); // Bắt đầu Transaction

            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                for (Map.Entry<UUID, Double> entry : balances.entrySet()) {
                    pstmt.setString(1, entry.getKey().toString());
                    pstmt.setDouble(2, entry.getValue());
                    pstmt.addBatch();
                }
                pstmt.executeBatch();
            }

            connection.commit(); // Hoàn tất ghi đĩa 1 lần duy nhất
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "LỖI LƯU DỮ LIỆU! Có thể mất dữ liệu người chơi.", e);
            try { connection.rollback(); } catch (SQLException ignored) {}
        } finally {
            try { connection.setAutoCommit(true); } catch (SQLException ignored) {}
        }
    }

    /**
     * Giải phóng tài nguyên
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                plugin.getLogger().info("Kết nối SQLite đã được đóng an toàn.");
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.WARNING, "Lỗi khi đóng kết nối SQLite.", e);
        }
    }
}