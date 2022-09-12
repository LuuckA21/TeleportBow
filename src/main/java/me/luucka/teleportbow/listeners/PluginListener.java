package me.luucka.teleportbow.listeners;

import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PluginListener implements Listener {

    private final TeleportBow plugin;

    public PluginListener(TeleportBow plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getSettings().isGiveOnJoin()) {
            plugin.giveBow(event.getPlayer());
        }
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (event.getEntityType() != EntityType.ARROW ||
                !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (plugin.checkBow(itemInMainHand)) {
            Location location = event.getEntity().getLocation();
            event.getEntity().remove();
            location.setYaw(player.getLocation().getYaw());
            location.setPitch(player.getLocation().getPitch());
            player.teleport(location);
        }
    }

    @EventHandler
    public void onPlayerShootArrow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (plugin.checkBow(itemInMainHand)) {
            player.getInventory().setItem(plugin.getSettings().getArrowSlot(), new ItemStack(Material.ARROW, 1));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.getSettings().isCanBeMovedInInventory()) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (plugin.checkBow(item)) {
                event.setCancelled(true);
            }
        }
    }

}
