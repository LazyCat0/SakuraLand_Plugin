package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

public class WardenOrigin extends Origin {
    public WardenOrigin(String id, String displayName) {
        super(id, displayName);
    }
    @Override
    public void onGetOrigin(@NotNull Player player) {
        // attributes
        AttributeInstance playerMaxHealth = player.getAttribute(Attribute.MAX_HEALTH);
        AttributeInstance playerScale = player.getAttribute(Attribute.SCALE);

        assert playerMaxHealth != null;
        playerMaxHealth.setBaseValue(30);

        assert playerScale != null;
        playerScale.setBaseValue(1.2);
        player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, -1, 999, false, false, false));
    }
}
