package dev.lazycat.sakuraLand.origins.originsListeners;

import dev.lazycat.sakuraLand.origins.OriginsCore;
import dev.lazycat.sakuraLand.origins.OriginsRegistry;
import dev.lazycat.sakuraLand.origins.originsStorage.VoidReaperOrigin;
import dev.lazycat.sakuraLand.someFeatures.AiCoded;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

@AiCoded(by = "@FB_cann0n", id = "7523927123", ai = "deepseek")
public class VoidReaperListener implements Listener {


    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Player killer = event.getEntity().getKiller();
        if (killer == null) return;

        if (OriginsCore.getPlayerOrigin(killer) == OriginsRegistry.get("void_reaper")) {
            VoidReaperOrigin.addSoul(killer);
            killer.sendMessage("§aВы поглотили душу! §7(Всего: §f" + VoidReaperOrigin.getSouls(killer) + "§7)");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (OriginsCore.getPlayerOrigin(player) == OriginsRegistry.get("void_reaper")) {
            VoidReaperOrigin.resetSouls(player);
            player.sendMessage("§cВы погибли и потеряли все накопленные души!");
        }
    }
}