package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class CreeperOrigin extends Origin {
    public CreeperOrigin(String id, String displayName, Plugin plugin) {
        super(id, displayName);
        this.p = plugin;
    }
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final int cooldownTime = 60 * 1000;
    private final Plugin p;

    private MiniMessage mm = MiniMessage.miniMessage();
    @Override
    public void abilityZExecute(@NotNull Player player) {
        if (isCooldownActive(player)) {
            long secondsLeft = getRemainingCooldown(player);
            String secondsLeftAsString = String.format("%s", secondsLeft);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 1 ещё на перезарядке - <white><sec></white> секунд",
                    Placeholder.parsed("sec", secondsLeftAsString)
            ));
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_HURT, 1.0f, 1.0f);
            return;
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_CREEPER_PRIMED, 1.0f, 1.0f);
        Bukkit.getScheduler().runTaskLater(p, () -> {
            player.getWorld().createExplosion(player.getLocation(), 3.0f, true, true, player);
            player.damage(1000.0, DamageSource.builder(DamageType.EXPLOSION).build());
        }, 40L);

        setCooldown(player);
    }
    private boolean isCooldownActive(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) return false;
        return cooldowns.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    private void setCooldown(Player player) {
        cooldowns.put(player.getUniqueId(), System.currentTimeMillis() + cooldownTime);
    }

    private long getRemainingCooldown(Player player) {
        if (!cooldowns.containsKey(player.getUniqueId())) return 0;
        long timeLeft = cooldowns.get(player.getUniqueId()) - System.currentTimeMillis();
        return Math.max(0, timeLeft / 1000);
    }
}
