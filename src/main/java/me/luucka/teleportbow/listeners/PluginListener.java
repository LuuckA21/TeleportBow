package me.luucka.teleportbow.listeners;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
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
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class PluginListener implements Listener {

    private final TeleportBow plugin;

    private final Multimap<UUID, Integer> tpArrows = ArrayListMultimap.create();

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

        final Player player = (Player) event.getEntity().getShooter();

        final int entityId = event.getEntity().getEntityId();
        if (!tpArrows.get(player.getUniqueId()).contains(entityId)) return;

        tpArrows.remove(player.getUniqueId(), entityId);

        final Location location = event.getEntity().getLocation();
        event.getEntity().remove();
        location.setYaw(player.getLocation().getYaw());
        location.setPitch(player.getLocation().getPitch());
        player.teleport(location);
    }

    @EventHandler
    public void onPlayerShootBow(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();

        if (checkBow(event.getBow())) {
            tpArrows.put(player.getUniqueId(), event.getProjectile().getEntityId());
            player.getInventory().setItem(plugin.getSettings().getArrowSlot(), new ItemStack(Material.ARROW, 1));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!plugin.getSettings().isCanBeMovedInInventory()) {
            ItemStack item = event.getCurrentItem();
            if (item == null) return;
            if (checkBow(item)) {
                event.setCancelled(true);
            }
        }
    }

    public boolean checkBow(final ItemStack item) {
        if (item.getType().equals(Material.BOW)) {
            NamespacedKey key = new NamespacedKey(plugin, "tpbow");
            PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
            if (container.has(key, PersistentDataType.STRING)) {
                String sKey = container.get(key, PersistentDataType.STRING);
                return sKey.equals("TpBow");
            }
        }
        return false;
    }

}
