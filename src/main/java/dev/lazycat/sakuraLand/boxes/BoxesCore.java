package dev.lazycat.sakuraLand.boxes;

import com.destroystokyo.paper.profile.PlayerProfile;
import dev.lazycat.sakuraLand.SakuraLand;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.Base64;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BoxesCore {
    private final SakuraLand plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private OrbitAnimation animation;

    private ItemDisplay defaultBoxEnt;
    private ItemDisplay rareBoxEnt;
    private ItemDisplay legendBoxEnt;
    private ItemDisplay ultraBoxEnt;
    private ItemDisplay neoBoxEnt;



    public BoxesCore(SakuraLand plugin) {
        this.plugin = plugin;
    }
    public void init() {

    }
    public void idleAnim(Location location) {
        YamlConfiguration config = (YamlConfiguration) plugin.getConfig();

        defaultBoxEnt = location.getWorld().spawn(location, ItemDisplay.class, ent -> {
            ent.setItemStack(createPlayerHead(config.getString("boxes.common.texture")));
            ent.setInterpolationDuration(20);
            ent.setInterpolationDelay(0);
            ent.customName(mm.deserialize("common box"));
        });
        rareBoxEnt = location.getWorld().spawn(location, ItemDisplay.class, ent -> {
            ent.setItemStack(createPlayerHead(config.getString("boxes.rare.texture")));
            ent.setInterpolationDuration(20);
            ent.setInterpolationDelay(0);
            ent.customName(mm.deserialize("<green>Rare box</green>"));
        });
        legendBoxEnt = location.getWorld().spawn(location, ItemDisplay.class, ent -> {
            ent.setItemStack(createPlayerHead(config.getString("boxes.legendary.texture")));
            ent.setInterpolationDuration(20);
            ent.setInterpolationDelay(0);
            ent.customName(mm.deserialize("<red>Legendary box</red>"));
        });
        ultraBoxEnt = location.getWorld().spawn(location, ItemDisplay.class, ent -> {
            ent.setItemStack(createPlayerHead(config.getString("boxes.ultra.texture")));
            ent.setInterpolationDuration(20);
            ent.setInterpolationDelay(0);
            ent.customName(mm.deserialize("<black>Ultra Box</black>"));
        });
        neoBoxEnt = location.getWorld().spawn(location, ItemDisplay.class, ent -> {
            ent.setItemStack(createPlayerHead(config.getString("boxes.neobox.texture")));
            ent.setInterpolationDuration(20);
            ent.setInterpolationDelay(0);
            ent.customName(mm.deserialize("<gold>Neo Box</gold>"));
        });


        List<ItemDisplay> displays = List.of(
                defaultBoxEnt,
                rareBoxEnt,
                legendBoxEnt,
                ultraBoxEnt,
                neoBoxEnt
        );
        animation = new OrbitAnimation(
                plugin, location, displays, 2.0, 0.05, 0.5
        );
        animation.start();
    }
    public void idleAnimStop() {
        animation.stop();
    }


    private static final Pattern URL_PATTERN = Pattern.compile("\"url\"\\s*:\\s*\"(https?://[^\"]+)\"");
    public static ItemStack createPlayerHead(String input) {
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) head.getItemMeta();

        PlayerProfile profile = Bukkit.createProfile(UUID.randomUUID(), null);
        PlayerTextures textures = profile.getTextures();

        try {
            if (input.startsWith("http://") || input.startsWith("https://")) {
                textures.setSkin(URI.create(input).toURL());
                profile.setTextures(textures);
            }
            else {
                String decodedJson = new String(Base64.getDecoder().decode(input));
                String skinUrl = extractUrlFromJson(decodedJson);
                if (skinUrl != null) {
                    textures.setSkin(URI.create(skinUrl).toURL());
                    profile.setTextures(textures);
                } else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(input);
                    if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
                        profile = offlinePlayer.getPlayerProfile();
                    } else {
                        return head;
                    }
                }
            }
        } catch (IllegalArgumentException | MalformedURLException e) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(input);
            if (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()) {
                profile = offlinePlayer.getPlayerProfile();
            } else {
                return head;
            }
        }

        meta.setPlayerProfile(profile);
        head.setItemMeta(meta);
        return head;
    }

    private static String extractUrlFromJson(String json) {
        Matcher matcher = URL_PATTERN.matcher(json);
        return matcher.find() ? matcher.group(1) : null;
    }
}
