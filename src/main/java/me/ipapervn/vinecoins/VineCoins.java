package me.ipapervn.vinecoins;

import me.ipapervn.vinecoins.command.MainCommand;
import me.ipapervn.vinecoins.currencies.CurrencyManager;
import me.ipapervn.vinecoins.manager.VineCoinsManager;
import me.ipapervn.vinecoins.utils.MessageUtils;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class VineCoins extends JavaPlugin {

    private VineCoinsManager vineCoinsManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        MessageUtils.setup(this);

        this.vineCoinsManager = new VineCoinsManager(this);

        MainCommand mainCommand = new MainCommand(this);
        PluginCommand cmd = getCommand("vinecoins");

        if (cmd != null) {
            cmd.setExecutor(mainCommand);
            cmd.setTabCompleter(mainCommand);
        }

        getLogger().info(MessageUtils.getMsg("system.startup"));
    }

    @Override
    public void onDisable() {
        if (vineCoinsManager != null && vineCoinsManager.getCurrencyManager() != null) {
            vineCoinsManager.getCurrencyManager().saveAll();
            CurrencyManager cm = this.vineCoinsManager.getCurrencyManager();
                    // 1. Lưu toàn bộ dữ liệu từ RAM xuống SQLite
                    cm.saveAll();
                    cm.closeConnection();
                    getLogger().info("Da luu du lieu va dong ket noi SQLite an toan.");
                }
            }

            public VineCoinsManager getCoinsManager() {
                return vineCoinsManager;
            }
        }