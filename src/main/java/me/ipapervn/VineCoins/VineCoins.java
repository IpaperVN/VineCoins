package me.ipapervn.VineCoins;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Objects;

public class VineCoins extends JavaPlugin {

    private VineCoinsManager coinsManager;

    @Override
    public void onEnable() {
        // Khởi tạo Manager và dữ liệu
        this.coinsManager = new VineCoinsManager(this);
        this.coinsManager.loadData();

        // Đăng ký lệnh
        VineCoinsCommand handler = new VineCoinsCommand(this);
        Objects.requireNonNull(getCommand("vinecoins")).setExecutor(handler);
        Objects.requireNonNull(getCommand("vinecoins")).setTabCompleter(handler);

        // Đăng ký PlaceholderAPI nếu có
        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new VineCoinsExpansion(this).register();
        }
    }

    @Override
    public void onDisable() {
        // LƯU DỮ LIỆU KHI SERVER TẮT HOẶC RELOAD
        if (coinsManager != null) {
            coinsManager.saveData();
        }
    }

    public VineCoinsManager getCoinsManager() {
        return coinsManager;
    }
}