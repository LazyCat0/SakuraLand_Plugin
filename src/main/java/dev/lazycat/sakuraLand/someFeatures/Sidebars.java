package dev.lazycat.sakuraLand.someFeatures;


import dev.lazycat.sakuraLand.SakuraLand;
import dev.lazycat.sakuraLand.origins.Origin;
import dev.lazycat.sakuraLand.origins.OriginsRegistry;
import dev.lazycat.sakuraLand.origins.pdct.OriginDataType;
import fr.mrmicky.fastboard.adventure.FastBoard;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;

import static dev.lazycat.sakuraLand.origins.OriginsCore.playerOrigin;

public class Sidebars {
    /**
     * Сайдбоард с информацией о вашем состоянии
     * @param board досочка
     * @param plugin ссылка на плагин
     * @apiNote используется FastBoard
     */
    public static void updateBoard(FastBoard board, SakuraLand plugin) {
        Player player = board.getPlayer();
        MiniMessage mm = MiniMessage.miniMessage();

        PersistentDataContainer pdc = player.getPersistentDataContainer();
        Origin origin = pdc.getOrDefault(playerOrigin, OriginDataType.INSTANCE, OriginsRegistry.get("default"));

        board.updateLines(
                Component.text(""),
                mm.deserialize("<white>Добро пожаловать, <gold><p><gold>!</white>", Placeholder.parsed("p", player.getName())),
                mm.deserialize("<white>Ваш ранг - <rank>", Placeholder.parsed("rank", "ВЗЗ")),
                mm.deserialize("<white>У вас <gold><value> искорок</gold>", Placeholder.component("value", Component.text(plugin.getSparksInstance().get(player)))),
                mm.deserialize("<white>У вас <gold><value>/<coins_limit> монет</gold>",
                        Placeholder.component("value", Component.text(plugin.getCoinsInstance().get(player))),
                        Placeholder.component("coins_limit", Component.text(plugin.getCoinsInstance().MAX_CURRENCY))

                ),
                mm.deserialize("<white>————————————"),
                mm.deserialize("<gray>Ваш пинг - <p>", Placeholder.component("p", Component.text(player.getPing()))),
                mm.deserialize("<gray>Онлайн сервера - <o>", Placeholder.component("o", Component.text(Bukkit.getOnlinePlayers().size()))),
                mm.deserialize("<gray>Ваша судьба - <o>", Placeholder.parsed("o", origin.getDisplayName())),
                Component.text(""),
                Component.text("server-ip").color(NamedTextColor.YELLOW)
        );
    }
}
