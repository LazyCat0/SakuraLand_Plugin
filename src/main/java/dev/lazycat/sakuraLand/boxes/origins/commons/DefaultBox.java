package dev.lazycat.sakuraLand.boxes.origins.commons;

import dev.lazycat.sakuraLand.SakuraLand;
import dev.lazycat.sakuraLand.boxes.Box;
import dev.lazycat.sakuraLand.boxes.BoxType;
import dev.lazycat.sakuraLand.origins.OriginsRegistry;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultBox extends Box {
    private final SakuraLand plugin;
    public DefaultBox(String id, Component displayName, BoxType boxType, SakuraLand plugin) {
        super(id, displayName, boxType, plugin);
        this.plugin = plugin;
    }
    @Override
    public void onWin(@NotNull Player player) {
        plugin.getOriginsCore().setPlayerOrigin(player, OriginsRegistry.get("default"));
    }
}
