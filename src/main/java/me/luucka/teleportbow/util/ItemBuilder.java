package me.luucka.teleportbow.util;

import de.tr7zw.changeme.nbtapi.NBT;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class help you to create a new {@link ItemStack} from a {@link Material}.
 */
public class ItemBuilder {

	private static final ItemFactory ITEM_FACTORY;

	private final Material material;
	private final ItemMeta meta;
	private final int amount;

	private final Map<String, String> tags = new HashMap<>();

	static {
		ITEM_FACTORY = Bukkit.getItemFactory();
	}

	/**
	 * Constructor
	 *
	 * @param material {@link Material} representing the item
	 */
	public ItemBuilder(final Material material) {
		this(material, 1);
	}

	/**
	 * Constructor
	 *
	 * @param material {@link Material} representing the item
	 * @param amount   of the items will give you
	 */
	public ItemBuilder(final Material material, final int amount) {
		this.material = material;
		meta = ITEM_FACTORY.getItemMeta(material);
		this.amount = amount <= 0 ? 1 : amount;
	}

	/**
	 * Return a new {@link ItemStack} based on 'material' and 'amount'
	 *
	 * @return an {@link ItemStack}
	 */
	public ItemStack make() {
		ItemStack item = new ItemStack(material, amount);
		item.setItemMeta(meta);

		for (final Map.Entry<String, String> entry : tags.entrySet())
			item = setMetadata(item, entry.getKey(), entry.getValue());

		return item;
	}

    /*
        Generic Item section
        ----------------------------------------------------------------------------------------------------------------
     */

	/**
	 * Sets the display name.
	 *
	 * @param name the name to set
	 */
	public ItemBuilder setDisplayName(final String name) {
		meta.setDisplayName(name);
		return this;
	}

	/**
	 * Sets the lore for this item.
	 * Removes lore when given null.
	 *
	 * @param lore the lore that will be set
	 */
	public ItemBuilder setLore(final List<String> lore) {
		meta.setLore(lore);
		return this;
	}

	/**
	 * Set itemflags which should be ignored when rendering a ItemStack in the Client.
	 * <p>
	 * This Method does silently ignore double set itemFlags.
	 */
	public ItemBuilder hideAttributes() {
		meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		return this;
	}

	/**
	 * Set itemflags which should be ignored when rendering a ItemStack in the Client.
	 * <p>
	 * This Method does silently ignore double set itemFlags.
	 */
	public ItemBuilder hideUnbreakable() {
		meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
		return this;
	}

	/**
	 * Sets the unbreakable tag. An unbreakable item will not lose durability.
	 *
	 * @param unbreakable true if set unbreakable
	 */
	public ItemBuilder setUnbreakable(final boolean unbreakable) {
		if (ServerVersion.MINOR >= 11) {
			meta.setUnbreakable(unbreakable);
		} else {
			try {
				if (meta != null) {
					Method spigotMethod = meta.getClass().getMethod("spigot");
					spigotMethod.setAccessible(true);

					Object spigot = spigotMethod.invoke(meta);

					Method setUnbreakableMethod = spigot.getClass().getMethod("setUnbreakable", boolean.class);
					setUnbreakableMethod.setAccessible(true);

					setUnbreakableMethod.invoke(spigot, unbreakable);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	public ItemBuilder tag(String key, String value) {
		tags.put(key, value);

		return this;
	}

	public static ItemStack setMetadata(final ItemStack item, final String key, final String value) {
		final boolean remove = value == null || value.isEmpty();
		final ItemStack clone = new ItemStack(item);

		return NBT.modify(clone, tag -> {
			if (remove) {
				if (tag.hasTag(key))
					tag.removeKey(key);
			} else
				tag.setString(key, value);

			return clone;
		});
	}

}