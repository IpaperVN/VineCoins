package me.ipapervn.VineCoins;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class VineCoinsExpansion extends PlaceholderExpansion {

    private final VineCoins plugin;

    public VineCoinsExpansion(VineCoins plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getAuthor() {
        return "YourName"; // Tên của bạn
    }

    @Override
    public @NotNull String getIdentifier() {
        return "vinecoins"; // Đây là phần đầu: %vinecoins_...%
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public boolean persist() {
        return true; // Giữ expansion này sau khi PAPI reload
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        if (player == null) return "0";

        // %vinecoins_amount%
        if (params.equalsIgnoreCase("amount")) {
            int coins = plugin.getCoinsManager().getCoins(player.getUniqueId());
            return String.valueOf(coins);
        }

        // %vinecoins_amount_formatted% (Thêm dấu phẩy ví dụ: 1,000,000)
        if (params.equalsIgnoreCase("amount_formatted")) {
            int coins = plugin.getCoinsManager().getCoins(player.getUniqueId());
            return String.format("%,d", coins);
        }

        return null; // Trả về null nếu placeholder không tồn tại
    }
}
