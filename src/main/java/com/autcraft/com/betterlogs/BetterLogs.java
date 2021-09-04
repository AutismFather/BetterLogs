package com.autcraft.com.betterlogs;

import com.autcraft.com.betterlogs.listeners.AnvilSpyListener;
import com.autcraft.com.betterlogs.listeners.BookSpyListener;
import com.autcraft.com.betterlogs.listeners.SignSpyListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.print.Book;

public final class BetterLogs extends JavaPlugin {

    private SignSpyListener signSpyListener;
    private AnvilSpyListener anvilSpyListener;
    private BookSpyListener bookSpyListener;

    @Override
    public void onEnable() {
        // Plugin startup logic
        // initialize the config
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);

        // Sign Spy listener
        signSpyListener = new SignSpyListener(this);
        getServer().getPluginManager().registerEvents(signSpyListener, this);

        // Anvil Spy Listener
        anvilSpyListener = new AnvilSpyListener(this);
        getServer().getPluginManager().registerEvents(anvilSpyListener, this);

        // Book Spy Listener
        bookSpyListener = new BookSpyListener(this);
        getServer().getPluginManager().registerEvents(bookSpyListener,this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    /**
     * Just a simpler and easier way to send messages to the log/console
     *
     * @param message
     */
    public static void sendToConsole(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }
}
