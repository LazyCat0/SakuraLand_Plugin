package dev.lazycat.sakuraLand.origins.originsListeners;

import dev.lazycat.sakuraLand.SakuraLand;
import dev.lazycat.sakuraLand.origins.OriginsCore;
import dev.lazycat.sakuraLand.origins.OriginsRegistry;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public class IfritDamageEventListener implements Listener {

    private final SakuraLand plugin;

    public IfritDamageEventListener(SakuraLand plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player player)) {
            return;
        }
        EntityDamageEvent.DamageCause cause = e.getCause();
        if (OriginsCore.getPlayerOrigin(player) == OriginsRegistry.get("ifrit")) {
            if (cause == EntityDamageEvent.DamageCause.CAMPFIRE || cause == EntityDamageEvent.DamageCause.FIRE || cause == EntityDamageEvent.DamageCause.FIRE_TICK) {
                e.setCancelled(true);
            }
            if (player.isInLava())
                e.setCancelled(true);
        }
    }
    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if (!(event.getEntity() instanceof Snowball snowball)) {
            return;
        }

        if (!(event.getHitEntity() instanceof Player target)) {
            return;
        }
        if (OriginsCore.getPlayerOrigin(target) == OriginsRegistry.get("ifrit"))
            target.damage(2.0, snowball);
    }
}
