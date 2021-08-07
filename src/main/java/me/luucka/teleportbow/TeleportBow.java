package me.luucka.teleportbow;

import lombok.Getter;
import me.luucka.teleportbow.commands.CmdTpBow;
import me.luucka.teleportbow.listeners.PluginListener;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportBow extends JavaPlugin {

    @Getter
    private static TeleportBow plugin;

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
        getServer().getPluginManager().registerEvents(new PluginListener(), this);
    }
}
