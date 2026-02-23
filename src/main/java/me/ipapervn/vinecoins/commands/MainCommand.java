package me.ipapervn.vinecoins.commands;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.utils.MessageUtils;
import me.ipapervn.vinecoins.utils.PermissionUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MainCommand implements CommandExecutor, TabCompleter {
    private final VineCoins plugin;

    public MainCommand(VineCoins plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "reload":
                if (!sender.hasPermission(PermissionUtils.RELOAD)) {
                    MessageUtils.sendMessage(sender, "no-permission");
                    return true;
                }
                plugin.reloadPluginConfigs();
                MessageUtils.sendMessage(sender, "plugin-reload-success");
                return true;

            case "reset":
                if (!sender.hasPermission(PermissionUtils.ADMIN)) {
                    MessageUtils.sendMessage(sender, "no-permission");
                    return true;
                }
                handleReset(sender, args);
                return true;

            case "resetall":
                if (!sender.hasPermission(PermissionUtils.ADMIN)) {
                    MessageUtils.sendMessage(sender, "no-permission");
                    return true;
                }
                handleResetAll(sender, args);
                return true;

            case "coin":
            case "mcoin":
                if (args.length < 2) {
                    sendHelp(sender);
                    return true;
                }
                boolean isMCoin = sub.equals("mcoin");
                String action = args[1].toLowerCase();

                switch (action) {
                    case "check":
                        handleCheck(sender, args, isMCoin);
                        break;
                    case "add":
                    case "give":
                        handleModify(sender, args, isMCoin, "add");
                        break;
                    case "set":
                        handleModify(sender, args, isMCoin, "set");
                        break;
                    case "take":
                        handleModify(sender, args, isMCoin, "take");
                        break;
                    default:
                        sendHelp(sender);
                        break;
                }
                return true;

            default:
                sendHelp(sender);
                return true;
        }
    }

    private void handleCheck(CommandSender sender, String[] args, boolean isMCoin) {
        Player target = (args.length == 2 && sender instanceof Player) ? (Player) sender : (args.length >= 3 ? Bukkit.getPlayer(args[2]) : null);

        if (target == null) {
            MessageUtils.sendMessage(sender, "player-not-found");
            return;
        }

        double bal = isMCoin ? plugin.getVineCoinsManager().getMCoinBalance(target.getUniqueId())
                : plugin.getVineCoinsManager().getCoinBalance(target.getUniqueId());

        MessageUtils.sendMessage(sender, "balance-check",
                "%type%", (isMCoin ? "MCoin" : "Coin"),
                "%player%", target.getName(),
                "%amount%", String.valueOf(bal));
    }

    private void handleModify(CommandSender sender, String[] args, boolean isMCoin, String type) {
        if (!sender.hasPermission(PermissionUtils.ADMIN)) {
            MessageUtils.sendMessage(sender, "no-permission");
            return;
        }
        if (args.length < 4) {
            MessageUtils.sendMessage(sender, "invalid-usage");
            return;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            MessageUtils.sendMessage(sender, "player-not-found");
            return;
        }

        try {
            double amount = Double.parseDouble(args[3]);
            UUID uuid = target.getUniqueId();
            String typeName = isMCoin ? "MCoin" : "Coin";

            switch (type) {
                case "add":
                    if (isMCoin) {
                        plugin.getVineCoinsManager().addMCoin(uuid, amount);
                        MessageUtils.sendMessage(target, "messages.receive-coin",
                                "%amount%", String.valueOf(amount),
                                "%balance%", String.valueOf(plugin.getVineCoinsManager().getCoinBalance(uuid))
                        );
                    } else {
                        plugin.getVineCoinsManager().addCoin(uuid, amount);
                        MessageUtils.sendMessage(target, "messages.receive-coin",
                                "%amount%", String.valueOf(amount),
                                "%balance%", String.valueOf(plugin.getVineCoinsManager().getCoinBalance(uuid)));
                    }

                    MessageUtils.sendMessage(sender, "admin-add-success",
                            "%amount%", String.valueOf(amount), "%type%", typeName, "%player%", target.getName());

                    MessageUtils.sendMessage(sender, "admin-add-success",
                            "%amount%", String.valueOf(amount), "%type%", typeName, "%player%", target.getName());

                    break;

                case "set":
                    if (isMCoin) {
                        plugin.getVineCoinsManager().setMCoin(uuid, amount);
                        MessageUtils.sendMessage(target, "messages.set-mcoin",
                                "%balance%", String.valueOf(amount));
                    } else {
                        plugin.getVineCoinsManager().setCoin(uuid, amount);
                        MessageUtils.sendMessage(target, "messages.set-coin",
                                "%balance%", String.valueOf(amount));
                    }

                    MessageUtils.sendMessage(sender, "admin-set-success",
                            "%amount%", String.valueOf(amount), "%type%", typeName, "%player%", target.getName());
                    break;

                case "take":
                    if (isMCoin) {
                        plugin.getVineCoinsManager().takeMCoin(uuid, amount);
                        MessageUtils.sendMessage(target, "messages.lost-mcoin",
                                "%amount%", String.valueOf(amount),
                                "%balance%", String.valueOf(plugin.getVineCoinsManager().getMCoinBalance(uuid)));
                    } else {
                        plugin.getVineCoinsManager().takeCoin(uuid, amount);
                        MessageUtils.sendMessage(target, "messages.lost-coin",
                                "%amount%", String.valueOf(amount),
                                "%balance%", String.valueOf(plugin.getVineCoinsManager().getCoinBalance(uuid)));
                    }

                    MessageUtils.sendMessage(sender, "admin-take-success",
                            "%amount%", String.valueOf(amount), "%type%", typeName, "%player%", target.getName());
                    break;
            }
        } catch (NumberFormatException e) {
            MessageUtils.sendMessage(sender, "invalid-amount");
        }
    }

    private void handleReset(CommandSender sender, String[] args) {
        if (args.length < 2) {
            MessageUtils.sendMessage(sender, "invalid-usage");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            MessageUtils.sendMessage(sender, "player-not-found");
            return;
        }

        UUID uuid = target.getUniqueId();

        // /vc reset <player> -> Reset cả hai
        if (args.length == 2) {
            plugin.getDataManager().resetBalance(uuid);
            sender.sendMessage("§a[VineCoins] Đã reset tất cả tiền của " + target.getName());
        }
        // /vc reset <player> <type> -> Reset riêng lẻ
        else {
            String type = args[2].toLowerCase();
            if (type.equals("mcoin")) {
                plugin.getVineCoinsManager().resetMCoin(uuid);
                sender.sendMessage("§a[VineCoins] Đã reset MCoin của " + target.getName());
            } else {
                plugin.getVineCoinsManager().resetCoin(uuid);
                sender.sendMessage("§a[VineCoins] Đã reset Coin của " + target.getName());
            }
        }
    }

    private void handleResetAll(CommandSender sender, String[] args) {
        // /vc resetall -> Reset cả coin và mcoin của tất cả
        if (args.length == 1) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                plugin.getDataManager().resetBalance(p.getUniqueId());
            }
            sender.sendMessage("§a[VineCoins] Đã reset tất cả tiền của tất cả người chơi!");
        }
        // /vc resetall <type> -> Reset riêng coin hoặc mcoin
        else {
            String type = args[1].toLowerCase();
            for (Player p : Bukkit.getOnlinePlayers()) {
                if (type.equals("mcoin")) {
                    plugin.getVineCoinsManager().resetMCoin(p.getUniqueId());
                } else {
                    plugin.getVineCoinsManager().resetCoin(p.getUniqueId());
                }
            }
            sender.sendMessage("§a[VineCoins] Đã reset " + (type.equals("mcoin") ? "MCoin" : "Coin") + " của tất cả người chơi!");
        }
    }

    private void sendHelp(CommandSender sender) {
        List<String> helpMessages = MessageUtils.getStringList("command-usage");
        // Chỉ kiểm tra isEmpty() vì getStringList không bao giờ null
        if (helpMessages.isEmpty()) {
            sender.sendMessage("§6§lVINE COINS §7- §cConfig 'command-usage' bị thiếu hoặc trống!");
            // Gửi lệnh cơ bản để Console/Player vẫn thấy cái gì đó
            sender.sendMessage("§e/vc reload §7- Reload plugin");
            sender.sendMessage("§e/vc coin/mcoin check <player> §7- Xem số dư");
            return;
        }

        for (String s : helpMessages) {
            sender.sendMessage(MessageUtils.color(s));
        }
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender s, @NotNull Command c, @NotNull String a, @NotNull String[] args) {
        if (args.length == 1) {
            return StringUtil.copyPartialMatches(args[0], Arrays.asList("coin", "mcoin", "reload", "reset", "resetall"), new ArrayList<>());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("coin") || args[0].equalsIgnoreCase("mcoin")) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("add", "set", "check", "take"), new ArrayList<>());
            }
            if (args[0].equalsIgnoreCase("reset")) {
                return null;
            }
            if (args[0].equalsIgnoreCase("resetall")) {
                return StringUtil.copyPartialMatches(args[1], Arrays.asList("coin", "mcoin"), new ArrayList<>());
            }
        }

        if (args.length == 3) {
            if (args[0].equalsIgnoreCase("reset")) {
                return StringUtil.copyPartialMatches(args[2], Arrays.asList("coin", "mcoin"), new ArrayList<>());
            }
            return null; // Hiện danh sách người chơi cho các lệnh add/set/check/take
        }

        return Collections.emptyList();
    }
}