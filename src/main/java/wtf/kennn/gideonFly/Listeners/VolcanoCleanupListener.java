package wtf.kennn.gideonFly.Listeners;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.Particle;
import wtf.kennn.gideonFly.GideonFly;

public class VolcanoCleanupListener implements Listener {

    private final NamespacedKey TAG;

    public VolcanoCleanupListener(GideonFly plugin) {
        this.TAG = new NamespacedKey(plugin, "gf_volcano");
    }

    @EventHandler
    public void onFallingBlockLand(EntityChangeBlockEvent e) {
        Entity ent = e.getEntity();
        if (!(ent instanceof FallingBlock fb)) return;

        PersistentDataContainer pdc = fb.getPersistentDataContainer();
        if (!pdc.has(TAG, PersistentDataType.BYTE)) return; // No es nuestro

        // ❌ Evita que se coloque como bloque
        e.setCancelled(true);

        // 💨 Efecto visual al eliminar
        fb.getWorld().spawnParticle(Particle.CLOUD, fb.getLocation(), 8, 0.2, 0.2, 0.2, 0.02);

        // 🧹 Borra la entidad
        fb.remove();
    }
}
