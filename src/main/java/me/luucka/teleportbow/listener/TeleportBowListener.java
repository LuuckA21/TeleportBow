package me.luucka.teleportbow.listener;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.Settings;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import static me.luucka.teleportbow.util.Color.colorize;

public final class TeleportBowListener implements Listener {

	public TeleportBowListener() {
		registerEvent(new SwapHandListener());
	}

	private void registerEvent(Listener listener) {
		try {
			Class.forName("org.bukkit.event.player.PlayerSwapHandItemsEvent");
			TeleportBow.getInstance().getServer().getPluginManager().registerEvents(listener, TeleportBow.getInstance());
		} catch (final ClassNotFoundException ignored) {
		}
	}

	@EventHandler
	public void onPlayerShootBow(final EntityShootBowEvent event) {
		if (!(event.getEntity() instanceof Player)) {
			return;
		}

		final ItemStack bow = event.getBow();
		if (bow == null || !BowManager.isValidBow(bow)) {
			return;
		}

		final Player player = (Player) event.getEntity();
		if (isWorldBlocked(player.getWorld())) {
			event.setCancelled(true);
			player.sendMessage(colorize(Settings.WORLD_NOT_ALLOWED));
			return;
		}

		if (event.getProjectile().getType() != EntityType.ARROW) {
			return;
		}

		final int entityId = event.getProjectile().getEntityId();

		BowManager.getTpArrows().put(player.getUniqueId(), entityId);

		Bukkit.getScheduler().runTask(TeleportBow.getInstance(), () -> player.getInventory().setItem(Settings.ARROW_SLOT, new ItemStack(Material.ARROW, 1)));

		(new BukkitRunnable() {
			@Override
			public void run() {
				BowManager.getTpArrows().remove(player.getUniqueId(), entityId);
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

		if (!BowManager.getTpArrows().get(player.getUniqueId()).contains(entityId)) return;

		final Location location = event.getEntity().getLocation();
		location.setYaw(player.getLocation().getYaw());
		location.setPitch(player.getLocation().getPitch());

		event.getEntity().remove();

		player.teleport(location);

		BowManager.getTpArrows().remove(player.getUniqueId(), entityId);
	}

	@EventHandler
	public void onPlayerFallAfterTeleport(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			final Player player = (Player) event.getEntity();
			if (BowManager.getTpArrows().containsKey(player.getUniqueId())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onJoin(final PlayerJoinEvent event) {
		if (Settings.GIVE_ON_JOIN) {
			BowManager.giveBow(event.getPlayer());
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		BowManager.getTpArrows().removeAll(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onItemDrop(final PlayerDropItemEvent event) {
		if (!Settings.CAN_BE_DROPPED) {
			if (BowManager.isValidBow(event.getItemDrop().getItemStack())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		if (!Settings.CAN_BE_MOVED_IN_INVENTORY) {
			final ItemStack item = event.getCurrentItem();
			if (item == null) return;
			if (BowManager.isValidBow(item)) {
				event.setCancelled(true);
			}
		}
	}

	static class SwapHandListener implements Listener {

		@EventHandler
		public void onSwapHandItem(final PlayerSwapHandItemsEvent event) {
			final ItemStack mainHand = event.getMainHandItem();
			final ItemStack offHand = event.getOffHandItem();

			if (mainHand == null && offHand == null) return;

			boolean isMainHand = false;
			if (mainHand != null) {
				if (BowManager.isValidBow(mainHand)) isMainHand = true;
			}

			boolean isOffHand = false;
			if (offHand != null) {
				if (BowManager.isValidBow(offHand)) isOffHand = true;
			}

			if (!Settings.CAN_BE_SWAPPED && (isMainHand || isOffHand)) event.setCancelled(true);
		}
	}

	private static boolean isWorldBlocked(final World world) {
		final String worldName = world.getName();
		return !"none".equalsIgnoreCase(Settings.WORLDS_LIST_TYPE) && "whitelist".equalsIgnoreCase(Settings.WORLDS_LIST_TYPE) != Settings.WORLDS_LIST.contains(worldName);
	}

}
