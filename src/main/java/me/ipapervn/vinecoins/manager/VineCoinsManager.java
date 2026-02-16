package me.ipapervn.vinecoins.manager;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.currencies.CurrencyManager;

public class VineCoinsManager {

    private final CurrencyManager currencyManager;

    public VineCoinsManager(VineCoins plugin) {
        // Truyen instance cua plugin vao CurrencyManager
        this.currencyManager = new CurrencyManager(plugin);
    }

    public CurrencyManager getCurrencyManager() {
        return this.currencyManager;
    }
}