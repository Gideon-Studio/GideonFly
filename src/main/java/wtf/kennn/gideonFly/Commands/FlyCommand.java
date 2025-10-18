package wtf.kennn.gideonFly.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import wtf.kennn.gideonFly.GideonFly;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;
import wtf.kennn.gideonFly.Utils.SoundUtil.SoundType;

@CommandAlias("fly")
@Description("Enable or disable flight mode.")
public class FlyCommand extends BaseCommand {

    private final GideonFly plugin;

    public FlyCommand() {
        this.plugin = GideonFly.getInstance();
    }

    @Default
    @Syntax("[player]")
    @CommandCompletion("@players")
    @CommandPermission("gideonfly.use")
    public void onFly(Player sender, @Optional String targetName) {

        var config = plugin.getConfigManager().getConfig();

        // 🔧 Valores por defecto si faltan en la config
        String prefix = config.getString("prefix", "&b[GIDEONFLY]&f ");
        String msgFlyOn = config.getString("messages.fly-on", "&a✈ Flight enabled.").replace("%prefix%", prefix);
        String msgFlyOff = config.getString("messages.fly-off", "&c🛑 Flight disabled.").replace("%prefix%", prefix);
        String msgNoPerm = config.getString("messages.no-permission", "&cYou don’t have permission to do that.").replace("%prefix%", prefix);

        if (targetName != null) {
            // 🔐 Comprobación de permisos para controlar a otros
            if (!sender.hasPermission("gideonfly.admin")) {
                send(sender, msgNoPerm);
                play(sender, SoundType.ERROR);
                return;
            }

            Player target = Bukkit.getPlayerExact(targetName);
            if (target == null) {
                send(sender, "&cPlayer not found.");
                play(sender, SoundType.ERROR);
                return;
            }

            boolean newState = !target.getAllowFlight();
            target.setAllowFlight(newState);
            target.setFlying(newState);

            if (newState) {
                send(target, msgFlyOn);
                play(target, SoundType.FLY_ON);
            } else {
                send(target, msgFlyOff);
                play(target, SoundType.FLY_OFF);
            }

            send(sender, "&7Toggled fly for &b" + target.getName() + "&7.");
            return;
        }

        // 🧍 Si no hay objetivo → togglear el vuelo propio
        boolean newState = !sender.getAllowFlight();
        sender.setAllowFlight(newState);
        sender.setFlying(newState);

        if (newState) {
            send(sender, msgFlyOn);
            play(sender, SoundType.FLY_ON);
            log("&a" + sender.getName() + " enabled flight mode.");
        } else {
            send(sender, msgFlyOff);
            play(sender, SoundType.FLY_OFF);
            log("&c" + sender.getName() + " disabled flight mode.");
        }
    }
}
