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
import wtf.kennn.gideonFly.Utils.ChatUtil;
import wtf.kennn.gideonFly.Utils.SoundUtil;

import java.util.List;

import static wtf.kennn.gideonFly.Utils.ChatUtil.*;
import static wtf.kennn.gideonFly.Utils.SoundUtil.play;

public class SetupMenu implements Listener {

    private final GideonFly plugin;
    private final Player player;
    private final Inventory menu;

    public SetupMenu(Player player) {
        this.plugin = GideonFly.getInstance();
        this.player = player;
        this.menu = Bukkit.createInventory(null, 27,
                getMenuText("menus.setup.title", "&b&lGideonFly &8| &fSetup Menu"));

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private void setupItems() {
        // 🔒 Sin permiso → solo muestra advertencia
        if (!player.hasPermission("gideonfly.admin")) {
            menu.setItem(13, createItem(Material.BARRIER, "&c🚫 No permission",
                    List.of("&7You don’t have permission to open this menu.")));
            return;
        }

        // ✅ Ítems configurables
        menu.setItem(11, createItem(Material.COMPASS,
                getMenuText("menus.setup.items.spawn.name", "&b🧭 Set VIP Spawn"),
                getMenuList("menus.setup.items.spawn.lore",
                        List.of("&7Configure VIP spawn location."))));

        menu.setItem(13, createItem(Material.PAPER,
                getMenuText("menus.setup.items.join.name", "&a💬 Join Messages"),
                getMenuList("menus.setup.items.join.lore",
                        List.of("&7Enable or disable join messages."))));

        menu.setItem(15, createItem(Material.NAME_TAG,
                getMenuText("menus.setup.items.prefix.name", "&d🏷️ Change Prefix"),
                getMenuList("menus.setup.items.prefix.lore",
                        List.of("&7Change the plugin's prefix."))));

        menu.setItem(22, createItem(Material.REDSTONE,
                getMenuText("menus.setup.items.config.name", "&e💾 Save / Reload Config"),
                getMenuList("menus.setup.items.config.lore",
                        List.of("&7Open save/reload menu."))));
    }

    private ItemStack createItem(Material mat, String name, List<String> lore) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(colorize(name));
        meta.setLore(lore.stream().map(ChatUtil::colorize).toList());
        item.setItemMeta(meta);
        return item;
    }

    public void open() {
        setupItems();
        player.openInventory(menu);
        play(player, SoundUtil.SoundType.OPEN_MENU);
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!event.getInventory().equals(menu)) return;
        if (!(event.getWhoClicked() instanceof Player p)) return;
        if (!p.getUniqueId().equals(player.getUniqueId())) return;
        if (event.getCurrentItem() == null) return;

        event.setCancelled(true);
        play(p, SoundUtil.SoundType.CLICK);

        // 🔒 Bloqueo por permiso
        if (!p.hasPermission("gideonfly.admin")) {
            send(p, "&cYou don’t have permission to use this menu.");
            play(p, SoundUtil.SoundType.ERROR);
            return;
        }

        // ✅ Acciones
        switch (event.getCurrentItem().getType()) {
            case COMPASS -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new SpawnMenu(p).open(), 2L);
            }
            case PAPER -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new MessagesMenu(p).open(), 2L);
            }
            case NAME_TAG -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin,
                        () -> wtf.kennn.gideonFly.Listeners.PrefixChatListener.addWaiting(p), 2L);
            }
            case REDSTONE -> {
                p.closeInventory();
                Bukkit.getScheduler().runTaskLater(plugin, () -> new SaveMenu(p).open(), 2L);
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
