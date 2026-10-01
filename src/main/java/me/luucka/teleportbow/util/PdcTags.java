package me.luucka.teleportbow.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PersistentDataContainer backend, 1.14+ only.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class PdcTags {

	private static final Map<String, NamespacedKey> KEYS = new ConcurrentHashMap<>();

	static String get(final ItemStack item, final String key) {
		if (!item.hasItemMeta()) return null;

		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return null;

		return meta.getPersistentDataContainer().get(namespacedKey(key), PersistentDataType.STRING);
	}

	static void set(final ItemStack item, final String key, final String value) {
		final ItemMeta meta = item.getItemMeta();
		if (meta == null) return;

		final PersistentDataContainer container = meta.getPersistentDataContainer();
		if (value == null) {
			container.remove(namespacedKey(key));
		} else {
			container.set(namespacedKey(key), PersistentDataType.STRING, value);
		}
		item.setItemMeta(meta);
	}

	private static NamespacedKey namespacedKey(final String key) {
		return KEYS.computeIfAbsent(key, k -> new NamespacedKey(TeleportBow.getInstance(), k));
	}

}
