package me.luucka.teleportbow;

import lombok.Getter;
import me.luucka.teleportbow.commands.CmdTpBow;
import me.luucka.teleportbow.listeners.PluginListener;
import me.luucka.teleportbow.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static me.luucka.teleportbow.utils.Color.colorize;

public final class TeleportBow extends JavaPlugin {

    @Getter
    private Settings settings;

    @Override
    public void onEnable() {
        settings = new Settings(this);
        getCommand("tpbow").setExecutor(new CmdTpBow(this));
        getServer().getPluginManager().registerEvents(new PluginListener(this), this);
    }

    public void giveBow(final Player player) {
        player.getInventory().setItem(getSettings().getBowSlot(), getBow());
        player.getInventory().setItem(getSettings().getArrowSlot(), new ItemStack(Material.ARROW, 1));
    }

    private ItemStack getBow() {
        ItemBuilder itemBuilder = new ItemBuilder(Material.BOW)
                .setDisplayName(colorize(settings.getBowName()))
                .setLore(colorize(settings.getBowLore()))
                .setPersistentDataContainerValue(this, "tpbow", "TpBow")
                .setUnbreakable(true)
                .hideAttributes()
                .hideUnbreakable();

        return itemBuilder.toItemStack();
    }

}
