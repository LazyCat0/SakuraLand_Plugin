package dev.lazycat.sakuraLand.currency.stars;

import dev.lazycat.sakuraLand.SakuraLand;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import static dev.lazycat.sakuraLand.SakuraLand.getStarsInstance0;

public class Stars {
    private final NamespacedKey starsKey;

    public Stars(SakuraLand plugin) {
        starsKey = new NamespacedKey(plugin, "currency-stars");
    }

    public static void applyCurrency(@NonNull Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(getStarsInstance0().starsKey, PersistentDataType.INTEGER, 0);
    }
    public Integer getCurrency(@NonNull Player player) {
        return player.getPersistentDataContainer().getOrDefault(starsKey, PersistentDataType.INTEGER, 0);
    }
    public void setCurrency(@NonNull Player player, Integer value) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(starsKey, PersistentDataType.INTEGER, value);
    }
    public void addCurrency(@NonNull Player player, Integer value) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(starsKey, PersistentDataType.INTEGER, getCurrency(player) + value);
    }
    public void decreaseCurrency(@NonNull Player player, Integer value) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(starsKey, PersistentDataType.INTEGER, getCurrency(player) - value);
    }
}
