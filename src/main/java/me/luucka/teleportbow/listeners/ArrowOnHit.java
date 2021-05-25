package me.luucka.teleportbow.listeners;

import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ArrowOnHit implements Listener {

    @EventHandler
    public void onArrowHit(ProjectileHitEvent event) {
        if (event.getEntityType() != EntityType.ARROW ||
                !(event.getEntity().getShooter() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity().getShooter();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (itemInMainHand.getType().equals(Material.BOW)) {
            NamespacedKey key = new NamespacedKey(TeleportBow.plugin(), "tpbow");
            PersistentDataContainer container = itemInMainHand.getItemMeta().getPersistentDataContainer();
            if (container.has(key, PersistentDataType.STRING)) {
                String sKey = container.get(key, PersistentDataType.STRING);
                if (sKey.equals("TpBow")) {
                    Location location = event.getEntity().getLocation();
                    event.getEntity().remove();
                    location.setYaw(player.getLocation().getYaw());
                    location.setPitch(player.getLocation().getPitch());
                    player.teleport(location);
                }
            }
        }
    }

}
