package me.ipapervn.vinecoins.coin;

import me.ipapervn.vinecoins.VineCoins;
import java.util.UUID;

public class CoinManager {
    private final VineCoins plugin;
    public CoinManager(VineCoins plugin) { this.plugin = plugin; }

    public double getBalance(UUID uuid) { return plugin.getDataManager().getBalance(uuid, false); }
    public void addBalance(UUID uuid, double amount) { plugin.getDataManager().addBalance(uuid, amount, false); }
    public void setBalance(UUID uuid, double amount) { plugin.getDataManager().setBalance(uuid, amount, false); }
    public void takeBalance(UUID uuid, double amount) { plugin.getDataManager().takeBalance(uuid, amount, false); }
    public void resetBalance(UUID uuid) {
        // Gọi xuống DataManager xử lý với tham số false (Coin)
        plugin.getDataManager().resetBalance(uuid, false);
    }
}