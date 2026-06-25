package dev.lazycat.sakuraLand.origins.originsListeners;

import dev.lazycat.sakuraLand.origins.Origin;
import dev.lazycat.sakuraLand.origins.OriginsCore;
import dev.lazycat.sakuraLand.origins.originsStorage.WolfOrigin;
import dev.lazycat.sakuraLand.origins.pdct.OriginDataType;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;

public class WolfTameListener implements Listener {
    private final MiniMessage mm = MiniMessage.miniMessage();

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Player)) return;
        Player wolf = (Player) event.getRightClicked();
        Player tamer = event.getPlayer();

        PersistentDataContainer pdc = wolf.getPersistentDataContainer();
        Origin current = pdc.get(OriginsCore.playerOrigin, OriginDataType.INSTANCE);
        if (!(current instanceof WolfOrigin)) return;

        WolfOrigin wolfOrigin = (WolfOrigin) current;

        ItemStack item = tamer.getInventory().getItemInMainHand();
        if (item.getType() != Material.BONE) return;

        if (wolfOrigin.isTameCooldownActive(tamer)) {
            long sec = wolfOrigin.getTameCooldownSeconds(tamer);
            tamer.sendActionBar(mm.deserialize(
                    "<red>Подождите <white>" + sec + "</white> секунд перед новой попыткой приручения."
            ));
            return;
        }

        if (wolfOrigin.isTamed(wolf)) {
            tamer.sendMessage(mm.deserialize("<red>Этот волк уже имеет хозяина!"));
            return;
        }

        openTameGUI(wolf, tamer);
    }

    private void openTameGUI(Player wolf, Player tamer) {
       wolf.sendMessage(mm.deserialize("<gold>Заглушка приручения"));
       tamer.sendMessage(mm.deserialize("<gold>Заглушка приручения"));
    }
}
