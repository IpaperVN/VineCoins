package me.ipapervn.vinecoins.mcoin;

import me.ipapervn.vinecoins.VineCoins;
import java.util.UUID;

public class MCoinManager {
    private final VineCoins plugin;
    public MCoinManager(VineCoins plugin) { this.plugin = plugin; }

    public double getBalance(UUID uuid) { return plugin.getDataManager().getBalance(uuid, true); }
    public void addBalance(UUID uuid, double amount) { plugin.getDataManager().addBalance(uuid, amount, true); }
    public void takeBalance(UUID uuid, double amount) { plugin.getDataManager().takeBalance(uuid, amount, true); }
    public void setBalance(UUID uuid, double amount) { plugin.getDataManager().setBalance(uuid, amount, true); }
    public void resetBalance(UUID uuid) {
        // Gọi xuống DataManager xử lý với tham số true (MCoin)
        plugin.getDataManager().resetBalance(uuid, true);
    }
}