package me.luucka.teleportbow;

import lombok.Getter;
import me.luucka.teleportbow.commands.CmdTpBow;
import me.luucka.teleportbow.listeners.PluginListener;
import me.luucka.teleportbow.utils.Color;
import me.luucka.teleportbow.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportBow extends JavaPlugin {

    @Getter
    private Settings settings;

    @Override
    public void onEnable() {
        settings = new Settings(this);
        getCommand("tpbow").setExecutor(new CmdTpBow(this));
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
    }

    public ItemStack createTpBow() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.BOW)
                .setDisplayName(Color.colorize(settings.getBowName()))
                .setLore(Color.colorize(settings.getBowLore()))
                .setPersistentDataContainerValue(this, "tpbow", "TpBow")
                .setUnbreakable(true)
                .hideAttributes()
                .hideUnbreakable();

        return itemBuilder.toItemStack();
    }

}
