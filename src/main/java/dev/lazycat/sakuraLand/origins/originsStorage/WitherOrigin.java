package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.SakuraLand;
import dev.lazycat.sakuraLand.origins.Origin;
import dev.lazycat.sakuraLand.someFeatures.AiCoded;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.UUID;


@AiCoded(by="@Kliker38", id = "5041138307", ai = "chatGpt")
public class WitherOrigin extends Origin {

    private final HashMap<UUID, Long> zCooldown = new HashMap<>();
    private final HashMap<UUID, Long> xCooldown = new HashMap<>();
    private final HashMap<UUID, Long> cCooldown = new HashMap<>();

    private final HashMap<UUID, Integer> boneArmor = new HashMap<>();

    private final SakuraLand plugin;

    private static final long Z_CD = 50;
    private static final long X_CD = 30;
    private static final long C_CD = 120;

    public WitherOrigin(String id, String displayName ,SakuraLand plugin) {
        super(id, displayName);
        this.plugin = plugin;
    }

    @Override
    public void onGetOrigin(Player player) {
        player.addPotionEffect(
                new PotionEffect(PotionEffectType.WITHER, 200, 0)
        );

        player.addPotionEffect(
                new PotionEffect(PotionEffectType.STRENGTH, 300, 0)
        );
    }

    @Override
    public void applyEffects(Player player) {
        if (player.hasPotionEffect(PotionEffectType.WITHER)) {
            player.removePotionEffect(PotionEffectType.WITHER);
        }
    }

    @Override
    public void abilityZExecute(Player player) {

        long current = System.currentTimeMillis();

        if (zCooldown.containsKey(player.getUniqueId())) {
            long last = zCooldown.get(player.getUniqueId());

            if ((current - last) < Z_CD * 1000) {

                long remain =
                        Z_CD - ((current - last) / 1000);

                player.sendActionBar(
                        MiniMessage.miniMessage().deserialize(
                                "<red>Способность будет готова через "
                                        + remain + " сек."
                        )
                );
                return;
            }
        }

        zCooldown.put(player.getUniqueId(), current);

        WitherSkull skull = player.launchProjectile(WitherSkull.class);

        skull.setVelocity(
                player.getLocation()
                        .getDirection()
                        .multiply(1.5)
        );

        player.getWorld().playSound(
                player.getLocation(),
                Sound.ENTITY_WITHER_SHOOT,
                1f,
                1f
        );

        new BukkitRunnable() {

            @Override
            public void run() {

                if (skull.isDead() || skull.isOnGround()) {

                    Location loc = skull.getLocation();

                    loc.getWorld().spawnParticle(
                            Particle.SOUL,
                            loc,
                            80,
                            1,
                            1,
                            1,
                            0.1
                    );

                    loc.getWorld().createExplosion(
                            loc,
                            1.5f,
                            false,
                            false
                    );

                    AreaEffectCloud cloud =
                            loc.getWorld().spawn(
                                    loc,
                                    AreaEffectCloud.class
                            );

                    cloud.setRadius(4f);
                    cloud.setDuration(160);
                    cloud.setParticle(Particle.ASH);

                    new BukkitRunnable() {

                        int ticks = 160;

                        @Override
                        public void run() {

                            if (ticks <= 0 || cloud.isDead()) {
                                cancel();
                                return;
                            }

                            for (Player nearby :
                                    Bukkit.getOnlinePlayers()) {

                                if (nearby.equals(player))
                                    continue;

                                if (!nearby.getWorld().equals(
                                        cloud.getWorld()))
                                    continue;

                                if (nearby.getLocation()
                                        .distance(
                                                cloud.getLocation())
                                        <= 4) {

                                    nearby.damage(
                                            2.0,
                                            player
                                    );
                                }
                            }

                            ticks -= 20;
                        }
                    }.runTaskTimer(
                            plugin,
                            0,
                            20
                    );

                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    @Override
    public void abilityXExecute(Player player) {

        long current = System.currentTimeMillis();

        if (xCooldown.containsKey(player.getUniqueId())) {

            long last = xCooldown.get(player.getUniqueId());

            if ((current - last) < X_CD * 1000) {

                long remain =
                        X_CD - ((current - last) / 1000);

                player.sendActionBar(
                        MiniMessage.miniMessage().deserialize(
                                "<red>Способность будет готова через "
                                        + remain + " сек."
                        )
                );
                return;
            }
        }

        xCooldown.put(player.getUniqueId(), current);

        boneArmor.put(player.getUniqueId(), 3);

        player.getWorld().playSound(
                player.getLocation(),
                Sound.ITEM_ARMOR_EQUIP_NETHERITE,
                1f,
                0.7f
        );

        player.sendMessage(
                MiniMessage.miniMessage().deserialize(
                        "<gray>Вас окружили <white>3 костяных щита</white>."
                )
        );

        new BukkitRunnable() {

            int timer = 200;

            @Override
            public void run() {

                if (timer <= 0
                        || !player.isOnline()) {

                    boneArmor.remove(
                            player.getUniqueId()
                    );
                    cancel();
                    return;
                }

                Location loc =
                        player.getLocation();

                for (int i = 0; i < 3; i++) {

                    double angle =
                            (Math.PI * 2 / 3 * i)
                                    + (timer / 10D);

                    Location point =
                            loc.clone().add(
                                    Math.cos(angle),
                                    1.2,
                                    Math.sin(angle)
                            );

                    player.getWorld().spawnParticle(
                            Particle.SOUL_FIRE_FLAME,
                            point,
                            1
                    );
                    pushEntitiesAway(player, 1.2, 5);
                }

                timer--;
            }
        }.runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void abilityCExecute(Player player) {

        long current = System.currentTimeMillis();

        if (cCooldown.containsKey(player.getUniqueId())) {

            long last = cCooldown.get(player.getUniqueId());

            if ((current - last) < C_CD * 1000) {

                long remain =
                        C_CD - ((current - last) / 1000);

                player.sendActionBar(
                        MiniMessage.miniMessage().deserialize(
                                "<red>Способность будет готова через "
                                        + remain + " сек."
                        )
                );
                return;
            }
        }

        cCooldown.put(player.getUniqueId(), current);

        for (int i = 0; i < 3; i++) {

            Location spawn = player.getLocation().clone().add(
                    Math.random() * 3 - 1.5,
                    0,
                    Math.random() * 3 - 1.5
            );

            WitherSkeleton skeleton = player.getWorld().spawn(
                    spawn,
                    WitherSkeleton.class
            );

            skeleton.setCustomName(ChatColor.DARK_GRAY + "Малый Иссушитель");
            skeleton.setCustomNameVisible(false);

            if (skeleton.getAttribute(Attribute.MAX_HEALTH) != null) {
                skeleton.getAttribute(Attribute.MAX_HEALTH).setBaseValue(12.0);
            }
            skeleton.setHealth(12.0);

            // Каждые 10 тиков обновляем цель
            new BukkitRunnable() {
                @Override
                public void run() {

                    if (!skeleton.isValid() || skeleton.isDead()) {
                        cancel();
                        return;
                    }

                    LivingEntity target = null;
                    double bestDistance = Double.MAX_VALUE;

                    for (LivingEntity entity : skeleton.getLocation()
                            .getNearbyLivingEntities(16)) {

                        if (entity.equals(player))
                            continue;

                        // Не атаковать других призванных скелетов
                        if (entity instanceof WitherSkeleton)
                            continue;

                        // Можно также игнорировать бронестойки и т.п.
                        if (entity.isDead())
                            continue;

                        double distance = entity.getLocation()
                                .distanceSquared(skeleton.getLocation());

                        if (distance < bestDistance) {
                            bestDistance = distance;
                            target = entity;
                        }
                    }

                    skeleton.setTarget(target);
                }
            }.runTaskTimer(plugin, 0L, 10L);

            new BukkitRunnable() {
                @Override
                public void run() {
                    if (skeleton.isValid()) {
                        skeleton.remove();
                    }
                }
            }.runTaskLater(plugin, 20L * 20L);
        }

        player.getWorld().playSound(
                player.getLocation(),
                Sound.ENTITY_WITHER_SPAWN,
                1f,
                1f
        );
    }

    public boolean hasBoneArmor(Player player) {
        return boneArmor.containsKey(
                player.getUniqueId()
        );
    }
    private void pushEntitiesAway(Player player, double radius, double power) {
        for (Entity entity : player.getNearbyEntities(radius, radius, radius)) {

            if (entity instanceof LivingEntity && !entity.equals(player)) {

                Location playerLoc = player.getLocation();
                Location entityLoc = entity.getLocation();

                Vector direction = entityLoc.toVector().subtract(playerLoc.toVector());

                if (direction.lengthSquared() == 0) {
                    direction = new Vector(1, 0, 0);
                }

                direction.normalize().multiply(power);

                direction.setY(0.4);

                entity.setVelocity(direction);
            }
        }
    }
}