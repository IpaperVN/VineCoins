package me.ipapervn.vinecoins.command.sub;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.command.BaseSubCommand;
import me.ipapervn.vinecoins.currencies.CurrencyManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class TakeCommand extends BaseSubCommand {
    private final VineCoins plugin;

    public TakeCommand(VineCoins plugin) {
        this.plugin = plugin;
    }

    @Override public String getName() { return "take"; }
    @Override public String getPermissionKey() { return "admin.take"; }

    @Override
    public void perform(@NotNull CommandSender sender, @NotNull String[] args) {
        // 1. Kiểm tra quyền và cú pháp (/vc take <player> <amount>)
        if (!validateBasic(sender, args, 3)) return;

        // 2. Lấy người chơi mục tiêu
        Player target = getTargetPlayer(sender, args[1]);
        if (target == null) return;

        // 3. Kiểm tra số tiền hợp lệ
        double amount = parseAmount(sender, args[2]);
        if (amount <= 0) return;

        // 4. Kiểm tra số dư hiện tại để tránh bị âm tiền (nếu bạn không muốn tiền âm)
        CurrencyManager economy = plugin.getCoinsManager().getCurrencyManager();
        double currentBalance = economy.getBalance(target.getUniqueId());

        if (currentBalance < amount) {
            // Nếu muốn trừ thẳng về 0 thay vì báo lỗi, bạn có thể đổi logic ở đây
            sender.sendMessage(getMsg("commands.take.error.not-enough")
                    .replace("%player%", target.getName())
                    .replace("%balance%", String.valueOf(currentBalance)));
            return;
        }

        // 5. Thực hiện trừ tiền
        economy.takeBalance(target.getUniqueId(), amount);

        // 6. Thông báo cho Admin
        sender.sendMessage(getMsg("commands.take.success")
                .replace("%amount%", String.valueOf(amount))
                .replace("%player%", target.getName()));

        // 7. Thông báo cho người chơi
        target.sendMessage(getMsg("commands.take.notified")
                .replace("%amount%", String.valueOf(amount)));
    }
}