package dev.lazycat.sakuraLand.origins.originsStorage;

import dev.lazycat.sakuraLand.SakuraLand;
import dev.lazycat.sakuraLand.origins.Origin;
import dev.lazycat.sakuraLand.someFeatures.AiCoded;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;
import java.util.HashMap;
import java.util.UUID;

@AiCoded(by = "@FB_cann0n", id = "7523927123", ai = "deepseek")

public class VoidReaperOrigin extends Origin {

    // Ключи для PDC и модификаторов
    private static final NamespacedKey SOULS_KEY = new NamespacedKey("sakuraland", "void_reaper_souls");
    private static final NamespacedKey SPEED_MOD_KEY = new NamespacedKey("sakuraland", "void_reaper_speed");
    private static final NamespacedKey DAMAGE_MOD_KEY = new NamespacedKey("sakuraland", "void_reaper_damage");

    private final SakuraLand plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    // Карты кулдаунов для каждой способности
    private final HashMap<UUID, Long> cooldownZ = new HashMap<>();
    private final HashMap<UUID, Long> cooldownX = new HashMap<>();
    private final HashMap<UUID, Long> cooldownC = new HashMap<>();

    // Время перезарядки в миллисекундах
    private static final long Z_CD = 25 * 1000L;  // 25 секунд
    private static final long X_CD = 60 * 1000L;  // 60 секунд
    private static final long C_CD = 120 * 1000L; // 120 секунд

    public VoidReaperOrigin(String id, String displayName, SakuraLand plugin) {
        super(id, displayName);
        this.plugin = plugin;
    }

    // ----- Работа с душами (статичные методы для удобства) -----
    public static int getSouls(Player player) {
        return player.getPersistentDataContainer().getOrDefault(SOULS_KEY, PersistentDataType.INTEGER, 0);
    }

    public static void setSouls(Player player, int souls) {
        int clamped = Math.min(souls, 10);
        player.getPersistentDataContainer().set(SOULS_KEY, PersistentDataType.INTEGER, clamped);
    }

    public static void addSoul(Player player) {
        int current = getSouls(player);
        if (current < 10) {
            setSouls(player, current + 1);
        }
    }

    public static void resetSouls(Player player) {
        setSouls(player, 0);
    }

    // ----- Вспомогательные методы для модификаторов -----
    private void removeSpeedModifier(Player player) {
        AttributeInstance speed = player.getAttribute(Attribute.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(SPEED_MOD_KEY);
        }
    }

    private void removeDamageModifier(Player player) {
        AttributeInstance damage = player.getAttribute(Attribute.ATTACK_DAMAGE);
        if (damage != null) {
            damage.removeModifier(DAMAGE_MOD_KEY);
        }
    }

    // ----- Переопределения методов Origin -----
    @Override
    public void onGetOrigin(@NotNull Player player) {
        // Очистка эффектов и модификаторов (вызов родительского метода)
        super.onGetOrigin(player);

        // Устанавливаем здоровье 24 (12 сердец)
        AttributeInstance health = player.getAttribute(Attribute.MAX_HEALTH);
        if (health != null) {
            health.setBaseValue(24.0);
            player.setHealth(Math.min(player.getHealth(), 24.0));
        }

        // Удаляем возможные старые модификаторы от этой расы
        removeSpeedModifier(player);
        removeDamageModifier(player);

        // Сбрасываем души при получении расы
        resetSouls(player);
    }

    @Override
    public void applyEffects(@NotNull Player player) {
        // Постоянное ночное зрение (обновляется каждые 2 секунды)
        if (!player.hasPotionEffect(PotionEffectType.NIGHT_VISION)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, -1, 0, false, false, false));
        }

        // Постоянный голод I (для баланса)
        if (!player.hasPotionEffect(PotionEffectType.HUNGER)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.HUNGER, -1, 0, false, false, false));
        }

        // Обновляем бонусы от душ
        int souls = getSouls(player);
        removeSpeedModifier(player);
        removeDamageModifier(player);

        if (souls > 0) {
            // +2% скорости за каждую душу
            double speedBonus = 0.02 * souls;
            AttributeInstance speedAttr = player.getAttribute(Attribute.MOVEMENT_SPEED);
            if (speedAttr != null) {
                AttributeModifier speedMod = new AttributeModifier(SPEED_MOD_KEY, speedBonus, AttributeModifier.Operation.ADD_SCALAR);
                speedAttr.addModifier(speedMod);
            }

            // +1% урона за каждую душу
            double damageBonus = 0.01 * souls;
            AttributeInstance damageAttr = player.getAttribute(Attribute.ATTACK_DAMAGE);
            if (damageAttr != null) {
                AttributeModifier damageMod = new AttributeModifier(DAMAGE_MOD_KEY, damageBonus, AttributeModifier.Operation.ADD_SCALAR);
                damageAttr.addModifier(damageMod);
            }
        }
    }

    // ----- Способности -----

    // Z – Похищение жизни
    @Override
    public void abilityZExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownZ)) {
            long seconds = getRemainingCooldown(player, cooldownZ);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 1 ещё на перезарядке – <white><sec></white> секунд",
                    Placeholder.parsed("sec", String.valueOf(seconds))
            ));
            return;
        }

        // Поиск ближайшей живой цели в радиусе 8 блоков
        Entity target = player.getWorld().getNearbyEntities(player.getLocation(), 8, 8, 8)
                .stream()
                .filter(e -> e instanceof LivingEntity && !e.equals(player))
                .min(Comparator.comparingDouble(e -> e.getLocation().distance(player.getLocation())))
                .orElse(null);

        if (target instanceof LivingEntity living) {
            // Наносим 4 урона
            living.damage(4.0, player);
            // Восстанавливаем 4 HP игроку
            double maxHealth = player.getAttribute(Attribute.MAX_HEALTH).getValue();
            player.setHealth(Math.min(player.getHealth() + 4.0, maxHealth));

            // Визуальные эффекты
            player.getWorld().spawnParticle(Particle.SOUL, living.getLocation().add(0, 1, 0), 20, 0.5, 0.5, 0.5, 0);
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WARDEN_SONIC_BOOM, 1.0f, 1.0f);
        } else {
            player.sendMessage(mm.deserialize("<red>Нет цели в радиусе 8 блоков."));
        }

        setCooldown(player, cooldownZ, Z_CD);
    }

    // X – Призрачная форма
    @Override
    public void abilityXExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownX)) {
            long seconds = getRemainingCooldown(player, cooldownX);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 2 ещё на перезарядке – <white><sec></white> секунд",
                    Placeholder.parsed("sec", String.valueOf(seconds))
            ));
            return;
        }

        // Эффекты на 8 секунд (160 тиков)
        player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 160, 0, false, false, false));
        player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 160, 1, false, false, false)); // SPEED II

        // Частицы вокруг игрока на протяжении действия
        new BukkitRunnable() {
            int ticks = 0;
            @Override
            public void run() {
                if (ticks >= 160 || !player.isOnline()) {
                    this.cancel();
                    return;
                }
                player.getWorld().spawnParticle(Particle.SOUL, player.getLocation().add(0, 0.5, 0), 5, 0.5, 0.5, 0.5, 0);
                ticks++;
            }
        }.runTaskTimer(plugin, 0L, 1L);

        // Звук активации
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ILLUSIONER_CAST_SPELL, 1.0f, 1.0f);

        setCooldown(player, cooldownX, X_CD);
    }

    // C – Разрыв Пустоты
    @Override
    public void abilityCExecute(@NotNull Player player) {
        if (isCooldownActive(player, cooldownC)) {
            long seconds = getRemainingCooldown(player, cooldownC);
            player.sendActionBar(mm.deserialize(
                    "<red>Ваша способность 3 ещё на перезарядке – <white><sec></white> секунд",
                    Placeholder.parsed("sec", String.valueOf(seconds))
            ));
            return;
        }

        int souls = getSouls(player);
        if (souls == 0) {
            player.sendMessage(mm.deserialize("<red>У вас нет душ для использования Разрыва Пустоты."));
            return;
        }

        // Наносим урон, равный количеству душ, всем существам в радиусе 8 блоков
        player.getWorld().getNearbyEntities(player.getLocation(), 8, 8, 8)
                .stream()
                .filter(e -> e instanceof LivingEntity && !e.equals(player))
                .forEach(e -> ((LivingEntity) e).damage(souls, player));

        // Визуальный взрыв душ
        player.getWorld().spawnParticle(Particle.SOUL, player.getLocation(), 100, 2, 2, 2, 0.1);
        player.getWorld().playSound(player.getLocation(), Sound.ENTITY_WITHER_SHOOT, 1.0f, 0.5f);

        // Сброс душ после использования
        resetSouls(player);

        setCooldown(player, cooldownC, C_CD);
    }

    // ----- Вспомогательные методы для кулдаунов -----
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