package dev.lazycat.sakuraLand.origins;

import dev.lazycat.sakuraLand.SakuraLand;
import dev.lazycat.sakuraLand.origins.originsStorage.*;
import dev.lazycat.sakuraLand.origins.pdct.OriginDataType;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.scheduler.BukkitRunnable;

public class OriginsCore {
    public static NamespacedKey playerOrigin;
    private final SakuraLand plugin;

    /**
     * иницилизация всякой хери
     *
     * @param plugin ссылка на плагин через который регистрируется NamespacedKey "origin"
     **/
    public OriginsCore(SakuraLand plugin) {
        playerOrigin = new NamespacedKey(plugin, "origin");
        this.plugin = plugin;

        effInit();
        reg();
    }

    /**
     * Регистрация рас
     */
    private void reg() {
        OriginsRegistry.register(new DefaultOrigin("default", "Человек", plugin));
        OriginsRegistry.register(new ChickenOrigin("chicken", "Авиан"));
        OriginsRegistry.register(new FishOrigin("fish", "Рыбка"));
        OriginsRegistry.register(new CatOrigin("cat", "Кот", plugin));
        OriginsRegistry.register(new WitchOrigin("witch", "Ведьма"));
        OriginsRegistry.register(new IfritOrigin("ifrit", "Ифрит"));
        OriginsRegistry.register(new OmnivoreOrigin("omnivore", "Всеядный"));
        OriginsRegistry.register(new CreeperOrigin("creeper", "Крипер", plugin));
        OriginsRegistry.register(new WardenOrigin("warden", "Варден"));
        OriginsRegistry.register(new EndermanOrigin("enderman", "Эндермен"));
        OriginsRegistry.register(new WitherOrigin("wither", "Чумной доктор", plugin));
        OriginsRegistry.register(new VoidReaperOrigin("void_reaper", "Жнец пустоты", plugin));
    }
    /**
     * Активация эффектов рас
     */
    private void effInit() {
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Origin origin = getPlayerOrigin(player);
                    if (origin != null) {
                        origin.applyEffects(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 20L);
        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    Origin origin = getPlayerOrigin(player);
                    if (origin == OriginsRegistry.get("fish")) {
                        origin.applyEffects(player);
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 5L);
    }

    /**
     * Получение расы игрока
     *
     * @param player объект игрока на сервере
     */
    public static Origin getPlayerOrigin(Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        return pdc.getOrDefault(playerOrigin, OriginDataType.INSTANCE, OriginsRegistry.get("default"));
    }

    /**
     * Установка рассы игроку
     *
     * @param player объект игрока на сервере
     * @param origin объект рассы
     */
    public void setPlayerOrigin(Player player, Origin origin) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        if (origin != null) {
            pdc.set(playerOrigin, OriginDataType.INSTANCE, origin);
            origin.onGetOrigin(player);
        }
    }

}
