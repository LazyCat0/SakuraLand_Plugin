package dev.lazycat.sakuraLand.origins;

import dev.lazycat.sakuraLand.SakuraLand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
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

/**
 * Технические утилиты рас, с командами. Будет в будущем расширятся
 */
public class OriginsUtils {
    private static final MiniMessage mm = MiniMessage.miniMessage();
    public static class OriginCommand implements CommandExecutor {
        private final SakuraLand plugin;
        public OriginCommand(SakuraLand plugin) {
            this.plugin = plugin;
        }
        @Override
        public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String @NotNull [] args) {
            String act = args[0];
            String player = args[1];
            String originId = args[2];

            switch (act) {
                case "list" -> {
                    commandSender.sendMessage(mm.deserialize("<gray>============ [ <yellow>Список зарегистрированных рас</yellow> ] ============"));

                    if (OriginsRegistry.getAllOrigins().isEmpty()) {
                        commandSender.sendMessage(mm.deserialize("<red>В реестре нет ни одной зарегистрированной расы</red>"));
                        return true;
                    }

                    for (Origin origin : OriginsRegistry.getAllOrigins()) {
                        commandSender.sendMessage(mm.deserialize(
                                "<gold>ID:</gold> <yellow><id></yellow> <gray>|</gray> " +
                                        "<gold>Имя:</gold> <green><name></green> <gray>|</gray> " +
                                        "<gold>Класс:</gold> <aqua><class_name></aqua>",
                                Placeholder.parsed("id", origin.getId()),
                                Placeholder.parsed("name", origin.getDisplayName()),
                                Placeholder.parsed("class_name", origin.getClass().getName()) // Выведет dev.lazycat...ClassName
                        ));
                    }

                    commandSender.sendMessage(mm.deserialize("<gray>==========================================================="));
                }
                case "set" -> {
                    Player player1 = Bukkit.getPlayer(player);
                    Origin origin = OriginsRegistry.get(originId);
                    if (player.isEmpty()) {
                        commandSender.sendMessage(mm.deserialize("<red>Имя игрока в команде НЕ может быть пустым."));
                        return true;
                    }
                    if (player1 == null) {
                        commandSender.sendMessage(mm.deserialize("<red>Игрок не найден."));
                        return true;
                    }
                    if (origin == null) {
                        plugin.getOriginsCore().setPlayerOrigin(player1, OriginsRegistry.get("default"));
                        commandSender.sendMessage(mm.deserialize("<yellow>По причине отсутсвия ID рассы или она не найдена, была установлена расса по умолчанию (человек)."));
                        return true;
                    }
                    plugin.getOriginsCore().setPlayerOrigin(player1, OriginsRegistry.get("default"));
                    Bukkit.getScheduler().runTaskLater(plugin, () -> plugin.getOriginsCore().setPlayerOrigin(player1, origin), 5L);
                    commandSender.sendMessage(mm.deserialize(
                            "<gold>Игроку <b><target></b> успешно выдана раса <name>",
                            Placeholder.parsed("target", player1.getName()),
                            Placeholder.parsed("name", origin.getDisplayName())
                    ));
                    return true;
                }
                default -> {return false;}
            }
            return true;
        }
    }
    public static class OriginCommandCompleter implements TabCompleter {
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
                ArrayList<String> list = new ArrayList<>();
                list.add("list");
                list.add("set");
                return list;
            }
            if (args.length == 3) {
                ArrayList<String> list = new ArrayList<>();
                list.add("origin_id");
                return list;
            }
            return new ArrayList<>();
        }
    }
    public static class CastZCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] args) {
            if (!(sender instanceof Player player)) return true;

            Origin pdcOrigin = OriginsCore.getPlayerOrigin(player);
            if (pdcOrigin == null) return true;

            Origin factoryOrigin = OriginsRegistry.get(pdcOrigin.getId());
            if (factoryOrigin != null) {
                factoryOrigin.abilityZExecute(player);
            }
            return true;
        }
    }
    public static class CastXCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] args) {
            if (!(sender instanceof Player player)) return true;

            Origin pdcOrigin = OriginsCore.getPlayerOrigin(player);
            if (pdcOrigin == null) return true;

            Origin factoryOrigin = OriginsRegistry.get(pdcOrigin.getId());
            if (factoryOrigin != null) {
                factoryOrigin.abilityXExecute(player);
            }
            return true;
        }
    }
    public static class CastCCommand implements CommandExecutor {
        @Override
        public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NonNull [] args) {
            if (!(sender instanceof Player player)) return true;

            Origin pdcOrigin = OriginsCore.getPlayerOrigin(player);
            if (pdcOrigin == null) return true;

            Origin factoryOrigin = OriginsRegistry.get(pdcOrigin.getId());
            if (factoryOrigin != null) {
                factoryOrigin.abilityCExecute(player);
            }
            return true;
        }
    }
}
