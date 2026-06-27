package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ItemType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class OmnivoreOrigin extends Origin {
    public OmnivoreOrigin(String id, String displayName) {
        super(id, displayName);
    }

    private final MiniMessage mm = MiniMessage.miniMessage();

    private final Set<Material> bannedItems = Stream.of(
                    Tag.ITEMS_PICKAXES,
                    Tag.ITEMS_AXES,
                    Tag.ITEMS_SHOVELS,
                    Tag.ITEMS_HOES,
                    Tag.ITEMS_SWORDS,
                    Tag.ITEMS_CHEST_ARMOR,
                    Tag.ITEMS_LEG_ARMOR,
                    Tag.ITEMS_HEAD_ARMOR,
                    Tag.ITEMS_FOOT_ARMOR
            )
            .flatMap(tag -> tag.getValues().stream())
            .filter(Material::isItem)
            .collect(Collectors.toUnmodifiableSet());
    private final List<Material> bannedItems1 = List.of(
            Material.AIR
    );

    private final List<Material> freezeItems = List.of(
            Material.ICE,
            Material.BLUE_ICE,
            Material.FROSTED_ICE,
            Material.PACKED_ICE,
            Material.SNOW,
            Material.SNOWBALL,
            Material.SNOW_BLOCK
    );
    private final List<Material> burnItems = List.of(
            Material.TORCH,
            Material.LAVA_BUCKET,
            Material.MAGMA_BLOCK,
            Material.MAGMA_CREAM
    );
    private final List<Material> glowItems = List.of(
            Material.GLASS,
            Material.GLASS_PANE,
            Material.GLOWSTONE,
            Material.GLOW_BERRIES
    );

    @Override
    public void abilityZExecute(@NotNull Player player) {
        ItemStack item = player.getInventory().getItemInMainHand();
        Material i = item.getType();

        if (player.getFoodLevel() >= 20) {
            player.sendActionBar(mm.deserialize("<red> == Вы не голодны == "));
            return;
        }
        if (bannedItems.contains(i) || bannedItems1.contains(i)) {
            player.sendActionBar(mm.deserialize("<red> == Вы не можете даже прожевать этот предмет... == "));
            return;
        }
        if (freezeItems.contains(i))
            player.setFreezeTicks(280);
        if (burnItems.contains(i))
            player.setFireTicks(280);
        if (glowItems.contains(i))
            player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 30, 1));

        item.setAmount(item.getAmount() - 1);
        player.setFoodLevel(player.getFoodLevel() + 1);
    }
}
