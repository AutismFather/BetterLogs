package com.autcraft.com.betterlogs;

import com.autcraft.com.betterlogs.listeners.AnvilSpyListener;
import com.autcraft.com.betterlogs.listeners.BookSpyListener;
import com.autcraft.com.betterlogs.listeners.SignSpyListener;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

import java.awt.print.Book;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        configDefaults();

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){

        // If the command is /betterlogs reload
        if(sender.hasPermission("betterlogs.reload") && args.length > 0 && args[0].equalsIgnoreCase("reload")){
            reloadConfig();
            sender.sendMessage("Betterlogs config reloaded");
            return true;
        }

        // Fail if console. Consoles get alerts whether they want them or not.
        if (sender instanceof ConsoleCommandSender) {
            sender.sendMessage("This can not be run from console");
            return true;
        }

        // If this is not a player running the command (who would it be then??), just exit out
        if (!(sender instanceof Player)) {
            return true;
        }
        return false;
    }

    /**
     * Just a simpler and easier way to send messages to the log/console
     *
     * @param message
     */
    public static void sendToConsole(String message){
        Bukkit.getConsoleSender().sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> completions = new ArrayList<>();
        List<String> commands = new ArrayList<>();

        // List of possible arguments
        if (args.length == 1){
            if(sender.hasPermission("betterlogs.reload"))
                commands.add("reload");

            StringUtil.copyPartialMatches(args[0], commands, completions);
        }

        Collections.sort(completions);
        return completions;
    }

    /**
     * Set default config values if they are not set already.
     */
    private void configDefaults(){
        Boolean needSaving = false;

        if(!getConfig().contains("log.anvils")) {
            getConfig().addDefault("log.anvils", true);
            needSaving = true;
        }
        if(!getConfig().contains("log.books")) {
            getConfig().addDefault("log.books", true);
            needSaving = true;
        }
        if(!getConfig().contains("log.signs")) {
            getConfig().addDefault("log.signs", true);
            needSaving = true;
        }
        if(needSaving) {
            saveConfig();
            sendToConsole("BetterLogs config file updated with new default values.");
        }
    }
}