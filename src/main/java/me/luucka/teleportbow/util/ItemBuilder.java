package me.luucka.teleportbow.util;

import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * This class help you to create a new {@link ItemStack} from a {@link Material}.
 */
public final class ItemBuilder {

	private static final ItemFactory ITEM_FACTORY = Bukkit.getItemFactory();

	private final Material material;
	private final ItemMeta meta;

	private final Map<String, String> tags = new HashMap<>();

	public ItemBuilder(final Material material) {
		this.material = material;
		meta = ITEM_FACTORY.getItemMeta(material);
	}

	public ItemStack make() {
		final ItemStack item = new ItemStack(material, 1);
		item.setItemMeta(meta);

		NbtPdcUtil.setTags(item, tags);

		return item;
	}

	public ItemBuilder setDisplayName(final String name) {
		meta.setDisplayName(name);
		return this;
	}

	public ItemBuilder setLore(final List<String> lore) {
		meta.setLore(lore);
		return this;
	}

	public ItemBuilder hideAttributes() {
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		return this;
	}

	public ItemBuilder hideUnbreakable() {
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		return this;
	}

	public ItemBuilder setUnbreakable(final boolean unbreakable) {
		if (MinecraftVersion.atLeast(MinecraftVersion.V.v1_11)) {
			meta.setUnbreakable(unbreakable);
		} else {
			// Before 1.11 it is only available through ItemMeta.spigot(). The methods are looked up on the public
			// Bukkit interfaces: the CraftBukkit implementations are not public, so invoking them would fail
			try {
				final Object spigot = ItemMeta.class.getMethod("spigot").invoke(meta);
				Class.forName("org.bukkit.inventory.meta.ItemMeta$Spigot")
						.getMethod("setUnbreakable", boolean.class).invoke(spigot, unbreakable);
			} catch (final ReflectiveOperationException e) {
				TeleportBow.getInstance().getLogger().log(Level.WARNING, "Cannot make the item unbreakable", e);
			}
		}
		return this;
	}

	/**
	 * Adds a custom tag, stored as PDC on 1.14+ and as NBT on older versions.
	 */
	public ItemBuilder addTag(String key, String value) {
		tags.put(key, value);
		return this;
	}

}