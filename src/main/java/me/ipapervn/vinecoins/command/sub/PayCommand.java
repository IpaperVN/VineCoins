package me.ipapervn.vinecoins.command.sub;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.command.BaseSubCommand;
import me.ipapervn.vinecoins.currencies.CurrencyManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PayCommand extends BaseSubCommand {
    private final VineCoins plugin;

    public PayCommand(VineCoins plugin) {
        this.plugin = plugin;
    }

    @Override public String getName() { return "pay"; }
    @Override public String getPermissionKey() { return "user.pay"; }

    @Override
    public void perform(@NotNull CommandSender sender, @NotNull String[] args) {
        // 1. Chỉ người chơi mới có thể dùng lệnh chuyển tiền
        if (!(sender instanceof Player player)) {
            sender.sendMessage(getMsg("system.no-console"));
            return;
        }

        // 2. Kiểm tra quyền và cú pháp (/vc pay <người> <số tiền>)
        if (!validateBasic(sender, args, 3)) return;

        // 3. Lấy người nhận
        Player target = getTargetPlayer(sender, args[1]);
        if (target == null) return;

        // 4. Không cho phép tự chuyển tiền cho chính mình
        if (target.equals(player)) {
            player.sendMessage(getMsg("commands.pay.error.pay-self"));
            return;
        }

        // 5. Kiểm tra số tiền hợp lệ
        double amount = parseAmount(sender, args[2]);
        if (amount == -1) return;

        // 6. Kiểm tra số dư người gửi
        CurrencyManager economy = plugin.getCoinsManager().getCurrencyManager();

        // Thay thế đoạn (senderBalance < amount) bằng hasEnough
        if (!economy.hasEnough(player.getUniqueId(), amount)) {
            player.sendMessage(getMsg("commands.pay.error.not-enough-money"));
            return;
        }

        // 7. Thực hiện giao dịch
        economy.takeBalance(player.getUniqueId(), amount);
        economy.addBalance(target.getUniqueId(), amount);

        // 8. Thông báo thành công cho cả 2 bên
        player.sendMessage(getMsg("commands.pay.success.sender")
                .replace("%amount%", String.valueOf(amount))
                .replace("%target%", target.getName()));

        target.sendMessage(getMsg("commands.pay.success.receiver")
                .replace("%amount%", String.valueOf(amount))
                .replace("%player%", player.getName()));
    }
}