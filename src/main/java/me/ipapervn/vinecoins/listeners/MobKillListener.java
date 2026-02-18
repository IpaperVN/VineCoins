package me.ipapervn.vinecoins.listeners;

import me.ipapervn.vinecoins.VineCoins;
import me.ipapervn.vinecoins.utils.MessageUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.concurrent.ThreadLocalRandom;

public class MobKillListener implements Listener {

    private final VineCoins plugin;

    public MobKillListener(VineCoins plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event) {
        if (!plugin.getConfig().getBoolean("mob-rewards.enabled", true)) return;

        LivingEntity victim = event.getEntity();
        Player killer = victim.getKiller();

        if (killer != null) {
            EntityType type = victim.getType();
            String path = "mob-rewards.rewards." + type.name();

            // Kiểm tra xem Mob có trong config không
            if (plugin.getConfig().contains(path)) {
                double amount = plugin.getConfig().getDouble(path + ".amount");
                double chance = plugin.getConfig().getDouble(path + ".chance");

                // Tạo số ngẫu nhiên từ 0.0 đến 100.0
                double randomValue = ThreadLocalRandom.current().nextDouble(101.0);

                // Nếu số ngẫu nhiên nhỏ hơn hoặc bằng tỉ lệ trong config thì mới được tiền
                if (randomValue <= chance) {
                    plugin.getMCoinManager().addBalance(killer.getUniqueId(), amount);

                    // Lấy tin nhắn từ messages.yml
                    String rawMsg = plugin.getMessagesConfig().getString("mob-kill-actionbar", "&a+ %amount% MCoin &7(%type%)");
                    String formattedMsg = rawMsg.replace("%amount%", String.valueOf(amount))
                            .replace("%type%", type.name());

                    MessageUtils.sendActionBar(killer, formattedMsg);
                }
            }
        }
    }
}