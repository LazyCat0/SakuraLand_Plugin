package dev.lazycat.sakuraLand.origins;

import dev.lazycat.sakuraLand.origins.pdct.OriginDataType;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

public abstract class Origin {
    public final String id;
    public final String displayName;

    private PluginMessageListener listenerZ;
    private PluginMessageListener listenerX;
    private PluginMessageListener listenerC;

    public Origin(String id, String displayName) {
        this.id = id.toLowerCase();
        this.displayName = displayName;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }

    public void onGetOrigin(@NotNull Player player) {
        // keys
        NamespacedKey speedModKey = new NamespacedKey("sakuraland", "speed_boost");
        NamespacedKey breakSpeedModKey = new NamespacedKey("sakuraland", "break_mod");
        NamespacedKey healthModKey = new NamespacedKey("sakuraland", "health_mod");
        NamespacedKey damageModKey = new NamespacedKey("sakuraland", "damage_mod");

        player.clearActivePotionEffects();

        // attributes
        AttributeInstance playerMaxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        AttributeInstance playerSpeed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        AttributeInstance playerBreakSpeed = player.getAttribute(Attribute.BLOCK_BREAK_SPEED);
        AttributeInstance playerDamage = player.getAttribute(Attribute.ATTACK_DAMAGE);
        AttributeInstance playerScale = player.getAttribute(Attribute.SCALE);
        AttributeInstance playerSafeFallDistance = player.getAttribute(Attribute.SAFE_FALL_DISTANCE);

        assert playerMaxHealth != null;
        playerMaxHealth.setBaseValue(20);
        playerMaxHealth.removeModifier(healthModKey);
        assert playerSpeed != null;
        playerSpeed.removeModifier(speedModKey);
        assert playerBreakSpeed != null;
        playerBreakSpeed.removeModifier(breakSpeedModKey);
        assert playerDamage != null;
        playerDamage.removeModifier(damageModKey);
        assert playerScale != null;
        playerScale.setBaseValue(1);
        assert playerSafeFallDistance != null;
        playerSafeFallDistance.setBaseValue(3);
    }
    public void applyEffects(@NotNull Player player) {
    }

    public void abilityZExecute(@NotNull Player player) {}
    public void abilityXExecute(@NotNull Player player) {}
    public void abilityCExecute(@NotNull Player player) {}

    public final void registerNetwork(@NotNull Plugin plugin) {
        if (isMethodOverridden("abilityZExecute")) {
            this.listenerZ = (ch, player, msg) -> {
                if (isCurrentOrigin(player)) {
                    abilityZExecute(player);
                }
            };
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "sakuraland:ability_z", this.listenerZ);
        }

        if (isMethodOverridden("abilityXExecute")) {
            this.listenerX = (ch, player, msg) -> {
                if (isCurrentOrigin(player)) {
                    abilityXExecute(player);
                }
            };
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "sakuraland:ability_x", this.listenerX);
        }

        if (isMethodOverridden("abilityCExecute")) {
            this.listenerC = (ch, player, msg) -> {
                if (isCurrentOrigin(player)) {
                    abilityCExecute(player);
                }
            };
            plugin.getServer().getMessenger().registerIncomingPluginChannel(plugin, "sakuraland:ability_c", this.listenerC);
        }
    }

    public final void unregisterNetwork(@NotNull Plugin plugin) {
        if (listenerZ != null) plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, "sakuraland:ability_z", listenerZ);
        if (listenerX != null) plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, "sakuraland:ability_x", listenerX);
        if (listenerC != null) plugin.getServer().getMessenger().unregisterIncomingPluginChannel(plugin, "sakuraland:ability_c", listenerC);
    }

    private boolean isMethodOverridden(String methodName) {
        try {
            return this.getClass().getMethod(methodName, Player.class).getDeclaringClass() != Origin.class;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public boolean isCurrentOrigin(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        Origin current = pdc.get(OriginsCore.playerOrigin, OriginDataType.INSTANCE);
        return current != null && this.id.equals(current.id);
    }
}