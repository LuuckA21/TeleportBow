package me.luucka.teleportbow.listener;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.Settings;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class MainListener implements Listener {

	private final Multimap<UUID, Integer> tpArrows = ArrayListMultimap.create();

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		if (Settings.GIVE_ON_JOIN) {
			BowManager.giveBow(event.getPlayer());
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		tpArrows.removeAll(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onPlayerShootBow(final EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntity();
		final ItemStack bow = event.getBow();
		final int entityId = event.getProjectile().getEntityId();

		if (bow == null) return;
		if (!BowManager.checkBow(bow)) return;

		tpArrows.put(player.getUniqueId(), entityId);

		Bukkit.getScheduler().runTask(TeleportBow.getInstance(), () -> player.getInventory().setItem(Settings.ARROW_SLOT, new ItemStack(Material.ARROW, 1)));

		(new BukkitRunnable() {
			@Override
			public void run() {
				tpArrows.remove(player.getUniqueId(), entityId);
			}
		}).runTaskLaterAsynchronously(TeleportBow.getInstance(), 600L);
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

		final Location location = event.getEntity().getLocation();
		location.setYaw(player.getLocation().getYaw());
		location.setPitch(player.getLocation().getPitch());

		event.getEntity().remove();
		player.teleport(location);
	}

	@EventHandler
	public void onPlayerFallAfterTeleport(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			if (tpArrows.containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onItemDrop(final PlayerDropItemEvent event) {
		if (!Settings.CAN_BE_DROPPED) {
			if (BowManager.checkBow(event.getItemDrop().getItemStack())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		if (!Settings.CAN_BE_MOVED_IN_INVENTORY) {
			final ItemStack item = event.getCurrentItem();
			if (item == null) return;
			if (BowManager.checkBow(item)) {
				event.setCancelled(true);
			}
		}
	}

}
