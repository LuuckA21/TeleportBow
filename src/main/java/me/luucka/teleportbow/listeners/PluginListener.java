package me.luucka.teleportbow.listeners;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.luucka.teleportbow.Settings;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class PluginListener implements Listener {

	private final TeleportBow plugin;
	private final Settings settings;

	private final Multimap<UUID, Integer> tpArrows = ArrayListMultimap.create();

	public PluginListener(final TeleportBow plugin) {
		this.plugin = plugin;
		settings = plugin.getSettings();
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		if (settings.isGiveOnJoin()) {
			plugin.giveBow(event.getPlayer());
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		tpArrows.removeAll(event.getPlayer().getUniqueId());
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

		if (plugin.checkBow(bow)) {
			final int entityId = event.getProjectile().getEntityId();
			tpArrows.put(player.getUniqueId(), entityId);
			Bukkit.getScheduler().runTask(plugin, () -> player.getInventory().setItem(settings.getArrowSlot(), new ItemStack(Material.ARROW, 1)));
			(new BukkitRunnable() {
				@Override
				public void run() {
					tpArrows.remove(player.getUniqueId(), entityId);
				}
			}).runTaskLaterAsynchronously(plugin, 600L);
		}
	}

	@EventHandler
	public void onItemDrop(final PlayerDropItemEvent event) {
		if (!settings.isCanBeDropped()) {
			if (plugin.checkBow(event.getItemDrop().getItemStack())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		if (!settings.isCanBeMovedInInventory()) {
			final ItemStack item = event.getCurrentItem();
			if (item == null) return;
			if (plugin.checkBow(item)) {
				event.setCancelled(true);
			}
		}
	}

}
