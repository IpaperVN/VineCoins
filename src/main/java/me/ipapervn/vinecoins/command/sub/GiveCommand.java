package me.ipapervn.vinecoins.command.sub;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.command.BaseSubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class GiveCommand extends BaseSubCommand {
    private final VineCoins plugin;

    public GiveCommand(VineCoins plugin) {
        this.plugin = plugin;
    }

    @Override public String getName() { return "give"; }
    @Override public String getPermissionKey() { return "admin.give"; }

    @Override
    public void perform(@NotNull CommandSender sender, @NotNull String[] args) {
        // 1. Kiểm tra quyền và số lượng tham số (/vc give <player> <amount>)
        // Nếu bạn đã đổi tên hàm theo gợi ý trước, hãy dùng hasBasicErrors(sender, args, 3)
        if (!validateBasic(sender, args, 3)) return;

        // 2. Lấy người chơi nhận tiền (hàm này đã tự báo lỗi nếu không tìm thấy)
        Player target = getTargetPlayer(sender, args[1]);
        if (target == null) return;

        // 3. Kiểm tra và lấy số tiền (hàm này đã tự báo lỗi nếu số tiền âm hoặc sai định dạng)
        double amount = parseAmount(sender, args[2]);
        if (amount == -1) return;

        // 4. Cộng tiền vào tài khoản
        plugin.getCoinsManager().getCurrencyManager().addBalance(target.getUniqueId(), amount);

        // 5. Gửi thông báo thành công cho người gửi (Admin)
        sender.sendMessage(getMsg("commands.give.success")
                .replace("%amount%", String.valueOf(amount))
                .replace("%player%", target.getName()));

        // 6. Gửi thông báo cho người nhận (nếu muốn)
        target.sendMessage(getMsg("commands.give.received")
                .replace("%amount%", String.valueOf(amount)));
    }
}