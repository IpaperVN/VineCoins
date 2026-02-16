package me.ipapervn.vinecoins.command.sub;

import me.ipapervn.vinecoins.command.BaseSubCommand;
import me.ipapervn.vinecoins.utils.PermissionUtils;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class HelpCommand extends BaseSubCommand {
    private final Collection<BaseSubCommand> commands;

    // Constructor nhận danh sách lệnh từ MainCommand
    public HelpCommand(Collection<BaseSubCommand> commands) {
        this.commands = commands;
    }

    @Override public String getName() { return "help"; }
    @Override public String getPermissionKey() { return "user.help"; }

    @Override
    public void perform(@NotNull CommandSender sender, @NotNull String[] args) {
        // Kiểm tra quyền cơ bản để xem help
        if (!validateBasic(sender, args, 1)) return;

        // Lấy các đoạn chat trang trí từ messages.yml nếu có, hoặc dùng mặc định
        sender.sendMessage("");
        sender.sendMessage("§b§l--- [ VINECOINS HELP ] ---");

        for (BaseSubCommand sub : commands) {
            // Chỉ hiển thị lệnh nếu người chơi có quyền
            String perm = PermissionUtils.getPerm(sub.getPermissionKey());
            if (sender.hasPermission(perm)) {
                // Hiển thị: /vc <syntax> - <description>
                sender.sendMessage("§e" + sub.getSyntax() + " §7- " + sub.getDescription());
            }
        }

        sender.sendMessage("§b§l--------------------------");
        sender.sendMessage("");
    }
}