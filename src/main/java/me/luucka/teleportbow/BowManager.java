package me.luucka.teleportbow;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.setting.Settings;
import me.luucka.teleportbow.util.ItemBuilder;
import me.luucka.teleportbow.util.LegacyNbtMigration;
import me.luucka.teleportbow.util.MinecraftVersion;
import me.luucka.teleportbow.util.NbtPdcUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

import static me.luucka.teleportbow.util.Color.colorize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BowManager {

	@Getter
	private static final Multimap<UUID, Integer> tpArrows = ArrayListMultimap.create();

	public static ItemStack createBow() {
		return new ItemBuilder(Settings.BOW_TYPE)
				.setDisplayName(colorize(Settings.BOW_NAME))
				.setLore(colorize(Settings.BOW_LORE))
				.setUnbreakable(true)
				.hideAttributes()
				.hideUnbreakable()
				.addTag("tpbow", "TpBow")
				.make();
	}

	/**
	 * Gives the bow and the arrow, removing the bows the player already has.
	 */
	public static void giveBow(final Player player) {
		removeBows(player.getInventory());
		placeItem(player, Settings.BOW_SLOT, createBow());
		giveArrow(player);
	}

	/**
	 * Puts an arrow in the arrow slot, unless it already contains arrows.
	 */
	public static void giveArrow(final Player player) {
		final ItemStack current = player.getInventory().getItem(Settings.ARROW_SLOT);
		if (current != null && current.getType() == Material.ARROW) return;

		placeItem(player, Settings.ARROW_SLOT, new ItemStack(Material.ARROW, 1));
	}

	private static void removeBows(final PlayerInventory inventory) {
		final ItemStack[] contents = inventory.getContents();
		for (int slot = 0; slot < contents.length; slot++) {
			if (contents[slot] != null && isValidBow(contents[slot])) {
				inventory.setItem(slot, null);
			}
		}
	}

	/**
	 * Puts the item in the slot, the previous item is moved to a free slot or dropped if the inventory is full.
	 */
	private static void placeItem(final Player player, final int slot, final ItemStack item) {
		final PlayerInventory inventory = player.getInventory();
		final ItemStack previous = inventory.getItem(slot);
		inventory.setItem(slot, item);

		if (previous == null || previous.getType() == Material.AIR) return;

		for (final ItemStack leftover : inventory.addItem(previous).values()) {
			player.getWorld().dropItemNaturally(player.getLocation(), leftover);
		}
	}

	public static boolean isValidBow(final ItemStack bow) {
		if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_14)) {
			if (bow.getType() != Material.BOW) return false;
		} else {
			if (bow.getType() != Material.BOW && bow.getType() != Material.CROSSBOW) return false;
		}

		String key = getTagWithFallback(bow, "tpbow");
		return key != null && key.equals("TpBow");
	}

	private static String getTagWithFallback(final ItemStack item, final String key) {
		final String value = NbtPdcUtil.getTag(item, key);
		if (value != null || !NbtPdcUtil.isPdcSupported()) return value;

		// Bows created up to 1.9.6 only have the NBT tag: remove in phase 2 of the migration
		return LegacyNbtMigration.migrate(item, key);
	}
}
