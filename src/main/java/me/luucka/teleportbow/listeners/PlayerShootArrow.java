package me.luucka.teleportbow.listeners;

import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class PlayerShootArrow implements Listener {

    @EventHandler
    public void onPlayerShootArrow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        Player player = (Player) event.getEntity();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (itemInMainHand.getType().equals(Material.BOW)) {
            NamespacedKey key = new NamespacedKey(TeleportBow.plugin(), "tpbow");
            PersistentDataContainer container = itemInMainHand.getItemMeta().getPersistentDataContainer();
            if (container.has(key, PersistentDataType.STRING)) {
                String sKey = container.get(key, PersistentDataType.STRING);
                if (sKey.equals("TpBow")) {
                    player.getInventory().setItem(TeleportBow.plugin().getConfig()
                            .getInt("bow.arrow-slot"), new ItemStack(Material.ARROW, 1));
                }
            }
        }
    }

}
