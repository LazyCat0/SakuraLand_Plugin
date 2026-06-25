package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.SmallFireball;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class IfritOrigin extends Origin {

    private final HashMap<UUID, Long> cooldownZ = new HashMap<>();
    private final HashMap<UUID, Long> cooldownX = new HashMap<>();

    private final long cooldownTimeZ = 10 * 1000;
    private final long cooldownTimeX = 3 * 1000;

    private MiniMessage mm = MiniMessage.miniMessage();
    public IfritOrigin(String id, String displayName) {
        super(id, displayName);
    }
    @Override
    public void applyEffects(@NotNull Player player) {
        if (player.isInRain() || player.isInWater()) {
            player.damage(2, DamageSource.builder(DamageType.DRY_OUT).build());
        }
    }
    @Override
    public void abilityZExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownZ)) {
            long secondsLeft = getRemainingCooldown(player, cooldownZ);
            String secondsLeftAsString = String.format("%s", secondsLeft);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 1 ещё на перезарядке - <white><sec></white> секунд",
                    Placeholder.parsed("sec", secondsLeftAsString)
            ));
            return;
        }

        Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.2));
        Fireball fireball = player.getWorld().spawn(spawnLocation, Fireball.class);

        fireball.setShooter(player);
        fireball.setDirection(player.getLocation().getDirection());
        fireball.setVelocity(player.getLocation().getDirection().multiply(1.2));
        setCooldown(player, cooldownZ, cooldownTimeZ);
    }
    @Override
    public void abilityXExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownX)) {
            long secondsLeft = getRemainingCooldown(player, cooldownX);
            String secondsLeftAsString = String.format("%s", secondsLeft);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 2 ещё на перезарядке - <white><sec></white> секунд",
                    Placeholder.parsed("sec", secondsLeftAsString)
            ));
            return;
        }

        Location spawnLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(1.2));
        SmallFireball fireball = player.getWorld().spawn(spawnLocation, SmallFireball.class);

        fireball.setShooter(player);
        fireball.setDirection(player.getLocation().getDirection());
        fireball.setVelocity(player.getLocation().getDirection().multiply(1.2));
        setCooldown(player, cooldownX, cooldownTimeX);
    }

    private boolean isCooldownActive(Player player, HashMap<UUID, Long> cooldownMap) {
        if (!cooldownMap.containsKey(player.getUniqueId())) return false;
        return cooldownMap.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    private void setCooldown(Player player, HashMap<UUID, Long> cooldownMap, long time) {
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis() + time);
    }

    private long getRemainingCooldown(Player player, HashMap<UUID, Long> cooldownMap) {
        if (!cooldownMap.containsKey(player.getUniqueId())) return 0;
        long timeLeft = cooldownMap.get(player.getUniqueId()) - System.currentTimeMillis();
        return Math.max(0, timeLeft / 1000);
    }
}
