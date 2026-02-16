package me.ipapervn.vinecoins.command;

import me.ipapervn.vinecoins.utils.MessageUtils;
import me.ipapervn.vinecoins.utils.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseSubCommand {

    // --- Các thông tin bắt buộc lớp con phải cung cấp ---
    public abstract String getName();
    public abstract String getPermissionKey();
    public abstract void perform(@NotNull CommandSender sender, @NotNull String[] args);

    // --- Hệ thống lấy tin nhắn tự động ---

    public String getSyntax() {
        return MessageUtils.getMsg("commands." + getName() + ".syntax");
    }

    public String getDescription() {
        return MessageUtils.getMsg("commands." + getName() + ".description");
    }

    /**
     * Lấy tin nhắn đã bao gồm Prefix từ messages.yml
     */
    protected String getMsg(String path) {
        String prefix = MessageUtils.getMsg("prefix");
        return prefix + MessageUtils.getMsg(path);
    }

    /**
     * LOGIC TAB-COMPLETE MẶC ĐỊNH
     * Tự động gợi ý tên người chơi ở tham số thứ 2 (args[1])
     */
    public List<String> getSubcommandArguments(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 2) {
            String input = args[1].toLowerCase();
            return Bukkit.getOnlinePlayers().stream()
                    // 1. Lọc trước (Filter): Loại bỏ người gửi và những người không khớp tên
                    .filter(player -> !player.getName().equals(sender.getName())
                            && player.getName().toLowerCase().startsWith(input))
                    // 2. Chuyển đổi sau (Map): Chỉ lấy tên của những người còn lại
                    .map(Player::getName)
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /**
     * KIỂM TRA QUYỀN VÀ SỐ LƯỢNG THAM SỐ
     * @param minArgs Số lượng tham số tối thiểu (tính cả tên lệnh con)
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean validateBasic(@NotNull CommandSender sender, @NotNull String[] args, int minArgs) {
        // 1. Kiểm tra quyền
        String permission = PermissionUtils.getPerm(getPermissionKey());
        if (!sender.hasPermission(permission)) {
            sender.sendMessage(getMsg("system.no-permission"));
            return false;
        }

        // 2. Kiểm tra số lượng tham số
        if (args.length < minArgs) {
            sender.sendMessage("§eSử dụng: §f" + getSyntax());
            return false;
        }
        return true;
    }

    /**
     * LẤY NGƯỜI CHƠI ONLINE (Tự báo lỗi nếu không tìm thấy)
     */
    protected @Nullable Player getTargetPlayer(CommandSender sender, String name) {
        Player target = Bukkit.getPlayer(name);
        if (target == null) {
            sender.sendMessage(getMsg("system.player-not-found").replace("%player%", name));
            return null;
        }
        return target;
    }

    /**
     * CHUYỂN ĐỔI VÀ KIỂM TRA SỐ TIỀN
     * @return -1 nếu số tiền không hợp lệ (nhập chữ hoặc số âm)
     */
    protected double parseAmount(CommandSender sender, String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                sender.sendMessage(getMsg("system.invalid-amount"));
                return -1;
            }
            return amount;
        } catch (NumberFormatException e) {
            sender.sendMessage(getMsg("system.invalid-amount"));
            return -1;
        }
    }
}