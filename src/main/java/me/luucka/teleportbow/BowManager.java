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

	public static void giveBow(final Player player) {
		player.getInventory().setItem(Settings.BOW_SLOT, createBow());
		player.getInventory().setItem(Settings.ARROW_SLOT, new ItemStack(Material.ARROW, 1));
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
