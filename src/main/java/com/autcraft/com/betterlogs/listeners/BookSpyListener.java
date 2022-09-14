package com.autcraft.com.betterlogs.listeners;

import com.autcraft.com.betterlogs.BetterLogs;
import com.autcraft.com.betterlogs.Webhook;
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
        StringBuilder contents = new StringBuilder();
        String content;
        String alertContent = "";
        String bookAction;
        String bookTitle = "";
        int charLimit = 256;

        // Get the player that wrote the book
        Player player = e.getPlayer();

        // If this is a signed book with a real titled, use that
        if (e.isSigning() && e.getNewBookMeta().hasTitle()) {
            bookAction = "signed";
            bookTitle = e.getNewBookMeta().getTitle();
        } else {
            bookAction = "edited";
            bookTitle = "Not Titled Yet";
        }

        // Contents of the book. Get the pages
        List<String> pages = e.getNewBookMeta().getPages();
        // Loop over the pages and build a string out of it.
        for (String page: pages) {
            contents.append(page.replaceAll("`", "'")).append("\n");
        }

        // Convert the string builder to a string
        content = contents.toString();
        if(content.length() < charLimit ){
            alertContent = content.substring(0, content.length()-1);
        }
        else {
            alertContent = content;
        }

        // Get strings from config.yml
        String alert = plugin.getConfig().getString("books.alert");
        String console = plugin.getConfig().getString("books.console");

        // Variable replacements
        alert = alert.replace("{player}", player.getName());
        alert = alert.replace("{bookaction}", bookAction);
        alert = alert.replace("{booktitle}", bookTitle);
        alert = alert.replace("{book}", alertContent);
        alert = ChatColor.translateAlternateColorCodes('&', alert);

        console = console.replace("{player}", player.getName());
        console = console.replace("{bookaction}", bookAction);
        console = console.replace("{booktitle}", bookTitle);
        console = console.replace("{book}", content);

        // Show this to players with the permission betterlogs.alerts.book
        // Thank you to Define | abyssmc.org for suggestion this method of messaging staff
        Bukkit.broadcast(alert, "betterlogs.alerts.book");

        // If enabled in the config, send book data to console
        if(plugin.getConfig().getBoolean("log.books"))
            BetterLogs.sendToConsole(console);

        // If enabled, send to Discord webhook
        if( plugin.getConfig().getBoolean("books.discord.enabled") ) {
            Webhook webhook = new Webhook();
            String url = plugin.getConfig().getString("books.discord.webhook");
            String image = plugin.getConfig().getString("books.discord.image");
            webhook.send(url, "Book", console, image);
        }
    }
}
