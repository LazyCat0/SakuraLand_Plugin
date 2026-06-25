package dev.lazycat.sakuraLand.origins.originsListeners;

import dev.lazycat.sakuraLand.origins.OriginsCore;
import dev.lazycat.sakuraLand.origins.OriginsRegistry;
import dev.lazycat.sakuraLand.origins.originsStorage.WitherOrigin;
import dev.lazycat.sakuraLand.someFeatures.AiCoded;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

@AiCoded(by="@Kliker38", id = "5041138307", ai = "chatGpt")
public class WitherOriginListener implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent event) {

        if (!(event.getEntity() instanceof Player player))
            return;

        if (!(OriginsCore.getPlayerOrigin(player)
                instanceof WitherOrigin origin))
            return;

        switch (event.getCause()) {

            case WITHER:
            case SUFFOCATION:
            case BLOCK_EXPLOSION:
            case ENTITY_EXPLOSION:
                event.setCancelled(true);
                break;
        }

        if (origin.hasBoneArmor(player)) {
            event.setDamage(
                    event.getDamage() * 0.5
            );
        }
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent event) {

        if (!(event.getDamager() instanceof Player player))
            return;

        if (!(OriginsCore.getPlayerOrigin(player) == OriginsRegistry.get("wither")))
            return;

        if (Math.random() <= 0.20
                && event.getEntity()
                instanceof LivingEntity target) {

            target.addPotionEffect(
                    new PotionEffect(
                            PotionEffectType.WITHER,
                            100,
                            0
                    )
            );
        }
    }
}