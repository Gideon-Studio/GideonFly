package wtf.kennn.gideonFly.Managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import wtf.kennn.gideonFly.GideonFly;

public class PlayerDataManager {

    private final GideonFly plugin;

    public PlayerDataManager(GideonFly plugin) {
        this.plugin = plugin;
    }

    /**
     * Guarda el efecto elegido por el jugador
     */
    public void setEffect(Player player, String effectType) {
        FileConfiguration data = plugin.getConfigManager().getPlayerData();
        data.set(player.getName() + ".effect", effectType.toUpperCase());
        plugin.getConfigManager().savePlayerData();
    }

    /**
     * Obtiene el efecto actual del jugador
     */
    public String getEffect(Player player) {
        return plugin.getConfigManager().getPlayerData()
                .getString(player.getName() + ".effect", "NINGUNO")
                .toUpperCase();
    }

    /**
     * Limpia todos los datos del jugador (si lo deseas en un futuro)
     */
    public void clear(Player player) {
        FileConfiguration data = plugin.getConfigManager().getPlayerData();
        data.set(player.getName(), null);
        plugin.getConfigManager().savePlayerData();
    }
}
