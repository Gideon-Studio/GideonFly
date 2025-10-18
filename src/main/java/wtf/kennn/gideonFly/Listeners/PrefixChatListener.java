package wtf.kennn.gideonFly.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class PrefixChatListener implements Listener {

    private static final Set<Player> waitingPrefix = new HashSet<>();

    public static void addWaiting(Player player) {
        waitingPrefix.add(player);
        send(player, getMenuText("prefix.message", "&dType the new prefix in chat. Example: &b[GideonFly]&f"));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if (!waitingPrefix.contains(player)) return;

        event.setCancelled(true);
        String message = event.getMessage();


        if (message.equalsIgnoreCase("cancel")) {
            waitingPrefix.remove(player);
            send(player, getMenuText("prefix.cancel", "&cPrefix change cancelled."));
            play(player, SoundUtil.SoundType.ERROR);
            return;
        }


        Bukkit.getScheduler().runTask(GideonFly.getInstance(), () -> {
            try {
                GideonFly plugin = GideonFly.getInstance();
                FileConfiguration config = plugin.getConfigManager().getConfig();
                File configFile = new File(plugin.getDataFolder(), "config.yml");


                config.set("prefix", message);
                config.save(configFile);


                plugin.getConfigManager().reloadAll();

                waitingPrefix.remove(player);

                send(player, getMenuText("prefix.success", "%prefix%&aPrefix updated to: %prefix%")
                        .replace("%prefix%", message));
                play(player, SoundUtil.SoundType.SUCCESS);

            } catch (IOException e) {
                e.printStackTrace();
                send(player, "&cError saving new prefix!");
            }
        });
    }
}
