package me.luucka.teleportbow.listeners;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.luucka.teleportbow.Settings;
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
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PluginListener implements Listener {

    private final TeleportBow plugin;
    private final Settings settings;

    private final Multimap<UUID, Integer> tpArrows = ArrayListMultimap.create();

    public PluginListener(final TeleportBow plugin) {
        this.plugin = plugin;
        this.settings = plugin.getSettings();
    }

    @EventHandler
    public void onJoin(final PlayerJoinEvent event) {
        if (settings.isGiveOnJoin()) {
            plugin.giveBow(event.getPlayer());
        }
    }

    @EventHandler
    public void onArrowHit(final ProjectileHitEvent event) {
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
    public void onPlayerShootBow(final EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player)) {
            return;
        }

        final Player player = (Player) event.getEntity();
        final ItemStack bow = event.getBow();
        if (bow == null) return;

        if (checkBow(bow)) {
            final int entityId = event.getProjectile().getEntityId();
            tpArrows.put(player.getUniqueId(), entityId);
            player.getInventory().setItem(settings.getArrowSlot(), new ItemStack(Material.ARROW, 1));
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
        if (!settings.isCanBeDropped()) {
            final ItemStack item = event.getItemDrop().getItemStack();
            if (checkBow(item)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(final InventoryClickEvent event) {
        if (!settings.isCanBeMovedInInventory()) {
            final ItemStack item = event.getCurrentItem();
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

        if (!settings.isCanBeSwapped() && (isMainHand || isOffHand)) event.setCancelled(true);
    }

    private boolean checkBow(final ItemStack item) {
        if (!item.getType().equals(Material.BOW)) return false;
        NamespacedKey key = new NamespacedKey(plugin, "tpbow");
        PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();
        if (container.has(key, PersistentDataType.STRING)) {
            final String sKey = container.get(key, PersistentDataType.STRING);
            if (sKey == null) return false;
            return sKey.equals("TpBow");
        }
        return false;
    }

}
