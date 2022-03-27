package me.luucka.teleportbow.listeners;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PluginListener implements Listener {

    private final TeleportBow PLUGIN;

    public PluginListener(TeleportBow PLUGIN) {
        this.PLUGIN = PLUGIN;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!PLUGIN.getConfig().getBoolean("bow.give-on-join")) return;

        event.getPlayer().getInventory().setItem(PLUGIN.getConfig().getInt("bow.slot"), BowManager.createTpBow());
        event.getPlayer().getInventory().setItem(PLUGIN.getConfig().getInt("bow.arrow-slot"), new ItemStack(Material.ARROW, 1));
    }

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (event.getEntityType() != EntityType.ARROW ||
                !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (checkBow(itemInMainHand)) {
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

        if (checkBow(itemInMainHand)) {
            player.getInventory().setItem(PLUGIN.getConfig()
                    .getInt("bow.arrow-slot"), new ItemStack(Material.ARROW, 1));
        }
    }

    private boolean checkBow(ItemStack itemInMainHand) {
        if (itemInMainHand.getType().equals(Material.BOW)) {
            NamespacedKey key = new NamespacedKey(PLUGIN, "tpbow");
            PersistentDataContainer container = itemInMainHand.getItemMeta().getPersistentDataContainer();
            if (container.has(key, PersistentDataType.STRING)) {
                String sKey = container.get(key, PersistentDataType.STRING);
                return sKey.equals("TpBow");
            }
        }

        return false;
    }

}
