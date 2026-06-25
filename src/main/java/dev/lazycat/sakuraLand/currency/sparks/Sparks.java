package dev.lazycat.sakuraLand.currency.sparks;

import dev.lazycat.sakuraLand.SakuraLand;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

import static dev.lazycat.sakuraLand.SakuraLand.getSparksInstance0;

public class Sparks {
    private final NamespacedKey sparksKey;
    public final Integer MIN_CURRENCY = -1000000;

    public Sparks(SakuraLand plugin) {
        sparksKey = new NamespacedKey(plugin, "currency_sparks");
    }

    public static void applyCurrency(@NonNull Player player) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(getSparksInstance0().sparksKey, PersistentDataType.INTEGER, 0);
    }
    public Integer get(@NonNull Player player) {
        return player.getPersistentDataContainer().getOrDefault(sparksKey, PersistentDataType.INTEGER, 0);
    }
    public void set(@NonNull Player player, Integer value) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(sparksKey, PersistentDataType.INTEGER, value);
    }
    public void set(@NonNull String player, String value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int val = Integer.parseInt(value);

        PersistentDataContainer pdc = p1.getPersistentDataContainer();
        pdc.set(sparksKey, PersistentDataType.INTEGER, val);
    }
    public void add(@NonNull Player player, Integer value) {
        PersistentDataContainer pdc = player.getPersistentDataContainer();
        pdc.set(sparksKey, PersistentDataType.INTEGER, get(player) + value);
    }
    public void add(@NonNull String player, Integer value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        PersistentDataContainer pdc = p1.getPersistentDataContainer();
        set(p1, get(p1) + value);
    }
    public void add(@NonNull String player, String value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int val = Integer.parseInt(value);

        set(p1, get(p1) + val);
    }

    public void decrease(@NonNull Player player, Integer value) {
        int current = get(player);
        int newBal = current - value;

        int capped = Math.max(newBal, MIN_CURRENCY);

        set(player, capped);
    }
    public void decrease(@NonNull String player, Integer value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int current = get(p1);
        int newBal = current - value;

        int capped = Math.max(newBal, MIN_CURRENCY);

        set(p1, capped);
    }
    public void decrease(@NonNull String player, String value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int val = Integer.parseInt(value);
        int current = get(p1);
        int newBal = current - val;

        int capped = Math.max(newBal, MIN_CURRENCY);

        set(p1, capped);
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
        decrease(player, value);
        return true;
    }
    public boolean waste(@NonNull String player, String value) {
        Player p1 = Objects.requireNonNull(Bukkit.getPlayer(player));
        int val = Integer.parseInt(value);
        int current = get(p1);
        if (current - val < 0)
            return false;
        decrease(player, value);
        return true;
    }
}
