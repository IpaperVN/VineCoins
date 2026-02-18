package me.ipapervn.vinecoins.listeners;

import me.ipapervn.vinecoins.VineCoins;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final VineCoins plugin;

    public PlayerListener(VineCoins plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Kiểm tra và khởi tạo dữ liệu mặc định nếu là người chơi mới
        // Việc này giúp tránh lỗi Null hoặc dữ liệu trống khi check lần đầu
        if (!player.hasPlayedBefore()) {
            plugin.getDataManager().setBalance(uuid, 0.0, false); // Coin
            plugin.getDataManager().setBalance(uuid, 0.0, true);  // MCoin
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // Lưu dữ liệu ngay khi người chơi thoát để đảm bảo an toàn
        plugin.getDataManager().saveData();
    }
}