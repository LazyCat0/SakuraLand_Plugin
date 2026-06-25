package dev.lazycat.sakuraLand.listeners

import dev.lazycat.sakuraLand.SakuraLand
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.attribute.Attribute
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import kotlin.math.sqrt

class PlayerEarnsCoins(val plugin: SakuraLand) : Listener {
    private val mm: MiniMessage = MiniMessage.miniMessage()
    private val blacklistedEntities = setOf(
        EntityType.PLAYER,
        EntityType.ENDERMITE,
        EntityType.VILLAGER,
        EntityType.ZOMBIE_HORSE,
        EntityType.ZOMBIE_VILLAGER,
        EntityType.ZOMBIFIED_PIGLIN,
        EntityType.ARMOR_STAND,
        EntityType.ITEM_FRAME,
        EntityType.GLOW_ITEM_FRAME,
        EntityType.WANDERING_TRADER,
        EntityType.IRON_GOLEM,
        EntityType.ITEM,
        EntityType.ALLAY,
        EntityType.AXOLOTL,
        EntityType.CREAKING,
        EntityType.EVOKER,
        EntityType.VEX,
        EntityType.PILLAGER,
        EntityType.FURNACE_MINECART,
        EntityType.MINECART,
        EntityType.TNT_MINECART,
        EntityType.CHEST_MINECART,
        EntityType.HOPPER_MINECART,
        EntityType.COMMAND_BLOCK_MINECART,
        EntityType.SPAWNER_MINECART,
        EntityType.OMINOUS_ITEM_SPAWNER,
        EntityType.SILVERFISH,
        EntityType.ILLUSIONER,
        EntityType.WOLF,
        EntityType.END_CRYSTAL
    )

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val deadEntity: LivingEntity = event.entity

        if (deadEntity.type in blacklistedEntities) return

        val player: Player = deadEntity.killer ?: return

        val maxHealth = deadEntity.getAttribute(Attribute.MAX_HEALTH)?.value ?: 20.0
        val droppedExp = event.droppedExp.toDouble()

        val rawCoins = (sqrt(droppedExp) * 9.3) + (maxHealth / 10.0) - 11.0
        val secretValue: Int = rawCoins.toInt().coerceIn(1, 1024)

        plugin.coinsInstance.add(player, secretValue)

        player.sendMessage(mm.deserialize(
            "<gold>Начислено <white><coins></white> монет за убийство <white><mob></white>",
            Placeholder.parsed("coins", secretValue.toString()),
            Placeholder.parsed("mob", deadEntity.name)
        ))
    }
}