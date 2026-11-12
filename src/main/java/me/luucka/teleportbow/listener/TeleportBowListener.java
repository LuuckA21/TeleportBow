package me.luucka.teleportbow.listener;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.Permissions;
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
import org.bukkit.event.Cancellable;
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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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

	@EventHandler(ignoreCancelled = true)
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
			if (!player.hasPermission(Permissions.USE) && !player.hasPermission(Permissions.BYPASS)) {
				event.setCancelled(true);
				player.sendMessage(Settings.BOW_NOT_ALLOWED);
				return;
			}
		}

		if (isWorldBlocked(player.getWorld()) && !player.hasPermission(Permissions.BYPASS)) {
			event.setCancelled(true);
			player.sendMessage(Settings.WORLD_NOT_ALLOWED);
			return;
		}

		if (isRegionBlocked(player) && !player.hasPermission(Permissions.BYPASS)) {
			event.setCancelled(true);
			player.sendMessage(Settings.REGION_NOT_ALLOWED);
			return;
		}

		if (event.getProjectile().getType() != EntityType.ARROW) {
			return;
		}

		final int entityId = event.getProjectile().getEntityId();

		BowManager.getTpArrows().put(entityId, player.getUniqueId());

		Bukkit.getScheduler().runTask(TeleportBow.getInstance(), () -> BowManager.giveArrow(player));

		Bukkit.getScheduler().runTaskLater(TeleportBow.getInstance(), () -> BowManager.getTpArrows().remove(entityId), 600L);
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
		if (!player.getUniqueId().equals(BowManager.getTpArrows().get(entityId))) return;

		projectile.remove();

		final Location arrowLocation = getSafeLocation(projectile.getLocation(), projectile.getVelocity());
		if (arrowLocation == null) return;

		arrowLocation.setYaw(playerLocation.getYaw());
		arrowLocation.setPitch(playerLocation.getPitch());

		if (!player.teleport(arrowLocation)) return;

		player.setFallDistance(0F);
		fallImmunityUntil.put(player.getUniqueId(), System.currentTimeMillis() + FALL_IMMUNITY_MILLIS);

		if (Settings.SOUND_ENABLE) {
			Bukkit.getScheduler().runTaskLater(TeleportBow.getInstance(), () -> Settings.SOUND_TYPE.play(player, Settings.SOUND_VOLUME, Settings.SOUND_PITCH), 1L);
		}

		// Kept for a short time, so onPlayerHitByArrow still recognizes the arrow
		Bukkit.getScheduler().runTaskLater(TeleportBow.getInstance(), () -> BowManager.getTpArrows().remove(entityId), 20L);
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
		if (!Settings.ARROW_DAMAGE && event.getEntity() instanceof Player && event.getDamager() instanceof Arrow
				&& BowManager.getTpArrows().containsKey(event.getDamager().getEntityId())) {
			event.setCancelled(true);
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
		if (!Settings.KEEP_ON_DEATH) return;

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
		BowManager.getTpArrows().values().removeIf(event.getPlayer().getUniqueId()::equals);
		fallImmunityUntil.remove(event.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onItemDrop(final PlayerDropItemEvent event) {
		if (!Settings.CAN_BE_DROPPED) cancelIfBow(event, event.getItemDrop().getItemStack());
	}

	@EventHandler
	public void onInventoryClick(final InventoryClickEvent event) {
		if (Settings.CAN_BE_MOVED_IN_INVENTORY) return;

		cancelIfBow(event, event.getCurrentItem());

		// Number keys swap the clicked slot with a hotbar slot, which can hold the bow
		if (event.getClick() == ClickType.NUMBER_KEY) {
			cancelIfBow(event, event.getWhoClicked().getInventory().getItem(event.getHotbarButton()));
		}

		// The F key swaps the clicked slot with the off hand (1.16+, ClickType.SWAP_OFFHAND does not exist before)
		if ("SWAP_OFFHAND".equals(event.getClick().name())) {
			cancelIfBow(event, event.getWhoClicked().getInventory().getItemInOffHand());
		}
	}

	@EventHandler
	public void onInventoryDrag(final InventoryDragEvent event) {
		if (!Settings.CAN_BE_MOVED_IN_INVENTORY) cancelIfBow(event, event.getOldCursor());
	}

	static class SwapHandListener implements Listener {

		@EventHandler
		public void onSwapHandItem(final PlayerSwapHandItemsEvent event) {
			if (Settings.CAN_BE_SWAPPED) return;

			cancelIfBow(event, event.getMainHandItem());
			cancelIfBow(event, event.getOffHandItem());
		}
	}

	private static void cancelIfBow(final Cancellable event, final ItemStack item) {
		if (item != null && BowManager.isValidBow(item)) event.setCancelled(true);
	}

	/**
	 * When the arrow hits the side of a block its location is inside the block: the location is moved back along
	 * the arrow direction until the player fits, and centered in the block so the player is not stuck in the wall.
	 * The feet are moved to the bottom of the block, so the player (1.8 blocks tall) stays in the two checked blocks.
	 *
	 * @return the location, or null if there is no room for the player
	 */
	private static Location getSafeLocation(final Location arrowLocation, final Vector velocity) {
		if (fitsPlayer(arrowLocation)) {
			arrowLocation.setY(arrowLocation.getBlockY());
			return arrowLocation;
		}

		final Vector step = velocity.lengthSquared() > 0
				? velocity.clone().normalize().multiply(-0.25)
				: new Vector(0, 0.25, 0);

		final Location location = arrowLocation.clone();
		for (int i = 0; i < 12; i++) {
			location.add(step);
			// A horizontal shot hits at eye height: one block lower fits e.g. a corridor two blocks high
			for (int down = 0; down <= 1; down++) {
				final Location candidate = location.clone().subtract(0, down, 0);
				if (fitsPlayer(candidate)) {
					candidate.setX(candidate.getBlockX() + 0.5);
					candidate.setY(candidate.getBlockY());
					candidate.setZ(candidate.getBlockZ() + 0.5);
					return candidate;
				}
			}
		}
		return null;
	}

	private static boolean fitsPlayer(final Location location) {
		final Block feet = location.getBlock();
		return !feet.getType().isSolid() && !feet.getRelative(BlockFace.UP).getType().isSolid();
	}

	private static boolean isWorldBlocked(final World world) {
		return isBlocked(Settings.WORLDS_LIST_TYPE, Settings.WORLDS_LIST, Collections.singletonList(world.getName()));
	}

	private static boolean isRegionBlocked(final Player player) {
		if ("none".equals(Settings.REGIONS_LIST_TYPE)) return false;

		final List<String> regions = HookManager.getRegions(player.getLocation());
		// Outside every region the bow is allowed, with a whitelist too
		if (regions.isEmpty()) return false;

		return isBlocked(Settings.REGIONS_LIST_TYPE, Settings.REGIONS_LIST, regions);
	}

	/**
	 * @param type   none, whitelist (only the listed values allowed) or blacklist (the listed values denied)
	 * @param values the world or the regions where the player is
	 */
	private static boolean isBlocked(final String type, final List<String> list, final Collection<String> values) {
		switch (type) {
			case "whitelist":
				return values.stream().noneMatch(list::contains);
			case "blacklist":
				return values.stream().anyMatch(list::contains);
			default:
				return false;
		}
	}

}
