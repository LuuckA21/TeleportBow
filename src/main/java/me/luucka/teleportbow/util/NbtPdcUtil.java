package me.luucka.teleportbow.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

/**
 * Stores custom String tags on items.
 * <p>
 * 1.14+ uses the PersistentDataContainer, older versions (1.8 - 1.13.2) write a root NBT tag through NMS reflection.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NbtPdcUtil {

	public static boolean isPdcSupported() {
		return MinecraftVersion.atLeast(MinecraftVersion.V.v1_14);
	}

	public static void setTags(final ItemStack item, final Map<String, String> tags) {
		tags.forEach((key, value) -> setTag(item, key, value));
	}

	/**
	 * Sets a tag on the item, a null or empty value removes it.
	 */
	public static void setTag(final ItemStack item, final String key, final String value) {
		final String normalized = value == null || value.isEmpty() ? null : value;
		if (isPdcSupported()) {
			PdcTags.set(item, key, normalized);
		} else {
			NbtTags.set(item, key, normalized);
		}
	}

	/**
	 * @return the tag value, or null if the item does not have it
	 */
	public static String getTag(final ItemStack item, final String key) {
		final String value = isPdcSupported() ? PdcTags.get(item, key) : NbtTags.get(item, key);
		return value == null || value.isEmpty() ? null : value;
	}

}
