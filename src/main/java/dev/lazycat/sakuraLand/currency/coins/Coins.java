package dev.lazycat.sakuraLand.currency.coins;

import dev.lazycat.sakuraLand.SakuraLand;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

import static dev.lazycat.sakuraLand.SakuraLand.getCoinsInstance0;

public class Coins {
    public final NamespacedKey coinsKey;
    public final Integer MAX_CURRENCY = 1024;
    public final Integer MIN_CURRENCY = -36864;

    public Coins (SakuraLand plugin) {
        coinsKey = new NamespacedKey(plugin, "currency_coins");
    }

    public static void applyCurrency(@NonNull Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(getCoinsInstance0().coinsKey, PersistentDataType.INTEGER, 0);
    }

    public Integer get(@NonNull Player player) {
        return player.getPersistentDataContainer().getOrDefault(coinsKey, PersistentDataType.INTEGER, 0);
    }
    public void set(@NonNull Player player, Integer value) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(coinsKey, PersistentDataType.INTEGER, value);
    }
    public void set(@NonNull String player, Integer value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));

        PersistentDataContainer pdc = p1.getPersistentDataContainer();
        pdc.set(coinsKey, PersistentDataType.INTEGER, value);
    }
    public void set(@NonNull String player, String value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int val = Integer.parseInt(value);

        PersistentDataContainer pdc = p1.getPersistentDataContainer();
        pdc.set(coinsKey, PersistentDataType.INTEGER, val);
    }
    public void add(@NonNull Player player, Integer value) {
        int current = get(player);
        int newBal = current + value;

        int capped = Math.min(newBal, MAX_CURRENCY);
        capped = Math.max(capped, MIN_CURRENCY);

        set(player, capped);
    }

    public void add(@NonNull String player, Integer value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));

        int current = get(p1);
        int newBal = current + value;

        int capped = Math.min(newBal, MAX_CURRENCY);
        capped = Math.max(capped, MIN_CURRENCY);

        set(p1, capped);
    }
    public void add(@NonNull String player, String value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int val = Integer.parseInt(value);

        int current = get(p1);
        int newBal = current + val;

        int capped = Math.min(newBal, MAX_CURRENCY);
        capped = Math.max(capped, MIN_CURRENCY);

        set(p1, capped);
    }
    public void decrease(@NonNull Player player, Integer value) {
        int current = get(player);
        int newBal = current - value;

        int capped = Math.max(newBal, MIN_CURRENCY);

        set(player, capped);
    }
    public void decrease(@NonNull String player, String value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int val = Integer.parseInt(value);
        int current = get(p1);
        int newBal = current - val;

        int capped = Math.max(newBal, MIN_CURRENCY);

        set(player, capped);
    }

    public boolean waste(@NonNull Player player, Integer value) {
        int current = get(player);
        if (current - value < 0)
            return false;
        decrease(player, value);
        return true;
    }
    public boolean waste(@NonNull String player, Integer value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int current = get(p1);
        if (current - value < 0)
            return false;
        decrease(p1, value);
        return true;
    }
    public void waste(@NonNull String player, String value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int val = Integer.parseInt(value);
        int current = get(p1);
        if (current - val < 0)
            return;
        decrease(p1, val);
    }
}
