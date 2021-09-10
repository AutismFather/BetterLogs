package com.autcraft.com.betterlogs.listeners;

import com.autcraft.com.betterlogs.BetterLogs;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class BookSpyListener implements Listener {
    private JavaPlugin plugin;

    /**
     * Constructor
     *
     * @param plugin
     */
    public BookSpyListener(JavaPlugin plugin ){
        this.plugin = plugin;
    }

    @EventHandler
    public void onBookEdit(PlayerEditBookEvent e){
        String title;
        StringBuilder contents = new StringBuilder();
        String content;
        String bookAction;
        String response;
        String responseShort;
        int charLimit = 256;

        // Get the player that wrote the book
        Player player = (Player) e.getPlayer();

        // If this is a signed book with a real titled, use that
        if (e.isSigning() && e.getNewBookMeta().hasTitle()) {
            bookAction = " signed a book called " + e.getNewBookMeta().getTitle() + ": ";
            title = e.getPlayer().getName() + bookAction;
        } else {
            bookAction = " edited a book: ";
            title = e.getPlayer().getName() + bookAction;
        }

        // Contents of the book. Get the pages
        List<String> pages = e.getNewBookMeta().getPages();
        // Loop over the pages and build a string out of it.
        for (String page: pages) {
            contents.append(page.replaceAll("`", "'")).append("\n");
        }

        // Convert the string builder to a string
        content = contents.toString();

        // Put it all together in one solid response string
        response = title + content;

        // If the contents + title + alert extras are longer than the character limit, cut it down to size. Otherwise, just show it all.
        if( (response.length() + title.length() + 6 + 21) >= charLimit ) {
            responseShort = response.substring(0, charLimit - title.length() - 6 - 21) + "..." + ChatColor.AQUA + " see logs for more";
        }
        else {
            responseShort = response;
        }

        // Show this to players with the permission betterlogs.alerts.book
        // Thank you to Define | abyssmc.org for suggestion this method of messaging staff
        Bukkit.broadcast(ChatColor.AQUA + "[Book] " + ChatColor.RESET + responseShort, "betterlogs.alerts.book");

        // If enabled in the config, send book data to console
        if(plugin.getConfig().getBoolean("log.books"))
        BetterLogs.sendToConsole(response);
    }
}
