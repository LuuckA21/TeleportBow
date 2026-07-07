package me.luucka.teleportbow;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.tr7zw.changeme.nbtapi.NBT;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.setting.Settings;
import me.luucka.teleportbow.util.ItemBuilder;
import me.luucka.teleportbow.util.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

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
				.tag("tpbow", "TpBow")
				.pdc("tpbow", "TpBow")
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
//		System.out.println("----- getTagWithFallback -----");
		if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_14)) {
//			System.out.println("Using NBT fallback for tag on item");
			return NBT.get(item, nbt -> {
				return nbt.getString(key);
			});
		}

		String value = getPersistentData(item, key);
		if (value != null && !value.isEmpty()) {
//			System.out.println("Using and find PDC");
			return value;
		}

		value = NBT.get(item, nbt -> {
			return nbt.getString(key);
		});
		if (value != null && !value.isEmpty()) {
//			System.out.println("Using NBT fallback");
			ItemBuilder.setPersistentDataContainer(item, key, value);
		}
		return value;
	}

	private static String getPersistentData(final ItemStack item, final String key) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;

		final NamespacedKey namespacedKey = new NamespacedKey(TeleportBow.getInstance(), key);
		return meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
	}
}
