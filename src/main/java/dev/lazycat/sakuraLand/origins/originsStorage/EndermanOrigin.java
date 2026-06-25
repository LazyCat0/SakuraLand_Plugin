package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Block;
import org.bukkit.damage.DamageSource;
import org.bukkit.damage.DamageType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.UUID;

public class EndermanOrigin extends Origin {
    public EndermanOrigin(String id, String displayName) {
        super(id, displayName);
    }

    private MiniMessage mm = MiniMessage.miniMessage();
    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private final int cooldownTime = 5 * 1000;

    @Override
    public void onGetOrigin(@NotNull Player player) {
        AttributeInstance playerScale = player.getAttribute(Attribute.SCALE);
        assert playerScale != null;
        playerScale.setBaseValue(1.5);
    }

    @Override
    public void applyEffects(@NotNull Player player) {
        Location loc1 = new Location(player.getWorld(), player.getX() + 0.5, player.getY() + 1.55, player.getZ() + 0.5);
        Location loc2 = new Location(player.getWorld(), player.getX() - 0.5, player.getY(), player.getZ() - 0.5);
        player.getWorld().spawnParticle(Particle.PORTAL, loc1, 9);
        player.getWorld().spawnParticle(Particle.PORTAL, loc2, 9);

        if (player.isInRain() || player.isInWater()) {
            player.damage(2, DamageSource.builder(DamageType.DRY_OUT).build());
        }
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
        teleportToLookLocation(player);

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

    public void teleportToLookLocation(Player player) {
        Block targetBlock = player.getTargetBlockExact(20);

        Location targetLocation;
        if (targetBlock == null) {
            targetLocation = player.getEyeLocation().add(player.getLocation().getDirection().multiply(20));
        } else {
            targetLocation = targetBlock.getLocation();
        }

        targetLocation.add(0.5, 1.0, 0.5);

        targetLocation.setYaw(player.getLocation().getYaw());
        targetLocation.setPitch(player.getLocation().getPitch());

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
        player.teleport(targetLocation);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1.0f, 1.0f);
    }

}
