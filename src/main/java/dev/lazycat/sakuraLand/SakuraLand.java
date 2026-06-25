package dev.lazycat.sakuraLand;

import dev.lazycat.sakuraLand.boxes.BoxesCore;
import dev.lazycat.sakuraLand.currency.coins.Coins;
import dev.lazycat.sakuraLand.currency.coins.commands.CoinsC;
import dev.lazycat.sakuraLand.currency.sparks.Sparks;
import dev.lazycat.sakuraLand.currency.sparks.commands.SparksC;
import dev.lazycat.sakuraLand.develop.DevsMeanings;
import dev.lazycat.sakuraLand.listeners.PlayerEarnsCoins;
import dev.lazycat.sakuraLand.listeners.PlayerJoinAndQuit;
import dev.lazycat.sakuraLand.origins.Origin;
import dev.lazycat.sakuraLand.origins.OriginsCore;
import dev.lazycat.sakuraLand.origins.OriginsRegistry;
import dev.lazycat.sakuraLand.origins.OriginsUtils;
import dev.lazycat.sakuraLand.origins.originsListeners.IfritDamageEventListener;
import dev.lazycat.sakuraLand.someFeatures.Sidebars;
import fr.mrmicky.fastboard.adventure.FastBoard;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class SakuraLand extends JavaPlugin {
    private final Map<UUID, FastBoard> boards = new HashMap<>();
    private static Sparks sparksInstance;
    private static Coins coinsInstance;
    private static OriginsCore originsCore;
    private static BoxesCore boxesCore;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        sparksInstance = new Sparks(this);
        coinsInstance = new Coins(this);
        originsCore = new OriginsCore(this);
        boxesCore = new BoxesCore(this);

        getServer().getPluginManager().registerEvents(new PlayerJoinAndQuit(this), this);
        getServer().getPluginManager().registerEvents(new PlayerEarnsCoins(this), this);
        getServer().getPluginManager().registerEvents(new IfritDamageEventListener(this), this);
        this.initCommands();

        for (Origin origin : OriginsRegistry.getAllOrigins()) {
            origin.registerNetwork(this);
        }

        getServer().getScheduler().runTaskTimer(this, () -> {
            for (FastBoard board : this.boards.values()) {
                Sidebars.updateBoard(board, this);
            }
        }, 0L, 20L);
        getServer().getScheduler().runTaskTimer(this, () ->
        {
            for (Player players : Bukkit.getOnlinePlayers()) {
                DevsMeanings.showActionbar(players);
            }
        }, 0L, 40L);
    }
    @Override
    public void onDisable() {
        for (Origin origin : OriginsRegistry.getAllOrigins()) {
            origin.unregisterNetwork(this);
        }
    }

    public Map<UUID, FastBoard> getBoards() {
        return boards;
    }


    public static Sparks getSparksInstance0() {
        return sparksInstance;
    }

    public Sparks getSparksInstance() {
        return sparksInstance;
    }

    public static Coins getCoinsInstance0() {
        return coinsInstance;
    }

    public Coins getCoinsInstance() {
        return coinsInstance;
    }
    public OriginsCore getOriginsCore() {
        return originsCore;
    }

    public BoxesCore getBoxesCore() {
        return boxesCore;
    }

    private void initCommands() {
        Objects.requireNonNull(getCommand("coins")).setExecutor(new CoinsC.CoinsCommand(this));
        Objects.requireNonNull(getCommand("sparks")).setExecutor(new SparksC.SparksCommand(this));
        Objects.requireNonNull(getCommand("coins")).setTabCompleter(new CoinsC.CoinsCommandCompleter());
        Objects.requireNonNull(getCommand("sparks")).setTabCompleter(new SparksC.SparksCommandCompleter());
        Objects.requireNonNull(getCommand("origin")).setExecutor(new OriginsUtils.OriginCommand(this));
        Objects.requireNonNull(getCommand("origin")).setTabCompleter(new OriginsUtils.OriginCommandCompleter());
        Objects.requireNonNull(getCommand("cast-z")).setExecutor(new OriginsUtils.CastZCommand());
        Objects.requireNonNull(getCommand("cast-x")).setExecutor(new OriginsUtils.CastXCommand());
        Objects.requireNonNull(getCommand("cast-c")).setExecutor(new OriginsUtils.CastCCommand());
    }
}
