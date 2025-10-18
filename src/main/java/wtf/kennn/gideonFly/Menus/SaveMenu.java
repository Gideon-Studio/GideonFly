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
import wtf.kennn.gideonFly.Listeners.JoinListener;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class SaveMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private final Inventory menu;

    public SaveMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;
        this.menu = Bukkit.createInventory(null, 27, getMenuText("config.title", "&b&lGideonFly &8| &fConfiguration"));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupItems();
    }

    private void setupItems() {
        menu.setItem(11, createItem(Material.EMERALD_BLOCK, getMenuText("config.items.save.name", "&a✅ Save Configuration"),
                getMenuList("config.items.save.lore", List.of("&7Save all configuration files."))));
        menu.setItem(15, createItem(Material.BOOK, getMenuText("config.items.reload.name", "&e♻ Reload Configuration"),
                getMenuList("config.items.reload.lore", List.of("&7Reload all plugin files."))));
        menu.setItem(22, createItem(Material.ARROW, getMenuText("config.items.back.name", "&e⬅ Back"),
                getMenuList("config.items.back.lore", List.of("&7Return to setup menu."))));
    }

    private ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(lore);
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        player.openInventory(menu);
        play(player, SoundUtil.SoundType.OPEN_MENU);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        // ✅ Si este menú está abierto, cancela TODO movimiento (arriba y abajo)
        if (!e.getView().getTopInventory().equals(menu)) return;
        e.setCancelled(true);

        // Solo procesamos si el click fue en el inventario del menú (arriba)
        if (e.getClickedInventory() == null || !e.getClickedInventory().equals(menu)) return;
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.getUniqueId().equals(player.getUniqueId())) return;
        if (e.getCurrentItem() == null) return;

        play(p, SoundUtil.SoundType.CLICK);

        switch (e.getCurrentItem().getType()) {
            case EMERALD_BLOCK -> {
                try {
                    plugin.getConfigManager().getConfig().save(new File(plugin.getDataFolder(), "config.yml"));
                    plugin.getConfigManager().getSpawn().save(new File(plugin.getDataFolder(), "spawn.yml"));
                    plugin.getConfigManager().getPlayerData().save(new File(plugin.getDataFolder(), "playerdata.yml"));
                    send(p, "&aAll configurations saved successfully!");
                    play(p, SoundUtil.SoundType.SUCCESS);
                    p.closeInventory();
                } catch (IOException ex) {
                    send(p, "&cError saving configurations!");
                    ex.printStackTrace();
                }
            }
            case BOOK -> {
                plugin.getConfigManager().reloadAll();
                // Re-registra listeners para tomar la config nueva
                Bukkit.getScheduler().runTask(plugin, () -> {
                    HandlerList.unregisterAll(plugin);
                    plugin.getServer().getPluginManager().registerEvents(new wtf.kennn.gideonFly.Listeners.JoinListener(plugin), plugin);
                });
                send(p, "&eAll configurations reloaded successfully!");
                play(p, SoundUtil.SoundType.SUCCESS);
                p.closeInventory();
            }
            case ARROW -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new SetupMenu(p).open(), 2L);
            }
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
