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

import java.util.List;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class MessagesMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private final Inventory menu;

    public MessagesMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;
        this.menu = Bukkit.createInventory(null, 27, getMenuText("join.title", "&b&lGideonFly &8| &fJoin Messages"));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupItems();
    }

    private void setupItems() {
        menu.setItem(11, createItem(Material.LIME_DYE, getMenuText("join.items.enable.name", "&a✅ Enable Join Messages"),
                getMenuList("join.items.enable.lore", List.of("&7Enable global join messages."))));
        menu.setItem(15, createItem(Material.RED_DYE, getMenuText("join.items.disable.name", "&c❌ Disable Join Messages"),
                getMenuList("join.items.disable.lore", List.of("&7Disable global join messages."))));
        menu.setItem(22, createItem(Material.ARROW, getMenuText("join.items.back.name", "&e⬅ Back"),
                getMenuList("join.items.back.lore", List.of("&7Return to setup menu."))));
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
        if (e.getCurrentItem() == null) return;

        e.setCancelled(true);
        play(p, SoundUtil.SoundType.CLICK);

        FileConfiguration config = plugin.getConfigManager().getConfig();

        switch (e.getCurrentItem().getType()) {
            case LIME_DYE -> {
                config.set("join-messages", true);
                send(p, "&aJoin messages are now ENABLED!");
                play(p, SoundUtil.SoundType.SUCCESS);
            }
            case RED_DYE -> {
                config.set("join-messages", false);
                send(p, "&cJoin messages are now DISABLED!");
                play(p, SoundUtil.SoundType.ERROR);
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
