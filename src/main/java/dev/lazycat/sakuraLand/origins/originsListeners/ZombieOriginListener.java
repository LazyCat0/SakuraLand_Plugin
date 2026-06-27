package dev.lazycat.sakuraLand.origins.originsListeners;

import dev.lazycat.sakuraLand.origins.OriginsCore;
import dev.lazycat.sakuraLand.origins.OriginsRegistry;
import dev.lazycat.sakuraLand.someFeatures.AiCoded;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

@AiCoded(by="@tea_109", id="7903867800")
public class ZombieOriginListener implements Listener {
    @EventHandler
    public void onEntityTarget(EntityTargetLivingEntityEvent event) {
        if (!(event.getTarget() instanceof Player player)) return;
        if (OriginsCore.getPlayerOrigin(player) != OriginsRegistry.get("zombie")) return;

        Entity source = event.getEntity();

        if (source instanceof Monster) {
            event.setCancelled(true);
            if (source instanceof Mob mob) {
                mob.setTarget(null);
            }
        }
        else if (source instanceof IronGolem) {
            if (source instanceof Mob mob) {
                mob.setTarget(player);
            }
        }
    }
}
