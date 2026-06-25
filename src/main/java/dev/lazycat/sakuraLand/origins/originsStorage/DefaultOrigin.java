package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.SakuraLand;
import dev.lazycat.sakuraLand.boxes.OrbitAnimation;
import dev.lazycat.sakuraLand.origins.Origin;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class DefaultOrigin extends Origin {
    private final SakuraLand p;
    public DefaultOrigin(String id, String displayName, SakuraLand p) {
        super(id, displayName);
        this.p = p;
    }

    @Override
    public void abilityZExecute(@NotNull Player player) {
        p.getBoxesCore().idleAnim(player.getLocation());
    }
    @Override
    public void abilityXExecute(@NotNull Player player) {
        OrbitAnimation.stopAll();
        OrbitAnimation.clearAll();
    }
}
