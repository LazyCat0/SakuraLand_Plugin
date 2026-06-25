package dev.lazycat.sakuraLand.listeners

import dev.lazycat.sakuraLand.SakuraLand
import dev.lazycat.sakuraLand.currency.coins.Coins
import dev.lazycat.sakuraLand.currency.stars.Stars
import fr.mrmicky.fastboard.adventure.FastBoard
import net.kyori.adventure.text.minimessage.MiniMessage
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
        board.updateTitle(mm.deserialize("<gold>\"SakuraLand\"<gold>"))
        plugin.boards[player.uniqueId] = board

        if (plugin.starsInstance.getCurrency(player) == null)
            Stars.applyCurrency(player)
        if (plugin.coinsInstance.getCurrency(player) == null)
            Coins.applyCurrency(player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        val board: FastBoard? = plugin.boards.remove(event.getPlayer().uniqueId)
        board?.delete()
    }
}