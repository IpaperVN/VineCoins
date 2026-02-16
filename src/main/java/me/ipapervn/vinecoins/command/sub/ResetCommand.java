package me.ipapervn.vinecoins.command.sub;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.command.BaseSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ResetCommand extends BaseSubCommand {
    private final VineCoins plugin;

    public ResetCommand(VineCoins plugin) {
        this.plugin = plugin;
    }

    @Override public String getName() { return "reset"; }
    @Override public String getPermissionKey() { return "admin.reset"; }

    @Override
    public void perform(@NotNull CommandSender sender, @NotNull String[] args) {
        // 1. Kiểm tra quyền và cú pháp (/vc reset <player>)
        if (!validateBasic(sender, args, 2)) return;

        // 2. Lấy người chơi mục tiêu
        Player target = getTargetPlayer(sender, args[1]);
        if (target == null) return;

        // 3. Thực hiện reset (Đặt về 0 hoặc giá trị mặc định trong config)
        double defaultBalance = plugin.getConfig().getDouble("settings.default-balance", 0.0);
        plugin.getCoinsManager().getCurrencyManager().setBalance(target.getUniqueId(), defaultBalance);

        // 4. Thông báo cho Admin
        sender.sendMessage(getMsg("commands.reset.success")
                .replace("%player%", target.getName())
                .replace("%amount%", String.valueOf(defaultBalance)));

        // 5. Thông báo cho người bị reset (nếu online)
        target.sendMessage(getMsg("commands.reset.notified")
                .replace("%amount%", String.valueOf(defaultBalance)));
    }
}