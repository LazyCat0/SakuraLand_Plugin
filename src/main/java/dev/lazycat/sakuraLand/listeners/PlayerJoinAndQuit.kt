package dev.lazycat.sakuraLand.listeners

import dev.lazycat.sakuraLand.SakuraLand
import dev.lazycat.sakuraLand.currency.coins.Coins
import dev.lazycat.sakuraLand.currency.sparks.Sparks
import dev.lazycat.sakuraLand.origins.Origin
import dev.lazycat.sakuraLand.origins.OriginsCore
import dev.lazycat.sakuraLand.origins.OriginsRegistry
import dev.lazycat.sakuraLand.origins.OriginsUtils
import dev.lazycat.sakuraLand.origins.pdct.OriginDataType
import fr.mrmicky.fastboard.adventure.FastBoard
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PlayerJoinAndQuit(private val plugin: SakuraLand) : Listener {
    @EventHandler
    fun onPlayerJoin(e: PlayerJoinEvent) {
        val player: Player = e.player
        val board = FastBoard(player)
        val mm: MiniMessage = MiniMessage.miniMessage()

        val header: Component = mm.deserialize("<gradient:#FF81CD:#7430E5>SakuraLand</gradient>")

        board.updateTitle(header)
        plugin.boards[player.uniqueId] = board

        player.sendPlayerListHeader(header)

        if (plugin.sparksInstance.get(player) == null)
            Sparks.applyCurrency(player)
        if (plugin.coinsInstance.get(player) == null)
            Coins.applyCurrency(player)

        val pdc = player.persistentDataContainer
        val origin: Origin = pdc.get(OriginsCore.playerOrigin, OriginDataType.INSTANCE) ?: run {
            val randomOrigin = OriginsUtils.RandomOrigin.getRandomOrigin()
            pdc.set(OriginsCore.playerOrigin, OriginDataType.INSTANCE, randomOrigin)
            randomOrigin.onGetOrigin(player)
            player.sendMessage(mm.deserialize("<gold>Вы впервые зашли. Ваша судьба - <origin>",
                Placeholder.parsed("origin", randomOrigin.displayName)
                ))
            randomOrigin
        }

    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val board: FastBoard? = plugin.boards.remove(event.getPlayer().uniqueId)
        board?.delete()
    }
}