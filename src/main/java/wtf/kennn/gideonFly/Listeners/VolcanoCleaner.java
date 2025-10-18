package wtf.kennn.gideonFly.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.entity.FallingBlock;
import org.bukkit.persistence.PersistentDataType;
import wtf.kennn.gideonFly.GideonFly;

public class VolcanoCleaner {

    public static void cleanAll(GideonFly plugin) {
        NamespacedKey TAG = new NamespacedKey(plugin, "gf_volcano");

        for (World world : Bukkit.getWorlds()) {
            for (FallingBlock fb : world.getEntitiesByClass(FallingBlock.class)) {
                if (fb.getPersistentDataContainer().has(TAG, PersistentDataType.BYTE)) {
                    fb.remove();
                }
            }
        }
    }
}
