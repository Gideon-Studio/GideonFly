package wtf.kennn.gideonFly.Utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import wtf.kennn.gideonFly.GideonFly;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    // Soporte de colores hexadecimales (#FFFFFF)
    private static final Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");

    /**
     * Convierte texto con & y #RRGGBB a formato de color válido.
     */
    public static String colorize(String message) {
        if (message == null) return "";

        // ✅ Reemplaza %prefix% dinámicamente cada vez que se usa
        String prefix = GideonFly.getInstance()
                .getConfigManager()
                .getConfig()
                .getString("prefix", "&b[GIDEONFLY]&f ");
        message = message.replace("%prefix%", prefix);

        // ✅ Soporte para hex y &
        Pattern HEX_PATTERN = Pattern.compile("#[a-fA-F0-9]{6}");
        Matcher matcher = HEX_PATTERN.matcher(message);
        while (matcher.find()) {
            String color = matcher.group();
            message = message.replace(color, net.md_5.bungee.api.ChatColor.of(color).toString());
        }

        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);
    }


    /**
     * Envía mensaje coloreado a un jugador o consola.
     */
    public static void send(CommandSender sender, String message) {
        if (message == null || message.isEmpty()) return;
        sender.sendMessage(colorize(message));
    }

    /**
     * Envía un broadcast global a todos los jugadores.
     */
    public static void broadcast(String message) {
        if (message == null || message.isEmpty()) return;
        Bukkit.getOnlinePlayers().forEach(p -> p.sendMessage(colorize(message)));
    }

    /**
     * Envía un mensaje formateado a la consola con soporte de & y #HEX.
     */
    public static void log(String message) {
        if (message == null || message.isEmpty()) return;
        Bukkit.getConsoleSender().sendMessage(colorize(message));
    }
    // Alias simple
    public static String color(String message) {
        return colorize(message);
    }

    // Útil para lore: colorea cada línea
    public static java.util.List<String> color(java.util.List<String> lines) {
        return lines.stream().map(ChatUtil::colorize).toList();
    }

    public static String getMenuText(String path, String def) {
        var cfg = GideonFly.getInstance().getConfigManager().getConfig();
        return colorize(cfg.getString("menus." + path, def));
    }
    public static java.util.List<String> getMenuList(String path, java.util.List<String> def) {
        var cfg = GideonFly.getInstance().getConfigManager().getConfig();
        var list = cfg.getStringList("menus." + path);
        if (list.isEmpty()) return def.stream().map(ChatUtil::colorize).toList();
        return list.stream().map(ChatUtil::colorize).toList();
    }


}
