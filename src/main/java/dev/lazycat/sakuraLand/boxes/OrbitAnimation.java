package dev.lazycat.sakuraLand.boxes;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class OrbitAnimation {

    private static final List<OrbitAnimation> activeAnimations = new ArrayList<>();

    private final Plugin plugin;
    private final Location center;
    private final List<ItemDisplay> displays;
    private final double radius;
    private final double speed;
    private final double heightOffset;
    private final double[] initialAngles;

    private int taskId = -1;
    private long tickCounter = 0;

    public OrbitAnimation(Plugin plugin, Location center, List<ItemDisplay> displays,
                          double radius, double speed, double heightOffset) {
        this.plugin = plugin;
        this.center = center.clone();
        this.displays = displays;
        this.radius = radius;
        this.speed = speed;
        this.heightOffset = heightOffset;

        this.initialAngles = new double[displays.size()];
        for (int i = 0; i < displays.size(); i++) {
            initialAngles[i] = (2 * Math.PI * i) / displays.size();
        }

        activeAnimations.add(this);
    }

    public void start() {
        if (taskId != -1) return;
        if (displays.isEmpty()) {
            Bukkit.getLogger().warning("OrbitAnimation: список сущностей пуст.");
            return;
        }

        taskId = new BukkitRunnable() {
            @Override
            public void run() {
                boolean anyAlive = false;
                for (ItemDisplay d : displays) {
                    if (d != null && d.isValid()) {
                        anyAlive = true;
                        break;
                    }
                }
                if (!anyAlive) {
                    stop();
                    return;
                }

                long currentTick = tickCounter++;
                for (int i = 0; i < displays.size(); i++) {
                    ItemDisplay display = displays.get(i);
                    if (display == null || !display.isValid()) continue;

                    double angle = initialAngles[i] + currentTick * speed;
                    double x = center.getX() + radius * Math.cos(angle);
                    double z = center.getZ() + radius * Math.sin(angle);

                    double y = center.getY();
                    if (heightOffset > 0) {
                        y += heightOffset * Math.sin(angle);
                    }

                    Location loc = new Location(center.getWorld(), x, y, z);
                    display.teleport(loc);
                }
            }
        }.runTaskTimer(plugin, 0L, 1L).getTaskId();

        Bukkit.getLogger().info("OrbitAnimation запущена для " + displays.size() + " сущностей.");
    }

    public void stop() {
        if (taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        activeAnimations.remove(this);
        Bukkit.getLogger().info("OrbitAnimation остановлена.");
    }

    public boolean isRunning() {
        return taskId != -1;
    }

    public static void stopAll() {
        List<OrbitAnimation> copy = new ArrayList<>(activeAnimations);
        for (OrbitAnimation anim : copy) {
            anim.stop();
        }
        activeAnimations.clear();
    }

    public static int getActiveCount() {
        return activeAnimations.size();
    }

    public static void clearAll() {
        activeAnimations.clear();
    }
}