package dev.lazycat.sakuraLand.develop;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.jetbrains.annotations.NotNull;

public class DevsMeanings {
    public static void showActionbar(final @NotNull Audience target) {
        final Component actionBar = MiniMessage.miniMessage().deserialize("<gray>Всё что показано - в разработке, и может <b>крайне сильно</b> изменится.");
        target.sendActionBar(actionBar);
    }
}
