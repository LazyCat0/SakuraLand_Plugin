package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class ChickenOrigin extends Origin {
    public ChickenOrigin(String id, String displayName) {
        super(id, displayName);
    }

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final long cooldownTime = 2000;

    private MiniMessage mm = MiniMessage.miniMessage();

    @Override
    public void applyEffects(@NonNull Player player) {
        if (!(player.hasPotionEffect(PotionEffectType.SLOW_FALLING)))
            player.addPotionEffect(new PotionEffect( PotionEffectType.SLOW_FALLING,120, 1, true, false, false));
    }
    @Override
    public void abilityZExecute(@NotNull Player player) {
        if (isCooldownActive(player)) {
            long secondsLeft = getRemainingCooldown(player);
            String secondsLeftAsString = String.format("%s", secondsLeft);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 1 ещё на перезарядке - <white><sec></white> секунд",
                    Placeholder.parsed("sec", secondsLeftAsString)
            ));
            return;
        }
        player.swingMainHand();
        player.playSound(player, Sound.ENTITY_CHICKEN_AMBIENT, 1.0f, 1.0f);
        Vector pushUp = player.getLocation().getDirection().multiply(0.5).setY(0.8);

        player.setVelocity(pushUp);

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
