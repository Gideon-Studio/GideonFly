package wtf.kennn.gideonFly.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import wtf.kennn.gideonFly.Apis.LuckPermsHook;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Managers.EffectManager;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class JoinListener implements Listener {

    private final GideonFly plugin;

    public JoinListener(GideonFly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();

        // ❌ Quita el mensaje default
        e.joinMessage(null);

        var config = plugin.getConfigManager().getConfig();

        // ✅ Mostrar mensaje de entrada global (si está activado)
        if (config.getBoolean("vip-broadcast", true)) {
            String prefix = LuckPermsHook.getPrefix(p);
            String joinMessage = config.getString(
                            "messages.join-broadcast",
                            "&6★ &e%luckperms_prefix% %player_name% joined the server!"
                    )
                    .replace("%player_name%", p.getName())
                    .replace("%luckperms_prefix%", prefix);

            Bukkit.broadcastMessage(colorize(joinMessage));
        }

        // ✅ Si es VIP: activar vuelo y teletransportar
        if (p.hasPermission("gideonfly.vip")) {
            p.setAllowFlight(true);
            p.setFlying(true);
            send(p, config.getString("messages.vip-entry", "&bWelcome VIP! Your flight is active."));
            play(p, SoundUtil.SoundType.SUCCESS);

            var spawn = plugin.getConfigManager().getSpawn();
            String worldName = spawn.getString("world");

            if (worldName != null && Bukkit.getWorld(worldName) != null) {
                Location loc = new Location(
                        Bukkit.getWorld(worldName),
                        spawn.getDouble("x"),
                        spawn.getDouble("y"),
                        spawn.getDouble("z"),
                        (float) spawn.getDouble("yaw"),
                        (float) spawn.getDouble("pitch")
                );
                p.teleport(loc);
            }
        }

        // ✅ Ejecutar efecto guardado (si existe)
        String effectType = plugin.getPlayerDataManager().getEffect(p);
        if (!effectType.equalsIgnoreCase("NINGUNO")) {
            Bukkit.getScheduler().runTaskLater(plugin, () ->
                    EffectManager.playEffect(p, effectType), 20L);
        }
    }
}
