package com.autcraft.com.betterlogs.listeners;

import com.autcraft.com.betterlogs.BetterLogs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SignSpyListener implements Listener {
    private JavaPlugin plugin;
    private String response;

    /**
     * Constructor for Sign Spy Listener
     *
     * @param instance
     */
    public SignSpyListener(JavaPlugin instance) {
        this.plugin = instance;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e){
        // Figure out the player who is changing the sign
        Player player = (Player) e.getPlayer();

        // Get all of the lines on the sign, put into one continuous string
        String[] signlines = e.getLines();
        String signText = "";
        for (String line : signlines) {
            if (!line.isEmpty()) {
                // If signText is not empty, it means stuff's already been added, so add a space before adding more.
                if( !signText.isEmpty() ){
                    signText += " ";
                }
                signText = signText + line;
            }
        }

        // If it's just a bunch of blank lines...
        if (signText.trim().isEmpty()) {
            return;
        }

        // get the player's location
        //Location playerLocation = e.getPlayer().getLocation();
        Location location = e.getBlock().getLocation();

        // Create a string with all of the important information to send ot the logs
        this.response = player.getName() + " placed sign with text \"" + signText + "\" in " + location.getWorld().getName() + " at (" + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ() + ")";

        // Show this to players with the permission betterlogs.alerts.signspy
        // Thank you to Define | abyssmc.org for suggestion this method of messaging staff
        Bukkit.broadcast(ChatColor.AQUA + "[Sign] " + ChatColor.RESET + this.response, "betterlogs.alerts.sign");

        // If enabled in the config, send sign data to console
        if(plugin.getConfig().getBoolean("log.signs"))
            BetterLogs.sendToConsole(response);
    }
}
