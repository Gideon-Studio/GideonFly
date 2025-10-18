package wtf.kennn.gideonFly.Menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import wtf.kennn.gideonFly.GideonFly;
import wtf.kennn.gideonFly.Managers.EffectManager;
import wtf.kennn.gideonFly.Utils.ChatUtil;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import java.util.List;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class JoinEffectsMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private final Inventory menu;

    public JoinEffectsMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;

        this.menu = Bukkit.createInventory(null, 27, getMenuText("menus.joineffects.title", "&b&lGideonFly &8| &fJoin Effects"));
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupItems();
    }

    private void setupItems() {
        // 🔒 Si no tiene permiso, solo muestra advertencia
        if (!player.hasPermission("gideonfly.joineffects")) {
            menu.setItem(13, createItem(Material.BARRIER, "&c🚫 No permission",
                    List.of("&7You don’t have permission to use join effects.")));
            return;
        }

        // ✅ Efectos configurables desde config.yml
        menu.setItem(10, createItem(
                Material.LIGHTNING_ROD,
                getMenuText("effects.rayo.name", "&e⚡ Friendly Lightning"),
                getMenuList("effects.rayo.lore", List.of("&7Strikes lightning (no damage)."))
        ));

        menu.setItem(12, createItem(
                Material.FIREWORK_ROCKET,
                getMenuText("effects.fuego.name", "&d🎇 Firework"),
                getMenuList("effects.fuego.lore", List.of("&7Launch a firework when joining."))
        ));

        menu.setItem(14, createItem(
                Material.RED_WOOL,
                getMenuText("effects.volcan.name", "&c🌋 Wool Volcano"),
                getMenuList("effects.volcan.lore", List.of("&7Throws colorful wool blocks."))
        ));

        menu.setItem(16, createItem(
                Material.BARRIER,
                getMenuText("effects.ninguno.name", "&8❌ No Effect"),
                getMenuList("effects.ninguno.lore", List.of("&7Disable any join effect."))
        ));

        menu.setItem(22, createItem(
                Material.ARROW,
                getMenuText("effects.back.name", "&e⬅ Close"),
                getMenuList("effects.back.lore", List.of("&7Return to setup menu."))
        ));
    }

    private ItemStack createItem(Material material, String name, List<String> lore) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize(name));
        meta.setLore(lore.stream().map(ChatUtil::colorize).toList());
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(menu);
        play(player, SoundUtil.SoundType.OPEN_MENU);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!e.getInventory().equals(menu)) return;
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.getUniqueId().equals(player.getUniqueId())) return;
        if (e.getCurrentItem() == null) return;

        e.setCancelled(true);
        play(p, SoundUtil.SoundType.CLICK);

        // 🔒 Bloqueo de interacción si no tiene permiso
        if (!p.hasPermission("gideonfly.joineffects")) {
            send(p, "&cYou don’t have permission to use this menu.");
            play(p, SoundUtil.SoundType.ERROR);
            return;
        }

        Material type = e.getCurrentItem().getType();
        String effectType = null;

        switch (type) {
            case LIGHTNING_ROD -> effectType = "RAYO";
            case FIREWORK_ROCKET -> effectType = "FUEGO";
            case RED_WOOL -> effectType = "VOLCAN";
            case BARRIER -> effectType = "NINGUNO";
            case ARROW -> {
                p.closeInventory();
                play(p, SoundUtil.SoundType.CLICK);
                HandlerList.unregisterAll(this);
                return;
            }
        }

        if (effectType != null) {
            plugin.getPlayerDataManager().setEffect(p, effectType);
            send(p, "&aSelected effect: &f" + effectType);
            play(p, SoundUtil.SoundType.SUCCESS);
            EffectManager.playEffect(p, effectType);
            p.closeInventory();
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent e) {
        if (!e.getInventory().equals(menu)) return;
        if (!e.getPlayer().getUniqueId().equals(player.getUniqueId())) return;
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (!e.getPlayer().getUniqueId().equals(player.getUniqueId())) return;
        HandlerList.unregisterAll(this);
    }
}
