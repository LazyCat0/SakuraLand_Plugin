package dev.lazycat.sakuraLand.boxes;

import dev.lazycat.sakuraLand.SakuraLand;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public abstract class Box {
    private final String id;
    private final Component displayName;
    private final BoxType boxType;
    private final SakuraLand plugin;
    public Box(String id, Component displayName, BoxType boxType, SakuraLand plugin) {
        this.id = id.toLowerCase();
        this.displayName = displayName;
        this.boxType = boxType;
        this.plugin = plugin;
    }
    public String getId() {
        return id;
    }
    public Component getDisplayName() {
        return displayName;
    }
    public BoxType getBoxType() {
        return boxType;
    }

    public abstract void onWin(@NotNull Player player);

}
