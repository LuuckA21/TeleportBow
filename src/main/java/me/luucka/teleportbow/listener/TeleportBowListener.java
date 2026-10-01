package me.luucka.teleportbow.listener;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.TeleportBow;
import me.luucka.teleportbow.hook.HookManager;
import me.luucka.teleportbow.setting.Settings;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static me.luucka.teleportbow.util.Color.colorize;

public final class TeleportBowListener implements Listener {

	// Fall damage is ignored for a short time after the teleport
	private static final long FALL_IMMUNITY_MILLIS = 1000L;

	private final Map<UUID, Long> fallImmunityUntil = new HashMap<>();

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

		Bukkit.getScheduler().runTask(TeleportBow.getInstance(), () -> player.getInventory().setItem(Settings.ARROW_SLOT, new ItemStack(Material.ARROW, 1)));

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
		final Location arrowLocation = projectile.getLocation();

		if (!BowManager.getTpArrows().get(player.getUniqueId()).contains(entityId)) return;

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
