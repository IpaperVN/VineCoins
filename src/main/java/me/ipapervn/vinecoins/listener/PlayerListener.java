package me.ipapervn.vinecoins.listener;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.currencies.CurrencyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final VineCoins plugin;

    public PlayerListener(VineCoins plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        CurrencyManager manager = plugin.getCoinsManager().getCurrencyManager();

        // Kiểm tra xem người chơi đã có dữ liệu trong Database/HashMap chưa
        // Nếu getBalance trả về giá trị mặc định (trong CurrencyManager thường là 0)
        // và bạn muốn chắc chắn họ được khởi tạo từ config:

        if (!event.getPlayer().hasPlayedBefore()) {
            double defaultBalance = plugin.getConfig().getDouble("currency.default-balance", 0.0);
            manager.setBalance(uuid, defaultBalance);
        }
    }
}