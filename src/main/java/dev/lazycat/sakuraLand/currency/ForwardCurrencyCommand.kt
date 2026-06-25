package dev.lazycat.sakuraLand.currency

import dev.lazycat.sakuraLand.SakuraLand
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class ForwardCurrencyCommand(val plugin: SakuraLand) : CommandExecutor, TabCompleter {
    private val mm: MiniMessage = MiniMessage.miniMessage()
    override fun onCommand(
        sender: CommandSender,
        cmd: Command,
        name: String,
        args: Array<out String>
    ): Boolean {
        if (args.size == 0) {
            sender.sendMessage(mm.deserialize("<red>Вы <b>не</b> можете отправить эту команду без аргументов"))
            return true
        }
        if (args.size == 1) {
            sender.sendMessage(mm.deserialize("<red>Вы <b>не</b> можете отправить эту команду без ещё <b>2-х</b> аргументов"))
            return true
        }
        if (args.size == 2) {
            sender.sendMessage(mm.deserialize("<red>Вы <b>не</b> можете отправить эту команду без ещё <b>1-о</b> аргумента"))
            return true
        }
        if (!Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(args[1]))) {
            sender.sendMessage(mm.deserialize("<red>Вы указали либо НЕ игрока вторым аргументом. Или игрок, которого вы указали, не на сервере"))
            return true
        }
        val number = args[2].toIntOrNull()
        if (number == null || number < 0) {
            sender.sendMessage(mm.deserialize("<red>Последний аргумент не может быть отрицательным или не целым числом"))
            return true
        }

        if (args.size == 3) {
            if (args[0] == "sparks") {
                plugin.sparksInstance.waste(sender as Player, number)
                plugin.sparksInstance.add(args[1], number)
                sender.sendMessage(mm.deserialize(
                    "<gold>Успешно переведено <white><value></white> искорок игроку <white><player></white>!",
                    Placeholder.parsed("value", number.toString()),
                    Placeholder.parsed("player", args[1])
                ))
                return true
            }
            if (args[0] == "coins") {
                plugin.coinsInstance.waste(sender as Player, number)

                var commission: Int = (number * 50 + 50) / 100

                if (commission >= number || number <= 0) {
                    commission = 0;
                }

                val total = number - commission

                plugin.coinsInstance.add(args[1], total)
                sender.sendMessage(mm.deserialize(
                    "<gold>Успешно переведено <white><value></white> монет с коммисией в 5% игроку <white><player></white>!",
                    Placeholder.parsed("value", number.toString()),
                    Placeholder.parsed("player", args[1])
                ))
                sender.sendMessage(mm.deserialize(
                    "<gold>Важно! Игрок (<white><player></white>) получит с учётом коммисии в 5% <white><value></white> монет.",
                    Placeholder.parsed("value", total.toString()),
                    Placeholder.parsed("player", args[1])
                ))
                return true
            }
            return true
        }
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        cmd: Command,
        alias: String,
        args: Array<out String>
    ): List<String?>? {
        TODO("Not yet implemented")
    }
}