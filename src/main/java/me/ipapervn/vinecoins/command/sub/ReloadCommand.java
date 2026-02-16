package me.ipapervn.vinecoins.command.sub;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.command.BaseSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand extends BaseSubCommand {
    private final VineCoins plugin;

    public ReloadCommand(VineCoins plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getPermissionKey() {
        return "admin.reload";
    }

    @Override
    public void perform(@NotNull CommandSender sender, @NotNull String[] args) {
        // 1. Thực hiện Logic Reload
        plugin.reloadConfig();
        plugin.getCoinsManager().getCurrencyManager().loadAll();

        // 2. Gửi tin nhắn
        if (sender instanceof Player player) {
            // Nếu là Player: Gửi tin nhắn vào Chat (có màu sắc)
            player.sendMessage(getMsg("system.reload"));

            // Ghi log vào Console 1 dòng duy nhất (dùng Logger của Bukkit)
            plugin.getLogger().info("Plugin đã được reload bởi " + player.getName());
        } else {
            // Nếu là Console: Chỉ in đúng 1 dòng này
            // Không dùng sender.sendMessage() ở đây để tránh lặp
            plugin.getLogger().info("Plugin đã được reload thành công!");
        }
    }
}