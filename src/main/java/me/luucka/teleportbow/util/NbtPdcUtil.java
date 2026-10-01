package me.luucka.teleportbow.util;

import de.tr7zw.changeme.nbtapi.NBT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NbtPdcUtil {

	public static void setNBTTags(final ItemStack item, final Map<String, String> tags) {
		tags.forEach((key, value) -> setNBTTag(item, key, value));
	}

	public static void setNBTTag(final ItemStack item, final String key, final String value) {
		final boolean remove = value == null || value.isEmpty();
		NBT.modify(item, tag -> {
			if (remove) {
				if (tag.hasTag(key)) tag.removeKey(key);
			} else {
				tag.setString(key, value);
			}
		});
	}

	public static String getNBTTag(final ItemStack item, final String key) {
		return NBT.get(item, nbt -> {
			return nbt.getString(key);
		});
	}

	public static void setPDCTags(final ItemStack item, final Map<String, String> tags) {
		tags.forEach((key, value) -> setPDCTag(item, key, value));
	}

	public static void setPDCTag(final ItemStack item, final String key, final String value) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return;

		final NamespacedKey namespacedKey = new NamespacedKey(TeleportBow.getInstance(), key);
		meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
		item.setItemMeta(meta);
	}

	public static String getPDCTag(final ItemStack item, final String key) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;

		final NamespacedKey namespacedKey = new NamespacedKey(TeleportBow.getInstance(), key);
		return meta.getPersistentDataContainer().get(namespacedKey, PersistentDataType.STRING);
	}

}
