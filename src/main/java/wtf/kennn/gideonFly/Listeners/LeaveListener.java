package wtf.kennn.gideonFly.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import wtf.kennn.gideonFly.Apis.LuckPermsHook;
import wtf.kennn.gideonFly.GideonFly;

import static wtf.kennn.gideonFly.Utils.ChatUtil.colorize;

public class LeaveListener implements Listener {

    private final GideonFly plugin;

    public LeaveListener(GideonFly plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // ❌ quitar mensaje por defecto de Minecraft
        e.quitMessage(null);

        var config = plugin.getConfigManager().getConfig();

        // 🚫 si está desactivado, salir
        if (!config.getBoolean("leave-messages.enabled", true)) return;

        // ✅ obtener datos
        var player = e.getPlayer();
        String prefix = LuckPermsHook.getPrefix(player);
        String msg = config.getString(
                "leave-messages.message",
                "&c✈ %luckperms_prefix% %player_name% has left the server!"
        );

        // Reemplazar placeholders
        msg = msg.replace("%player_name%", player.getName())
                .replace("%luckperms_prefix%", prefix);

        // Enviar a todos los jugadores
        Bukkit.broadcastMessage(colorize(msg));
    }
}
