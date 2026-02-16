package me.ipapervn.vinecoins.command;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.command.sub.*;
import me.ipapervn.vinecoins.utils.MessageUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MainCommand implements CommandExecutor, TabCompleter {

    private final Map<String, BaseSubCommand> subcommands = new HashMap<>();
    private final String prefix;

    public MainCommand(VineCoins plugin) {
        // Lấy prefix một lần để dùng cho các thông báo lỗi chung
        this.prefix = MessageUtils.getMsg("prefix");
        setupCommands(plugin);
    }

    private void setupCommands(VineCoins plugin) {
        // 1. Đăng ký các lệnh chức năng
        subcommands.put("pay", new PayCommand(plugin));
        subcommands.put("give", new GiveCommand(plugin));
        subcommands.put("take", new TakeCommand(plugin));
        subcommands.put("set", new SetCommand(plugin));
        subcommands.put("reset", new ResetCommand(plugin));
        subcommands.put("reload", new ReloadCommand(plugin));

        // Thêm các lệnh khác vào đây...

        // 2. Đăng ký HelpCommand cuối cùng và truyền danh sách lệnh
        subcommands.put("help", new HelpCommand(subcommands.values()));
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Nếu chỉ gõ /vc -> hiện trang Help
        if (args.length == 0) {
            subcommands.get("help").perform(sender, args);
            return true;
        }

        // Kiểm tra xem lệnh con có tồn tại không
        BaseSubCommand sub = subcommands.get(args[0].toLowerCase());
        if (sub == null) {
            sender.sendMessage(prefix + MessageUtils.getMsg("system.command-not-found"));
            return true;
        }

        // Chạy lệnh con
        sub.perform(sender, args);
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        // Gợi ý ở vị trí đối số thứ nhất: /vc [Tab]
        if (args.length == 1) {
            return subcommands.keySet().stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        // Gợi ý ở các vị trí tiếp theo (args[1], args[2]...) dựa vào từng SubCommand
        if (args.length > 1) {
            BaseSubCommand sub = subcommands.get(args[0].toLowerCase());
            if (sub != null) {
                return sub.getSubcommandArguments(sender, args);
            }
        }

        return new ArrayList<>();
    }
}