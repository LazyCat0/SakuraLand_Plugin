package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class CatOrigin extends Origin {
    private final JavaPlugin plugin;
    public CatOrigin(String id, String displayName, JavaPlugin plugin) {
        super(id, displayName);
        this.plugin = plugin;
    }
    @Override
    public void onGetOrigin(@NotNull Player player) {
        NamespacedKey speedModKey = new NamespacedKey(plugin, "speed_boost");
        NamespacedKey breakSpeedModKey = new NamespacedKey(plugin, "break_mod");

        player.clearActivePotionEffects();
        AttributeInstance playerMaxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        AttributeInstance playerSpeed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        AttributeInstance playerBreakSpeed = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        AttributeInstance playerSafeFallDistance = player.getAttribute(Attribute.SAFE_FALL_DISTANCE);
        if (playerMaxHealth != null) {
            playerMaxHealth.setBaseValue(16);
            player.setHealth(Math.min(player.getHealth(), 16));
        }
        AttributeModifier speedMod = new AttributeModifier(
                speedModKey,
                0.1,
                AttributeModifier.Operation.ADD_SCALAR
        );
        if (playerSpeed != null) {
            if (!playerSpeed.getModifiers().contains(speedMod))
                playerSpeed.addModifier(speedMod);
        }
        AttributeModifier breakMod = new AttributeModifier(
                breakSpeedModKey,
                -0.8,
                AttributeModifier.Operation.ADD_SCALAR
        );
        if (playerBreakSpeed != null) {
            if (!playerBreakSpeed.getModifiers().contains(breakMod))
                playerBreakSpeed.addModifier(breakMod);
        }
        assert playerSafeFallDistance != null;
        playerSafeFallDistance.setBaseValue(20);
    }
    @Override
    public void applyEffects(@NotNull Player player) {
        if (!player.hasPotionEffect(PotionEffectType.JUMP_BOOST) || !player.hasPotionEffect(PotionEffectType.NIGHT_VISION) || !player.hasPotionEffect(PotionEffectType.SPEED)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 1200, 0, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 1200, 1, false, false, false));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 1200, 0, false, false, false));
        }
    }
}
