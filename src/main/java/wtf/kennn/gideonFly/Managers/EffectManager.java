package wtf.kennn.gideonFly.Managers;

import org.bukkit.*;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Listeners.VolcanoCleaner;

import java.util.Random;

import static co.aikar.commands.ACFUtil.RANDOM;

public class EffectManager {

    private static final Random random = new Random();


    /**
     * Reproduce el efecto según el tipo guardado del jugador
     */
    public static void playEffect(Player player, String effectType) {
        if (effectType == null) return;
        effectType = effectType.toUpperCase();

        Location loc = player.getLocation();
        World world = loc.getWorld();

        switch (effectType) {
            case "RAYO" -> lightningEffect(world, loc);
            case "FUEGO" -> fireworkEffect(world, loc);
            case "VOLCAN" -> spawnWoolVolcano(player);
            case "NINGUNO" -> {} // No hace nada
            default -> {
                Bukkit.getLogger().warning("[GIDEONFLY] Unknown effect: " + effectType);
            }
        }
    }

    /**
     * ⚡ RAYO (solo efecto visual)
     */
    private static void lightningEffect(World world, Location loc) {
        world.strikeLightningEffect(loc);
        world.spawnParticle(Particle.ELECTRIC_SPARK, loc, 30, 0.5, 1, 0.5, 0.1);
        world.playSound(loc, Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 0.6f, 1.4f);
    }

    /**
     * 🎇 FUEGO ARTIFICIAL (color aleatorio)
     */
    private static void fireworkEffect(World world, Location loc) {
        Firework fw = world.spawn(loc, Firework.class);
        FireworkMeta meta = fw.getFireworkMeta();

        Color[] colors = {Color.RED, Color.AQUA, Color.LIME, Color.FUCHSIA, Color.YELLOW, Color.ORANGE};
        meta.addEffect(FireworkEffect.builder()
                .withColor(colors[random.nextInt(colors.length)])
                .withFade(Color.WHITE)
                .with(FireworkEffect.Type.BALL_LARGE)
                .trail(true)
                .flicker(true)
                .build());
        meta.setPower(0);
        fw.setFireworkMeta(meta);


        Bukkit.getScheduler().runTaskLater(GideonFly.getInstance(), fw::detonate, 10L);
    }

    /**
     * 🌋 VOLCÁN DE LANA
     */
    public static void spawnWoolVolcano(Player player) {
        World world = player.getWorld();
        GideonFly plugin = GideonFly.getInstance();

        // Tag invisible para identificar nuestras lanas
        NamespacedKey TAG = new NamespacedKey(plugin, "gf_volcano");

        Material[] colors = {
                Material.RED_WOOL, Material.ORANGE_WOOL, Material.YELLOW_WOOL,
                Material.LIME_WOOL, Material.LIGHT_BLUE_WOOL,
                Material.CYAN_WOOL, Material.PURPLE_WOOL, Material.PINK_WOOL, Material.WHITE_WOOL
        };

        final int durationTicks = 60; // ⏱️ efecto visible durante 3 s
        final int interval = 3;       // cada 3 ticks ≈ 0.15 s

        new BukkitRunnable() {
            int elapsed = 0;

            @Override
            public void run() {
                if (!player.isOnline() || elapsed >= durationTicks) {
                    Location end = player.getLocation().clone().add(0, 0.5, 0);
                    world.playSound(end, Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1.3f);
                    world.spawnParticle(Particle.CLOUD, end, 50, 1, 0.5, 1, 0.05);
                    cancel();
                    return;
                }

                // Base del efecto (sigue al jugador)
                Location base = player.getLocation().clone().add(0, 0.1, 0);
                int count = 10 + RANDOM.nextInt(5);

                for (int i = 0; i < count; i++) {
                    Material wool = colors[RANDOM.nextInt(colors.length)];
                    Location spawn = base.clone().add(
                            (RANDOM.nextDouble() - 0.5) * 0.8,
                            RANDOM.nextDouble() * 0.6,
                            (RANDOM.nextDouble() - 0.5) * 0.8
                    );

                    FallingBlock fb = world.spawnFallingBlock(spawn, wool.createBlockData());
                    fb.setVelocity(new Vector(
                            (RANDOM.nextDouble() - 0.5) * 0.9,
                            RANDOM.nextDouble() * 1.4 + 0.8,
                            (RANDOM.nextDouble() - 0.5) * 0.9
                    ));
                    fb.setDropItem(false);
                    fb.setHurtEntities(false);

                    // Tag invisible para limpieza
                    fb.getPersistentDataContainer().set(TAG, PersistentDataType.BYTE, (byte) 1);

                    // Partículas + sonidos
                    world.spawnParticle(Particle.LAVA, spawn, 6, 0.2, 0.3, 0.2, 0.01);
                    world.spawnParticle(Particle.CRIT, spawn, 4, 0.3, 0.3, 0.3, 0.01);
                    world.playSound(spawn, Sound.BLOCK_LAVA_POP, 0.6f, 1.3f + RANDOM.nextFloat() * 0.4f);
                    world.playSound(spawn, Sound.ENTITY_FIREWORK_ROCKET_TWINKLE, 0.5f, 1.6f + RANDOM.nextFloat() * 0.5f);
                }

                elapsed += interval;
            }
        }.runTaskTimer(plugin, 0L, interval);

        // 🔥 Limpieza forzada a los 20 s
        Bukkit.getScheduler().runTaskLater(plugin, () -> VolcanoCleaner.cleanAll(plugin), 20L * 20);
    }
}
