package com.autcraft.com.betterlogs.listeners;

import com.autcraft.com.betterlogs.BetterLogs;
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
        String originalName = "";

        // Make sure it's not empty
        if (originalItem != null && originalItem.hasItemMeta() && originalItem.getItemMeta().hasDisplayName() && originalItem.getItemMeta().getDisplayName() != null) {
            originalName = originalItem.getItemMeta().getDisplayName();
        }

        // If the two names are the same, that means it was enchanted, not renamed, so don't bother reporting it.
        if( newName.equalsIgnoreCase(originalName) ){
            return;
        }

        // Create a string with all of the important information to send ot the logs
        this.response = player.getName() + " renamed " + originalItem.getType().toString() + " to \"" + newName + "\"";

        // Show this to players with the permission betterlogs.alerts.signspy
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if( onlinePlayer.hasPermission("betterlogs.alerts.anvil") ){
                // Send the person an alert
                onlinePlayer.sendMessage(ChatColor.AQUA + "[Anvil] " + ChatColor.RESET + this.response);
            }
        }

        // Send the report to the log
        BetterLogs.sendToConsole(response);
    }
}
