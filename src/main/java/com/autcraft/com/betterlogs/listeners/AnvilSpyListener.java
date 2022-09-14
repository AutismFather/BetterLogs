package com.autcraft.com.betterlogs.listeners;

import com.autcraft.com.betterlogs.BetterLogs;
import com.autcraft.com.betterlogs.Webhook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class AnvilSpyListener implements Listener {
    private JavaPlugin plugin;
    private String response;

    /**
     * Constructor
     *
     * @param plugin
     */
    public AnvilSpyListener( JavaPlugin plugin ){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){

        // If anvil logging is turned off, just stop here.
        if( !plugin.getConfig().getBoolean("log.anvils") ){
            return;
        }

        // Is this a player? Has to be, right? but if not, exit.
        if( !(e.getWhoClicked() instanceof Player) ){
            return;
        }

        // Get the player instance
        Player player = (Player) e.getWhoClicked();
        Inventory inventory = e.getInventory();

        // Inventory's not set for whatever reason? Exit.
        if( inventory == null ){
            return;
        }

        // We mostly want the inventory item coming from an anvil, so...
        if( inventory.getType() != InventoryType.ANVIL || e.getSlotType() != InventoryType.SlotType.RESULT ){
            return;
        }

        // Get the names of the item, new and old.
        ItemStack renamedItem = e.getCurrentItem();
        if (renamedItem == null || !renamedItem.hasItemMeta() || !renamedItem.getItemMeta().hasDisplayName() || renamedItem.getItemMeta().getDisplayName() == null) {
            return;
        }

        // The new name of the item
        String newName = renamedItem.getItemMeta().getDisplayName();
        AnvilInventory anvilInventory = (AnvilInventory) inventory;
        ItemStack originalItem = anvilInventory.getItem(0);
        int itemCount = originalItem.getAmount();
        String originalName = "";

        // Make sure it's not empty
        if (originalItem != null && originalItem.hasItemMeta() && originalItem.getItemMeta().hasDisplayName() && originalItem.getItemMeta().getDisplayName() != null) {
            originalName = originalItem.getItemMeta().getDisplayName();
        }

        // If the two names are the same, that means it was enchanted, not renamed, so don't bother reporting it.
        if( newName.equalsIgnoreCase(originalName) ){
            return;
        }

        // If there is no original name, just pretend the name is the item type
        if( originalName.isEmpty() )
            originalName = originalItem.getType().toString();

        String alert = plugin.getConfig().getString("anvils.alert");
        String console = plugin.getConfig().getString("anvils.console");

        alert = alert.replace("{player}", player.getName());
        alert = alert.replace("{count}", itemCount+"");
        alert = alert.replace("{item}", originalItem.getType().toString());
        alert = alert.replace("{oldname}", originalName);
        alert = alert.replace("{newname}", newName);
        alert = ChatColor.translateAlternateColorCodes('&', alert);

        console = console.replace("{player}", player.getName());
        console = console.replace("{count}", itemCount+"");
        console = console.replace("{item}", originalItem.getType().toString());
        console = console.replace("{oldname}", originalName);
        console = console.replace("{newname}", newName);

        // Show this to players with the permission betterlogs.alerts.signspy
        // Thank you to Define | abyssmc.org for suggestion this method of messaging staff
        Bukkit.broadcast(alert, "betterlogs.alerts.anvil");

        // If enabled in the config, send sign data to console
        if(plugin.getConfig().getBoolean("log.anvils"))
            BetterLogs.sendToConsole(console);

        // If enabled, send to Discord webhook
        if( plugin.getConfig().getBoolean("anvils.discord.enabled") ) {
            Webhook webhook = new Webhook();
            String url = plugin.getConfig().getString("anvils.discord.webhook");
            String image = plugin.getConfig().getString("anvils.discord.image");
            webhook.send(url, "Anvil", console, image);
        }
    }
}
