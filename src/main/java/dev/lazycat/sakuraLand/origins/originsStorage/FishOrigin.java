package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.SakuraLand;
import dev.lazycat.sakuraLand.origins.Origin;
import dev.lazycat.sakuraLand.origins.OriginsCore;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class FishOrigin extends Origin {

    private static final NamespacedKey OXYGEN_KEY = new NamespacedKey("sakuraland", "fish_oxygen");
    private static final int MAX_OXYGEN = 300;
    private static final int OXYGEN_INCREASE = 2;
    private static final int OXYGEN_DECREASE = 2;
    private final SakuraLand plugin;

    public FishOrigin(String id, String displayName, SakuraLand plugin) {
        super(id, displayName);
        startOxygenTask();
        this.plugin = plugin;
    }

    private void startOxygenTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (!(OriginsCore.getPlayerOrigin(player) instanceof FishOrigin)) continue;
                    updateOxygen(player);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    private void updateOxygen(Player player) {
        boolean inWater = player.getEyeLocation().getBlock().getType() == Material.WATER;
        int oxygen = getOxygen(player);

        if (inWater) {
            oxygen = Math.min(oxygen + OXYGEN_INCREASE, MAX_OXYGEN);

            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, -1, 1, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 1, false, false, false));
        } else {
            oxygen = Math.max(0, oxygen - OXYGEN_DECREASE);

            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
        }

        setOxygen(player, oxygen);

        if (oxygen == 0 && !inWater) {
            long lastDamage = player.getPersistentDataContainer().getOrDefault(
                    new NamespacedKey("sakuraland", "last_damage_time"),
                    PersistentDataType.LONG, 0L);
            if (System.currentTimeMillis() - lastDamage >= 1000) {
                player.damage(1.0);
                player.getPersistentDataContainer().set(
                        new NamespacedKey("sakuraland", "last_damage_time"),
                        PersistentDataType.LONG, System.currentTimeMillis()
                );
            }
        }
    }

    private int getOxygen(Player player) {
        return player.getPersistentDataContainer().getOrDefault(OXYGEN_KEY, PersistentDataType.INTEGER, MAX_OXYGEN);
    }

    private void setOxygen(Player player, int oxygen) {
        player.getPersistentDataContainer().set(OXYGEN_KEY, PersistentDataType.INTEGER, Math.max(0, Math.min(oxygen, MAX_OXYGEN)));
    }

    @Override
    public void onGetOrigin(@NotNull Player player) {
        setOxygen(player, MAX_OXYGEN);
    }
}