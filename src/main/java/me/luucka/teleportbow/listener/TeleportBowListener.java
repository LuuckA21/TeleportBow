package me.luucka.teleportbow.listener;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.TeleportBow;
import me.luucka.teleportbow.hook.HookManager;
import me.luucka.teleportbow.setting.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static me.luucka.teleportbow.util.Color.colorize;

public final class TeleportBowListener implements Listener {

	// Fall damage is ignored for a short time after the teleport
	private static final long FALL_IMMUNITY_MILLIS = 1000L;

	private final Map<UUID, Long> fallImmunityUntil = new HashMap<>();

	// Players whose bow was removed from the death drops, it is given back on respawn
	private final Set<UUID> bowLostOnDeath = new HashSet<>();

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

		if (Settings.NEEDED_PERMISSION) {
			if (!player.hasPermission("tpbow.use") && !player.hasPermission("tpbow.bypass")) {
				event.setCancelled(true);
				player.sendMessage(colorize(Settings.BOW_NOT_ALLOWED));
				return;
			}
		}

		if (isWorldBlocked(player.getWorld()) && !player.hasPermission("tpbow.bypass")) {
			event.setCancelled(true);
			player.sendMessage(colorize(Settings.WORLD_NOT_ALLOWED));
			return;
		}

		if (isRegionBlocked(player) && !player.hasPermission("tpbow.bypass")) {
			event.setCancelled(true);
			player.sendMessage(colorize(Settings.REGION_NOT_ALLOWED));
			return;
		}

		if (event.getProjectile().getType() != EntityType.ARROW) {
			return;
		}

		final int entityId = event.getProjectile().getEntityId();

		BowManager.getTpArrows().put(player.getUniqueId(), entityId);

		Bukkit.getScheduler().runTask(TeleportBow.getInstance(), () -> BowManager.giveArrow(player));

		Bukkit.getScheduler().runTaskLater(TeleportBow.getInstance(), () -> BowManager.getTpArrows().remove(player.getUniqueId(), entityId), 600L);
	}

	@EventHandler
	public void onArrowHit(final ProjectileHitEvent event) {
		if (event.getEntityType() != EntityType.ARROW ||
				!(event.getEntity().getShooter() instanceof Player)) {
			return;
		}

		final Player player = (Player) event.getEntity().getShooter();
		final Location playerLocation = player.getLocation();
		final Projectile projectile = event.getEntity();
		final int entityId = projectile.getEntityId();
		if (!BowManager.getTpArrows().get(player.getUniqueId()).contains(entityId)) return;

		final Location arrowLocation = getSafeLocation(projectile.getLocation(), projectile.getVelocity());

		arrowLocation.setYaw(playerLocation.getYaw());
		arrowLocation.setPitch(playerLocation.getPitch());

		event.getEntity().remove();

		player.setFallDistance(0F);
		player.teleport(arrowLocation);
		fallImmunityUntil.put(player.getUniqueId(), System.currentTimeMillis() + FALL_IMMUNITY_MILLIS);

		if (Settings.SOUND_ENABLE) {
			Bukkit.getScheduler().runTaskLater(TeleportBow.getInstance(), () -> Settings.SOUND_TYPE.play(player, Settings.SOUND_VOLUME, Settings.SOUND_PITCH), 1L);
		}

		// Kept for a short time, so onPlayerHitByArrow still recognizes the arrow
		Bukkit.getScheduler().runTaskLater(TeleportBow.getInstance(), () -> BowManager.getTpArrows().remove(player.getUniqueId(), entityId), 20L);
	}

	@EventHandler
	public void onPlayerFallAfterTeleport(final EntityDamageEvent event) {
		if (event.getEntity() instanceof Player && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
			final Long until = fallImmunityUntil.remove(event.getEntity().getUniqueId());
			if (until != null && System.currentTimeMillis() <= until) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerHitByArrow(final EntityDamageByEntityEvent event) {
		if (!Settings.ARROW_DAMAGE) {
			if (event.getEntity() instanceof Player && event.getDamager() instanceof Arrow) {
				if (BowManager.getTpArrows().containsValue(event.getDamager().getEntityId())) {
					event.setCancelled(true);
				}
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
	public void onDeath(final PlayerDeathEvent event) {
		if (Settings.CAN_BE_DROPPED) return;

		if (event.getDrops().removeIf(BowManager::isValidBow)) {
			bowLostOnDeath.add(event.getEntity().getUniqueId());
		}
	}

	@EventHandler
	public void onRespawn(final PlayerRespawnEvent event) {
		final Player player = event.getPlayer();
		if (bowLostOnDeath.remove(player.getUniqueId())) {
			Bukkit.getScheduler().runTask(TeleportBow.getInstance(), () -> BowManager.giveBow(player));
		}
	}

	@EventHandler
	public void onQuit(final PlayerQuitEvent event) {
		BowManager.getTpArrows().removeAll(event.getPlayer().getUniqueId());
		fallImmunityUntil.remove(event.getPlayer().getUniqueId());
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
			if (item != null && BowManager.isValidBow(item)) {
				event.setCancelled(true);
				return;
			}

			// Number keys swap the clicked slot with a hotbar slot, which can hold the bow
			if (event.getClick() == ClickType.NUMBER_KEY) {
				final ItemStack hotbarItem = event.getWhoClicked().getInventory().getItem(event.getHotbarButton());
				if (hotbarItem != null && BowManager.isValidBow(hotbarItem)) {
					event.setCancelled(true);
				}
			}

			// The F key swaps the clicked slot with the off hand (1.16+, ClickType.SWAP_OFFHAND does not exist before)
			if ("SWAP_OFFHAND".equals(event.getClick().name())) {
				final ItemStack offHandItem = event.getWhoClicked().getInventory().getItemInOffHand();
				if (offHandItem != null && BowManager.isValidBow(offHandItem)) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onInventoryDrag(final InventoryDragEvent event) {
		if (!Settings.CAN_BE_MOVED_IN_INVENTORY) {
			final ItemStack item = event.getOldCursor();
			if (item != null && BowManager.isValidBow(item)) {
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

	/**
	 * When the arrow hits the side of a block its location is inside the block: the location is moved back along
	 * the arrow direction until the player fits, and centered in the block so the player is not stuck in the wall.
	 */
	private static Location getSafeLocation(final Location arrowLocation, final Vector velocity) {
		if (fitsPlayer(arrowLocation)) return arrowLocation;

		final Vector step = velocity.lengthSquared() > 0
				? velocity.clone().normalize().multiply(-0.25)
				: new Vector(0, 0.25, 0);

		final Location location = arrowLocation.clone();
		for (int i = 0; i < 12; i++) {
			location.add(step);
			if (fitsPlayer(location)) {
				location.setX(location.getBlockX() + 0.5);
				location.setZ(location.getBlockZ() + 0.5);
				return location;
			}
		}
		return arrowLocation;
	}

	private static boolean fitsPlayer(final Location location) {
		final Block feet = location.getBlock();
		return !feet.getType().isSolid() && !feet.getRelative(BlockFace.UP).getType().isSolid();
	}

	private static boolean isWorldBlocked(final World world) {
		final String worldName = world.getName();
		return !"none".equalsIgnoreCase(Settings.WORLDS_LIST_TYPE) && "whitelist".equalsIgnoreCase(Settings.WORLDS_LIST_TYPE) != Settings.WORLDS_LIST.contains(worldName);
	}

	private static boolean isRegionBlocked(final Player player) {
		String type = Settings.REGIONS_LIST_TYPE.toLowerCase();
		List<String> regions = HookManager.getRegions(player.getLocation());
		if (regions.isEmpty()) return false;

		switch (type) {
			case "whitelist":
				return regions.stream().noneMatch(Settings.REGIONS_LIST::contains);
			case "blacklist":
				return regions.stream().anyMatch(Settings.REGIONS_LIST::contains);
			default:
				return false;
		}
	}

}
