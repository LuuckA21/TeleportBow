package me.luucka.teleportbow.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

/**
 * Root NBT tag backend through NMS reflection, 1.8 - 1.13.2 only.
 * <p>
 * Uses the Spigot names of those versions (net.minecraft.server.vX_Y_RZ), which do not exist on 1.17+.
 * Writes the same root String tag NBT-API used to write, so bows created by older plugin versions keep working.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
final class NbtTags {

	// CraftItemStack
	private static final Method AS_NMS_COPY;
	private static final Method AS_BUKKIT_COPY;

	// net.minecraft.server.ItemStack
	private static final Method GET_TAG;
	private static final Method SET_TAG;

	// net.minecraft.server.NBTTagCompound
	private static final Constructor<?> NEW_COMPOUND;
	private static final Method HAS_KEY;
	private static final Method GET_STRING;
	private static final Method SET_STRING;
	private static final Method REMOVE;

	static {
		final String craftPackage = Bukkit.getServer().getClass().getPackage().getName();
		final String version = craftPackage.substring(craftPackage.lastIndexOf('.') + 1);
		final String nmsPackage = "net.minecraft.server." + version;

		try {
			final Class<?> craftItemStack = Class.forName(craftPackage + ".inventory.CraftItemStack");
			final Class<?> nmsItemStack = Class.forName(nmsPackage + ".ItemStack");
			final Class<?> compound = Class.forName(nmsPackage + ".NBTTagCompound");

			AS_NMS_COPY = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
			AS_BUKKIT_COPY = craftItemStack.getMethod("asBukkitCopy", nmsItemStack);

			GET_TAG = nmsItemStack.getMethod("getTag");
			SET_TAG = nmsItemStack.getMethod("setTag", compound);

			NEW_COMPOUND = compound.getConstructor();
			HAS_KEY = compound.getMethod("hasKey", String.class);
			GET_STRING = compound.getMethod("getString", String.class);
			SET_STRING = compound.getMethod("setString", String.class, String.class);
			REMOVE = compound.getMethod("remove", String.class);
		} catch (final ReflectiveOperationException e) {
			throw new IllegalStateException("Cannot access NBT on server version " + version, e);
		}
	}

	static String get(final ItemStack item, final String key) {
		if (!item.hasItemMeta()) return null;

		try {
			final Object nmsItem = AS_NMS_COPY.invoke(null, item);
			if (nmsItem == null) return null;

			final Object tag = GET_TAG.invoke(nmsItem);
			if (tag == null || !(boolean) HAS_KEY.invoke(tag, key)) return null;

			return (String) GET_STRING.invoke(tag, key);
		} catch (final ReflectiveOperationException e) {
			throw new IllegalStateException("Cannot read NBT tag '" + key + "'", e);
		}
	}

	static void set(final ItemStack item, final String key, final String value) {
		try {
			final Object nmsItem = AS_NMS_COPY.invoke(null, item);
			if (nmsItem == null) return;

			Object tag = GET_TAG.invoke(nmsItem);
			if (value == null) {
				if (tag == null || !(boolean) HAS_KEY.invoke(tag, key)) return;
				REMOVE.invoke(tag, key);
			} else {
				if (tag == null) {
					tag = NEW_COMPOUND.newInstance();
					SET_TAG.invoke(nmsItem, tag);
				}
				SET_STRING.invoke(tag, key, value);
			}

			// Unknown root tags are kept by CraftMetaItem, so copying the meta back works for both
			// plain ItemStacks and CraftItemStack mirrors
			final ItemStack updated = (ItemStack) AS_BUKKIT_COPY.invoke(null, nmsItem);
			item.setItemMeta(updated.getItemMeta());
		} catch (final ReflectiveOperationException e) {
			throw new IllegalStateException("Cannot write NBT tag '" + key + "'", e);
		}
	}

}
