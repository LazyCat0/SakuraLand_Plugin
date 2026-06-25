package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FishOrigin extends Origin {
    public FishOrigin(String id, String displayName) {
        super(id, displayName);
    }
    @Override
    public void applyEffects(@NonNull Player player) {
        boolean isInWater = player.getEyeLocation().getBlock().getType() == Material.WATER;
        if (isInWater) {
            player.addPotionEffect(new PotionEffect( PotionEffectType.DOLPHINS_GRACE,-1, 1, false, false, false));
            player.setRemainingAir(player.getMaximumAir());
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 1, false, false, false));
        }
        else {
            player.setRemainingAir(player.getRemainingAir() - 2);
            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
            player.removePotionEffect(PotionEffectType.NIGHT_VISION);
            if (player.getRemainingAir() < 0)
                player.damage(1.0);
        }
    }
}
