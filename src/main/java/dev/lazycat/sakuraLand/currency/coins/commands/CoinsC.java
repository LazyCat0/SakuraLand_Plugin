package dev.lazycat.sakuraLand.currency.coins.commands;

import dev.lazycat.sakuraLand.SakuraLand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CoinsC {
    public static class CoinsCommand implements CommandExecutor {
        private final SakuraLand plugin;
        private final MiniMessage mm = MiniMessage.miniMessage();
        public CoinsCommand(SakuraLand plugin) {
            this.plugin = plugin;
        }
        @Override
        public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
            if (args.length < 3) {
                return true;
            }
            String action = args[0];
            String player = args[1];
            String value = args[2];
            switch (action) {
                case "waste" -> {
                    if (player.isEmpty() || value.isEmpty()) {
                        commandSender.sendMessage(mm.deserialize("<red>Вы не можете НЕ указывать число которое убавлять и/или игрока у которого убавлять эти самые деньги."));
                        return false;
                    }
                    plugin.getCoinsInstance().waste(player, value);
                    commandSender.sendMessage(mm.deserialize("<gold>Вы успешно потратили <value> монет у игрока с никнеймом <player>"));
                    return true;
                }
                case "add" -> {
                    if (player.isEmpty() || value.isEmpty()) {
                        commandSender.sendMessage(mm.deserialize("<red>Вы не можете НЕ указывать число которое прибавлять и/или игрока которому прибавлять эти самые деньги."));
                        return false;
                    }
                    plugin.getCoinsInstance().add(player, value);
                    commandSender.sendMessage(mm.deserialize("<gold>Вы успешно дали <value> монет игроку с никнеймом <player>"));
                    return true;
                }
                case "set" -> {
                    if (player.isEmpty() || value.isEmpty()) {
                        commandSender.sendMessage(mm.deserialize("<red>Вы не можете НЕ указывать число которое ставить и/или игрока которому ставить эти самые деньги."));
                        return false;
                    }
                    plugin.getCoinsInstance().set(player, value);
                    commandSender.sendMessage(mm.deserialize("<gold>Вы успешно поставили <value> монет игроку с никнеймом <player>"));
                    return true;
                }
                default -> {
                    return false;
                }
            }
        }
    }
    public static class CoinsCommandCompleter implements TabCompleter {
        @Override
        public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String @NonNull [] args) {
            if (args.length == 2) {
                String partial = args[1].toLowerCase();
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(partial))
                        .collect(Collectors.toList());
            }
            if (args.length == 1) {
                ArrayList<String> list = new ArrayList<String>();
                list.add("add");
                list.add("waste");
                list.add("set");
                return list;
            }
            return new ArrayList<>();
        }
    }
}
