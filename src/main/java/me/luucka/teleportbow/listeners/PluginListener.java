package me.luucka.teleportbow.listeners;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.logging.Level;

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
        final ItemStack bow = event.getBow();
        if (bow == null) return;

        if (checkBow(bow)) {
            final int entityId = event.getProjectile().getEntityId();
            tpArrows.put(player.getUniqueId(), entityId);
            player.getInventory().setItem(plugin.getSettings().getArrowSlot(), new ItemStack(Material.ARROW, 1));
            (new BukkitRunnable() {
                @Override
                public void run() {
                    tpArrows.remove(player.getUniqueId(), entityId);
                }
            }).runTaskLaterAsynchronously(plugin, 1200L);
        }
    }

    @EventHandler
    public void onItemDrop(final PlayerDropItemEvent event) {
        if (!plugin.getSettings().isCanBeDropped()) {
            final ItemStack item = event.getItemDrop().getItemStack();
            if (checkBow(item)) {
                event.setCancelled(true);
            }
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

    @EventHandler
    public void onSwapHandItem(final PlayerSwapHandItemsEvent event) {
        final ItemStack mainHand = event.getMainHandItem();
        final ItemStack offHand = event.getOffHandItem();

        if (mainHand == null && offHand == null) return;

        boolean isMainHand = false;
        if (mainHand != null) {
            if (checkBow(mainHand)) isMainHand = true;
        }

        boolean isOffHand = false;
        if (offHand != null) {
            if (checkBow(offHand)) isOffHand = true;
        }

        if (!plugin.getSettings().isCanBeSwapped() && (isMainHand || isOffHand)) event.setCancelled(true);
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
