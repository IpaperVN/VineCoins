package me.ipapervn.VineCoins;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class VineCoinsCommand implements CommandExecutor, TabCompleter {

    private final VineCoins plugin;

    public VineCoinsCommand(VineCoins plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        // 1. Xem tiền bản thân
        if (args.length == 0) {
            if (sender instanceof Player p) {
                int pts = plugin.getCoinsManager().getCoins(p.getUniqueId());
                p.sendMessage(ChatColor.GREEN + "Số dư hiện tại: " + ChatColor.GOLD + pts + " Coins");
            } else {
                sender.sendMessage(ChatColor.RED + "Console vui lòng dùng: /vc help");
            }
            return true;
        }

        String sub = args[0].toLowerCase();

        // Các lệnh yêu cầu quyền ADMIN
        if (Arrays.asList("give", "take", "set", "reset", "look", "reload").contains(sub)) {
            if (!sender.hasPermission("vinecoins.admin")) {
                sender.sendMessage(ChatColor.RED + "Lỗi: Bạn không có quyền quản trị VineCoins.");
                return true;
            }
        }

        switch (sub) {
            case "help" -> sendHelp(sender);
            case "reload" -> handleReload(sender);
            case "reset" -> handleReset(sender, args);
            case "give", "take", "set" -> handleAdminAction(sender, sub, args);
            case "pay" -> handlePay(sender, args);
            case "look" -> handleLook(sender, args);
            default -> sender.sendMessage(ChatColor.RED + "Không tìm thấy lệnh này. Dùng /vc help");
        }
        return true;
    }

    // --- Xử lý Gợi ý (Tab Complete) ---
    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> subCommands = new ArrayList<>(Arrays.asList("pay", "help"));
            if (sender.hasPermission("vinecoins.admin")) {
                subCommands.addAll(Arrays.asList("give", "take", "set", "look", "reset", "reload"));
            }
            return subCommands.stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            // Trả về null để Spigot gợi ý danh sách người chơi online
            return null;
        }

        return new ArrayList<>();
    }

    // --- Các hàm hỗ trợ (Helper Methods) ---

    private void sendHelp(CommandSender s) {
        s.sendMessage(ChatColor.YELLOW + "===== VineCoins Help =====");
        s.sendMessage(ChatColor.AQUA + "/vc pay <tên> <số tiền>" + ChatColor.WHITE + " - Chuyển tiền");
        if (s.hasPermission("vinecoins.admin")) {
            s.sendMessage(ChatColor.RED + "/vc give/take/set <tên> <số tiền>" + ChatColor.WHITE + " - Quản trị");
            s.sendMessage(ChatColor.RED + "/vc look <tên>" + ChatColor.WHITE + " - Xem tiền người khác");
        }
    }

    private void handleAdminAction(CommandSender sender, String sub, String[] args) {
        if (!sender.hasPermission("vinecoins.admin")) {
            sender.sendMessage(ChatColor.RED + "Bạn không có quyền thực hiện lệnh admin.");
            return;
        }
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Sử dụng: /vc " + sub + " <player> <amount>");
        }
    }
    // Hàm xử lý Reload
    private void handleReload(@NotNull CommandSender sender) {
        if (!sender.hasPermission("vinecoins.admin")) {
            sender.sendMessage(ChatColor.RED + "Bạn không có quyền reload plugin!");
            return;
        }

        // Gọi hàm reload từ Manager
        plugin.getCoinsManager().reloadData();

        // Nếu bạn có file config.yml (để chỉnh message), hãy thêm plugin.reloadConfig() ở đây

        sender.sendMessage(ChatColor.GREEN + "[VineCoins] Plugin và dữ liệu đã được tải lại thành công!");
    }
    @SuppressWarnings("deprecation")
    private void handleReset(@NotNull CommandSender sender, @NotNull String[] args) {
        // 1. Kiểm tra quyền Admin (Cực kỳ quan trọng)
        if (!sender.hasPermission("vinecoins.admin")) {
            sender.sendMessage(ChatColor.RED + "Bạn không có quyền thực hiện lệnh reset này!");
            return;
        }

        // 2. Kiểm tra xem có nhập tên người chơi không
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Sử dụng: /vc reset <tên_người_chơi>");
            return;
        }

        // 3. Xử lý Reset
        String targetName = args[1];

        // Nếu muốn reset toàn bộ server
        if (targetName.equalsIgnoreCase("all")) {
            plugin.getCoinsManager().resetAllPlayers();
            Bukkit.broadcastMessage(ChatColor.RED + " [VineCoins] " + ChatColor.YELLOW + "Toàn bộ số dư của server đã được reset bởi Admin!");
            return;
        }

        // Nếu reset một người chơi cụ thể
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);

        // Kiểm tra xem người chơi này đã từng vào server chưa (tránh tạo rác trong data)
        if (!target.hasPlayedBefore() && !target.isOnline()) {
            sender.sendMessage(ChatColor.RED + "Người chơi này chưa từng tham gia server!");
            return;
        }

        plugin.getCoinsManager().setCoins(target.getUniqueId(), 0);
        sender.sendMessage(ChatColor.GREEN + "Đã reset VineCoins của " + ChatColor.WHITE + target.getName() + ChatColor.GREEN + " về 0.");

        // Gửi thông báo cho người đó nếu họ đang online
        if (target.isOnline() && target.getPlayer() != null) {
            target.getPlayer().sendMessage(ChatColor.RED + "Số dư VineCoins của bạn đã được reset về 0 bởi Admin.");
        }
    }
    @SuppressWarnings("deprecation")
    private void handleLook(CommandSender sender, String[] args) {
        if (!sender.hasPermission("vinecoins.admin")) {
            sender.sendMessage(ChatColor.RED + "Không có quyền.");
            return;
        }
        if (args.length < 2) return;
        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        int points = plugin.getCoinsManager().getCoins(target.getUniqueId());
        sender.sendMessage(ChatColor.GREEN + "Người chơi " + target.getName() + " có " + ChatColor.GOLD + points + " CPoints");
    }
    @SuppressWarnings("deprecation")
    private void handlePay(CommandSender sender, String[] args) {
        if (!(sender instanceof Player p)) {
            sender.sendMessage("Console không thể chuyển tiền!");
            return;
        }
        if (args.length < 3) {
            p.sendMessage(ChatColor.RED + "Dùng: /cp pay <tên> <số tiền>");
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[1]);
        if (target.getUniqueId().equals(p.getUniqueId())) {
            p.sendMessage(ChatColor.RED + "Bạn không thể tự chuyển cho mình.");
            return;
        }

        try {
            int amount = Integer.parseInt(args[2]);
            if (amount <= 0) {
                p.sendMessage(ChatColor.RED + "Số tiền phải lớn hơn 0.");
                return;
            }

            if (plugin.getCoinsManager().takeCoins(p.getUniqueId(), amount)) {
                plugin.getCoinsManager().addCoins(target.getUniqueId(), amount);
                p.sendMessage(ChatColor.GREEN + "Đã chuyển " + amount + " cho " + target.getName());
                if (target.isOnline() && target.getPlayer() != null) {
                    target.getPlayer().sendMessage(ChatColor.GREEN + "Bạn nhận được " + amount + " từ " + p.getName());
                }
            } else {
                p.sendMessage(ChatColor.RED + "Bạn không đủ tiền.");
            }
        } catch (NumberFormatException e) {
            p.sendMessage(ChatColor.RED + "Số tiền không hợp lệ.");
        }
    }
}