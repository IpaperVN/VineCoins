package me.ipapervn.vinecoins.manager;

import me.ipapervn.vinecoins.VineCoins;
import java.util.UUID;

public class VineCoinsManager {
    private final VineCoins plugin;

    public VineCoinsManager(VineCoins plugin) {
        this.plugin = plugin;
    }

    public double getCoinBalance(UUID uuid) {
        return plugin.getDataManager().getBalance(uuid, false);
    }

    public void addCoin(UUID uuid, double amount) {
        plugin.getDataManager().addBalance(uuid, amount, false);
    }

    public void setCoin(UUID uuid, double amount) {
        plugin.getDataManager().setBalance(uuid, amount, false);
    }

    public void takeCoin(UUID uuid, double amount) {
        plugin.getDataManager().takeBalance(uuid, amount, false);
    }

    public void resetCoin(UUID uuid) {
        plugin.getDataManager().resetBalance(uuid, false);
    }

    public double getMCoinBalance(UUID uuid) {
        return plugin.getDataManager().getBalance(uuid, true);
    }

    public void addMCoin(UUID uuid, double amount) {
        plugin.getDataManager().addBalance(uuid, amount, true);
    }

    public void setMCoin(UUID uuid, double amount) {
        plugin.getDataManager().setBalance(uuid, amount, true);
    }

    public void takeMCoin(UUID uuid, double amount) {
        plugin.getDataManager().takeBalance(uuid, amount, true);
    }

    public void resetMCoin(UUID uuid) {
        plugin.getDataManager().resetBalance(uuid, true);
    }
}
