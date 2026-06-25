package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.origins.Origin;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.SplashPotion;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.sound.Sound;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class WitchOrigin extends Origin {

    // Отдельные мапы для каждой способности
    private final HashMap<UUID, Long> cooldownZ = new HashMap<>();
    private final HashMap<UUID, Long> cooldownX = new HashMap<>();
    private final HashMap<UUID, Long> cooldownC = new HashMap<>();

    private final long cooldownTimeZ = 12 * 1000;
    private final long cooldownTimeX = 18 * 1000;
    private final long cooldownTimeC = 3 * 1000;

    private final MiniMessage mm = MiniMessage.miniMessage();

    public WitchOrigin(String id, String displayName) {
        super(id, displayName);
    }

    private final List<PotionType> potions = List.of(
            PotionType.HEALING,
            PotionType.POISON,
            PotionType.STRONG_HARMING,
            PotionType.STRONG_POISON,
            PotionType.STRONG_SLOWNESS
    );

    public void throwRandomPotion(Player player, boolean isLingering) {
        if (potions.isEmpty()) return;

        int randomIndex = ThreadLocalRandom.current().nextInt(potions.size());
        PotionType randomType = potions.get(randomIndex);

        Material material = isLingering ? Material.LINGERING_POTION : Material.SPLASH_POTION;
        ItemStack potionItem = new ItemStack(material);

        PotionMeta meta = (PotionMeta) potionItem.getItemMeta();
        if (meta != null) {
            meta.setBasePotionType(randomType);
            potionItem.setItemMeta(meta);
        }

        Location launchLocation = player.getEyeLocation();
        SplashPotion splashPotion = player.getWorld().spawn(launchLocation, SplashPotion.class, potion -> {
            potion.setItem(potionItem);
            potion.setShooter(player);
        });

        splashPotion.setVelocity(launchLocation.getDirection().multiply(0.5));
    }


    @Override
    public void abilityZExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownZ)) {
            long secondsLeft = getRemainingCooldown(player, cooldownZ);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 1 ещё на перезарядке - <white><sec></white> секунд",
                    Placeholder.parsed("sec", String.valueOf(secondsLeft))
            ));
            return;
        }
        player.swingMainHand();
        throwRandomPotion(player, false);
        setCooldown(player, cooldownZ, cooldownTimeZ);
    }

    @Override
    public void abilityXExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownX)) {
            long secondsLeft = getRemainingCooldown(player, cooldownX);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 2 ещё на перезарядке - <white><sec></white> секунд",
                    Placeholder.parsed("sec", String.valueOf(secondsLeft))
            ));
            return;
        }
        player.swingMainHand();
        throwRandomPotion(player, true);
        setCooldown(player, cooldownX, cooldownTimeX);
    }

    @Override
    public void abilityCExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownC)) {
            long secondsLeft = getRemainingCooldown(player, cooldownC);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 3 ещё на перезарядке - <white><sec></white> секунд",
                    Placeholder.parsed("sec", String.valueOf(secondsLeft))
            ));
            return;
        }
        player.swingMainHand();
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 4000, 0));
        Sound drink = Sound.sound(
                Key.key("entity.generic.drink"),
                Sound.Source.PLAYER,
                1.0f,
                1.0f
        );
        player.getWorld().playSound(drink);
        setCooldown(player, cooldownC, cooldownTimeC);
    }


    private boolean isCooldownActive(Player player, HashMap<UUID, Long> cooldownMap) {
        if (!cooldownMap.containsKey(player.getUniqueId())) return false;
        return cooldownMap.get(player.getUniqueId()) > System.currentTimeMillis();
    }

    private void setCooldown(Player player, HashMap<UUID, Long> cooldownMap, long time) {
        cooldownMap.put(player.getUniqueId(), System.currentTimeMillis() + time);
    }

    private long getRemainingCooldown(Player player, HashMap<UUID, Long> cooldownMap) {
        if (!cooldownMap.containsKey(player.getUniqueId())) return 0;
        long timeLeft = cooldownMap.get(player.getUniqueId()) - System.currentTimeMillis();
        return Math.max(0, timeLeft / 1000);
    }
}