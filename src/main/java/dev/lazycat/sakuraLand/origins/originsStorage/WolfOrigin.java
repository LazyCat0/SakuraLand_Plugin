package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class WolfOrigin extends Origin {
    private static final NamespacedKey OWNER_KEY = new NamespacedKey("sakuraland", "wolf_owner");
    private static final NamespacedKey TAMED_TIME_KEY = new NamespacedKey("sakuraland", "wolf_tamed_time");

    public WolfOrigin(String id, String displayName) {
        super(id, displayName);
    }

    public void setOwner(Player wolf, Player owner) {
        if (owner == null) {
            wolf.getPersistentDataContainer().remove(OWNER_KEY);
        } else {
            wolf.getPersistentDataContainer().set(OWNER_KEY, PersistentDataType.STRING, owner.getUniqueId().toString());
            NamespacedKey healthModKey = new NamespacedKey("sakuraland", "health_mod");
            NamespacedKey damageModKey = new NamespacedKey("sakuraland", "damage_mod");

            AttributeInstance playerMaxHealth = wolf.getAttribute(Attribute.MAX_HEALTH);
            AttributeInstance playerDamage = wolf.getAttribute(Attribute.ATTACK_DAMAGE);

            AttributeModifier modifier1 = new AttributeModifier(
                    healthModKey,
                    0.3,
                    AttributeModifier.Operation.ADD_SCALAR
            );
            AttributeModifier modifier2 = new AttributeModifier(
                    damageModKey,
                    0.3,
                    AttributeModifier.Operation.ADD_SCALAR
            );
            assert playerMaxHealth != null;
            playerMaxHealth.addModifier(modifier1);
            assert playerDamage != null;
            playerDamage.addModifier(modifier2);
        }
    }

    @Nullable
    public UUID getOwnerUUID(Player wolf) {
        String uuidStr = wolf.getPersistentDataContainer().get(OWNER_KEY, PersistentDataType.STRING);
        return uuidStr == null ? null : UUID.fromString(uuidStr);
    }

    @Nullable
    public Player getOwner(Player wolf) {
        UUID uuid = getOwnerUUID(wolf);
        return uuid == null ? null : org.bukkit.Bukkit.getPlayer(uuid);
    }

    public boolean isTamed(Player wolf) {
        return getOwnerUUID(wolf) != null;
    }
    public long getLastTameAttempt(Player tamer) {
        return tamer.getPersistentDataContainer().getOrDefault(
                new NamespacedKey("sakuraland", "tame_attempt_cooldown"),
                PersistentDataType.LONG,
                0L
        );
    }

    public void setLastTameAttempt(Player tamer, long time) {
        tamer.getPersistentDataContainer().set(
                new NamespacedKey("sakuraland", "tame_attempt_cooldown"),
                PersistentDataType.LONG,
                time
        );
    }

    public boolean isTameCooldownActive(Player tamer) {
        long last = getLastTameAttempt(tamer);
        long cooldownMillis = 2 * 60 * 1000; // 2 минуты
        return System.currentTimeMillis() - last < cooldownMillis;
    }

    public long getTameCooldownSeconds(Player tamer) {
        long last = getLastTameAttempt(tamer);
        long cooldownMillis = 2 * 60 * 1000;
        long remaining = (last + cooldownMillis) - System.currentTimeMillis();
        return Math.max(0, remaining / 1000);
    }
}