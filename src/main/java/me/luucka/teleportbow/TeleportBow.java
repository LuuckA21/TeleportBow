package me.luucka.teleportbow;

import me.luucka.teleportbow.commands.CmdTpBow;
import me.luucka.teleportbow.listeners.ArrowOnHit;
import me.luucka.teleportbow.listeners.PlayerOnJoin;
import me.luucka.teleportbow.listeners.PlayerShootArrow;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportBow extends JavaPlugin {

    private static TeleportBow plugin;

    public static TeleportBow plugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        registerCommands();
        registerEvents();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    // Register Commands / Listener
    //------------------------------------------------------------------------------------------------------------------

    private void registerCommands() {
        getCommand("tpbow").setExecutor(new CmdTpBow());
    }

    private void registerEvents() {
        getServer().getPluginManager().registerEvents(new ArrowOnHit(), this);
        getServer().getPluginManager().registerEvents(new PlayerOnJoin(), this);
        getServer().getPluginManager().registerEvents(new PlayerShootArrow(), this);
    }
}
