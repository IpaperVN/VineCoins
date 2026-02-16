package me.ipapervn.vinecoins.command.sub;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.command.BaseSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetCommand extends BaseSubCommand {
    private final VineCoins plugin;

    public SetCommand(VineCoins plugin) {
        this.plugin = plugin;
    }

    @Override public String getName() { return "set"; }
    @Override public String getPermissionKey() { return "admin.set"; }

    @Override
    public void perform(@NotNull CommandSender sender, @NotNull String[] args) {
        // 1. Kiểm tra quyền và cú pháp (/vc set <player> <amount>)
        if (!validateBasic(sender, args, 3)) return;

        // 2. Lấy người chơi mục tiêu
        Player target = getTargetPlayer(sender, args[1]);
        if (target == null) return;

        // 3. Kiểm tra số tiền hợp lệ (có thể là 0 nhưng không được âm)
        double amount = parseAmount(sender, args[2]);
        if (amount < 0) return; // parseAmount đã báo lỗi nếu không phải số

        // 4. Thiết lập số dư mới
        plugin.getCoinsManager().getCurrencyManager().setBalance(target.getUniqueId(), amount);

        // 5. Thông báo cho Admin
        sender.sendMessage(getMsg("commands.set.success")
                .replace("%player%", target.getName())
                .replace("%amount%", String.valueOf(amount)));

        // 6. Thông báo cho người chơi (nếu online)
        target.sendMessage(getMsg("commands.set.notified")
                .replace("%amount%", String.valueOf(amount)));
    }
}