package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.SakuraLand;
import dev.lazycat.sakuraLand.origins.Origin;
import dev.lazycat.sakuraLand.someFeatures.AiCoded;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

@AiCoded(by="@tea_109", id="7903867800")
public class ZombieOrigin extends Origin  {

    private final SakuraLand plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    private final HashMap<UUID, Long> cooldownZ = new HashMap<>();
    private final HashMap<UUID, Long> cooldownX = new HashMap<>();
    private static final long Z_COOLDOWN = 30 * 1000L;
    private static final long X_COOLDOWN = 15 * 1000L;

    public ZombieOrigin(String id, String displayName, SakuraLand plugin) {
        super(id, displayName);
        this.plugin = plugin;
    }


    @Override
    public void onGetOrigin(@NotNull Player player) {
        super.onGetOrigin(player);

        AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(24.0);
            player.setHealth(Math.min(player.getHealth(), 24.0));
        }
    }

    @Override
    public void applyEffects(@NotNull Player player) {
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.NIGHT_VISION,
                    200, 0, false, false, false
            ));
        }
            for (Entity entity : player.getNearbyEntities(12, 4, 12)) {
                if (entity instanceof Villager villager) {

                    Vector fleeVector = villager.getLocation().toVector()
                            .subtract(player.getLocation().toVector())
                            .normalize()
                            .multiply(5);

                    villager.getPathfinder().moveTo(
                            villager.getLocation().add(fleeVector),
                            1.3
                    );
                }
                if (entity instanceof IronGolem golem) {
                    golem.setTarget(player);
                }
            }
    }

    @Override
    public void abilityZExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownZ)) {
            long secondsLeft = getRemainingCooldown(player, cooldownZ);
            player.sendActionBar(mm.deserialize(
                    "<red>Способность 1 на перезарядке - <white><sec></white> секунд",
                    Placeholder.parsed("sec", String.valueOf(secondsLeft))
            ));
            return;
        }

        player.swingMainHand();

        double chance = 0.4;
        if (Math.random() > chance) {
            player.sendMessage(mm.deserialize("<red>Призыв не удался! Попробуйте снова."));
            setCooldown(player, cooldownZ, Z_COOLDOWN);
            return;
        }

        Location spawnLoc = player.getLocation().add(
                (Math.random() - 0.5) * 6,
                1,
                (Math.random() - 0.5) * 6
        );

        Zombie zombie = player.getWorld().spawn(spawnLoc, Zombie.class);
        zombie.setAdult();
        zombie.customName(mm.deserialize("<green>Зомби-слуга"));
        zombie.setCustomNameVisible(true);

        if (zombie.getAttribute(Attribute.MAX_HEALTH) != null) {
            Objects.requireNonNull(zombie.getAttribute(Attribute.MAX_HEALTH)).setBaseValue(20.0);
        }
        zombie.setHealth(20.0);

        // Автоматический поиск целей для зомби-слуги
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!zombie.isValid() || zombie.isDead()) {
                    cancel();
                    return;
                }

                double nearestDistance = Double.MAX_VALUE;
                LivingEntity nearestTarget = null;

                for (LivingEntity entity : zombie.getLocation().getNearbyLivingEntities(20)) {
                    if (entity.equals(player)) continue;
                    if (entity instanceof Player) continue;
                    if (entity instanceof Monster || entity instanceof Animals) {
                        double dist = entity.getLocation().distance(zombie.getLocation());
                        if (dist < nearestDistance) {
                            nearestDistance = dist;
                            nearestTarget = entity;
                        }
                    }
                }

                zombie.setTarget(nearestTarget);
            }
        }.runTaskTimer(plugin, 0L, 10L);

        // Автоудаление через 30 секунд
        new BukkitRunnable() {
            @Override
            public void run() {
                if (zombie.isValid()) {
                    zombie.remove();
                    player.sendMessage(mm.deserialize("<red>Ваш зомби-слуга истлел."));
                }
            }
        }.runTaskLater(plugin, 30 * 20L);

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_AMBIENT, 1.0f, 1.0f);
        player.sendMessage(mm.deserialize("<green>Вы призвали <gold>Зомби-слугу</gold>!"));

        setCooldown(player, cooldownZ, Z_COOLDOWN);
    }

    @Override
    public void abilityXExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownX)) {
            long secondsLeft = getRemainingCooldown(player, cooldownX);
            player.sendActionBar(mm.deserialize(
                    "<red>Способность 2 на перезарядке - <white><sec></white> секунд",
                    Placeholder.parsed("sec", String.valueOf(secondsLeft))
            ));
            return;
        }

        player.swingMainHand();

        double damage = 8.0;
        double knockback = 0.8;

        for (LivingEntity target : player.getLocation().getNearbyLivingEntities(4.0)) {
            if (target.equals(player)) continue;
            if (target instanceof Player) continue;

            target.damage(damage, player);

            org.bukkit.util.Vector direction = target.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            direction.setY(0.5);
            target.setVelocity(direction.multiply(knockback));

            target.getWorld().spawnParticle(Particle.SOUL, target.getLocation().add(0, 1, 0), 20, 0.3, 0.3, 0.3, 0.1);
        }

        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 1.0f, 0.5f);
        player.sendMessage(mm.deserialize("<green>Вы нанесли <gold>Зомби-удар</gold>!"));

        setCooldown(player, cooldownX, X_COOLDOWN);
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