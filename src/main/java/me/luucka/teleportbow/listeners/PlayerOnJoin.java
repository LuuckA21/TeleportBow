package me.luucka.teleportbow.listeners;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerOnJoin implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!TeleportBow.plugin().getConfig().getBoolean("bow.give-on-join")) {
            return;
        }
        event.getPlayer().getInventory().setItem(TeleportBow.plugin().getConfig().getInt("bow.slot"), BowManager.createTpBow());
        event.getPlayer().getInventory().setItem(TeleportBow.plugin().getConfig().getInt("bow.arrow-slot"), new ItemStack(Material.ARROW, 1));
    }

}
