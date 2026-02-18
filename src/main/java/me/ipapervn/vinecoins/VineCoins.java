package me.ipapervn.vinecoins;

import me.ipapervn.vinecoins.coin.CoinManager;
import me.ipapervn.vinecoins.listeners.PlayerListener;
import me.ipapervn.vinecoins.mcoin.MCoinManager;
import me.ipapervn.vinecoins.commands.MainCommand;
import me.ipapervn.vinecoins.data.DataManager;
import me.ipapervn.vinecoins.listeners.MobKillListener;

import me.ipapervn.vinecoins.utils.MessageUtils;
import me.ipapervn.vinecoins.utils.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public class VineCoins extends JavaPlugin {

    private static VineCoins instance;

    private DataManager dataManager;
    private CoinManager coinManager;
    private MCoinManager mCoinManager;
    private FileConfiguration messagesConfig;


    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        MessageUtils.init(this);
        // 1. Phải nạp file messages trước để có dữ liệu log
        setupMessages();
        //2. DataManager
        this.dataManager = new DataManager(this);
        this.dataManager.loadData();
        PermissionUtils.registerAll();
        getServer().getPluginManager().registerEvents(new MobKillListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // 3. Khởi tạo Logic Managers
        this.coinManager = new CoinManager(this);
        this.mCoinManager = new MCoinManager(this);

        // 4. Đăng ký Command
        MainCommand mainCommand = new MainCommand(this);
        Objects.requireNonNull(getCommand("vinecoins")).setExecutor(mainCommand);
        Objects.requireNonNull(getCommand("vinecoins")).setTabCompleter(mainCommand);
        // Lấy prefix và tin nhắn bật plugin từ file messages.yml
        MessageUtils.logConfig("plugin-enabled");
        startAutoSaveTask();
    }
    private org.bukkit.scheduler.BukkitTask autoSaveTask;
    public void startAutoSaveTask() {
        if (autoSaveTask != null) autoSaveTask.cancel();
        long interval = 20 * 60 * 10;
        autoSaveTask = Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            if (dataManager != null) {
                dataManager.saveData();
                MessageUtils.logConfig("plugin-auto-saved");
            }
        }, interval, interval);
    }
    public void loadMessagesConfig() {
        File messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public FileConfiguration getMessagesConfig() {
        return this.messagesConfig;
    }
    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.close();
        }

        // Lấy prefix và tin nhắn tắt plugin từ file messages.yml
        MessageUtils.logConfig("plugin-disabled");
    }

    public String getMessage(String path) {
        if (messagesConfig == null) return "Config Error!";
        // Lấy text, nếu path không tồn tại thì hiện path đó ra để dễ fix
        String msg = messagesConfig.getString(path, "Missing path: " + path);
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    /**
     * Thiết lập file messages.yml và xử lý thư mục an toàn
     */
    private void setupMessages() {
        if (!getDataFolder().exists()) {
            if (!getDataFolder().mkdirs()) {
                Bukkit.getLogger().warning("Could not create plugin directory!");
            }
        }

        // Khai báo trực tiếp tại đây thay vì để ở ngoài class
        File messagesFile = new File(getDataFolder(), "messages.yml");

        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
    }
    public void reloadPluginConfigs() {
        // 1. Reload file config.yml và messages.yml của Bukkit
        reloadConfig();
        loadMessagesConfig();

        // 2. Chỉ gọi nạp dữ liệu từ DataManager DUY NHẤT 1 lần
        if (this.dataManager != null) {
            this.dataManager.loadData(); // Dòng này in ra log YAML
        } else {
            this.dataManager = new DataManager(this);
            this.dataManager.loadData();
        }

        // 3. KHÔNG 'new' lại Manager nếu không cần thiết
        // Chỉ cần báo cho chúng biết dữ liệu đã thay đổi (nếu cần)

        // 4. Reset Task Auto-save
        startAutoSaveTask();

    }
    // --- Getters ---
    public static VineCoins getInstance() { return instance; }
    public DataManager getDataManager() { return dataManager; }
    public CoinManager getCoinManager() { return coinManager; }
    public MCoinManager getMCoinManager() { return mCoinManager; }
}