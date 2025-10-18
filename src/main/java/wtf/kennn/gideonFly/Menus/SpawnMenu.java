package wtf.kennn.gideonFly.Menus;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
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
import wtf.kennn.gideonFly.Utils.SoundUtil;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class SpawnMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private final Inventory menu;

    public SpawnMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;
        this.menu = Bukkit.createInventory(null, 27, getMenuText("spawn.title", "&b&lGideonFly &8| &fVIP Spawn"));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupItems();
    }

    private void setupItems() {
        menu.setItem(11, createItem(Material.EMERALD_BLOCK, getMenuText("spawn.items.set.name", "&a✅ Set VIP Spawn"),
                getMenuList("spawn.items.set.lore", List.of("&7Save your current position as VIP spawn."))));
        menu.setItem(15, createItem(Material.REDSTONE_BLOCK, getMenuText("spawn.items.remove.name", "&c❌ Remove VIP Spawn"),
                getMenuList("spawn.items.remove.lore", List.of("&7Delete the existing VIP spawn point."))));
        menu.setItem(22, createItem(Material.ARROW, getMenuText("spawn.items.back.name", "&e⬅ Back"),
                getMenuList("spawn.items.back.lore", List.of("&7Return to setup menu."))));
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
        if (!e.getInventory().equals(menu)) return;
        if (!(e.getWhoClicked() instanceof Player p)) return;
        if (!p.getUniqueId().equals(player.getUniqueId())) return;

        e.setCancelled(true);
        play(p, SoundUtil.SoundType.CLICK);
        if (e.getCurrentItem() == null || e.getCurrentItem().getType().isAir()) return;
        FileConfiguration spawn = plugin.getConfigManager().getSpawn();

        switch (e.getCurrentItem().getType()) {
            case EMERALD_BLOCK -> {
                var loc = p.getLocation();
                spawn.set("world", loc.getWorld().getName());
                spawn.set("x", loc.getX());
                spawn.set("y", loc.getY());
                spawn.set("z", loc.getZ());
                spawn.set("yaw", loc.getYaw());
                spawn.set("pitch", loc.getPitch());
                try {
                    plugin.getConfigManager().getSpawn().save(new File(plugin.getDataFolder(), "spawn.yml"));
                    send(p, "&aVIP Spawn saved successfully!");
                    play(p, SoundUtil.SoundType.SUCCESS);
                } catch (IOException ex) {
                    send(p, "&cError saving spawn.yml!");
                    ex.printStackTrace();
                }
            }
            case REDSTONE_BLOCK -> {
                spawn.set("world", null);
                spawn.set("x", null);
                spawn.set("y", null);
                spawn.set("z", null);
                spawn.set("yaw", null);
                spawn.set("pitch", null);
                try {
                    plugin.getConfigManager().getSpawn().save(new File(plugin.getDataFolder(), "spawn.yml"));
                    send(p, "&cVIP Spawn removed!");
                    play(p, SoundUtil.SoundType.ERROR);
                } catch (IOException ex) {
                    send(p, "&cError saving spawn.yml!");
                    ex.printStackTrace();
                }
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
