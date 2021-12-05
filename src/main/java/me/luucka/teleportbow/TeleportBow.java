package me.luucka.teleportbow;

import lombok.Getter;
import me.luucka.teleportbow.commands.CmdTpBow;
import me.luucka.teleportbow.listeners.PluginListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class TeleportBow extends JavaPlugin {

    @Getter
    private static TeleportBow plugin;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        Objects.requireNonNull(getCommand("tpbow")).setExecutor(new CmdTpBow());
        getServer().getPluginManager().registerEvents(new PluginListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

}
