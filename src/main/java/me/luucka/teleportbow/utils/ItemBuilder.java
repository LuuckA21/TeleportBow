package me.luucka.teleportbow.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

/**
 * This class help you to create a new {@link ItemStack} from a {@link Material}.
 */
public class ItemBuilder {

    private static final ItemFactory ITEM_FACTORY;

    private final Material material;
    private final ItemMeta meta;
    private final int amount;

    static {
        ITEM_FACTORY = Bukkit.getItemFactory();
    }

    /**
     * Constructor
     * @param material {@link Material} representing the item
     */
    public ItemBuilder(final Material material) {
        this(material, 1);
    }

    /**
     * Constructor
     * @param material {@link Material} representing the item
     * @param amount of the items will give you
     */
    public ItemBuilder(final Material material, final int amount) {
        this.material = material;
        this.meta = ITEM_FACTORY.getItemMeta(material);
        this.amount = amount <= 0 ? 1 : amount;
    }

    /**
     * Return a new {@link ItemStack} based on 'material' and 'amount'
     * @return an {@link ItemStack}
     */
    public ItemStack toItemStack() {
        ItemStack item = new ItemStack(this.material, this.amount);
        item.setItemMeta(this.meta);
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
     * Set a persistent data container value into this item
     * @param plugin your plugin class extends {@link JavaPlugin}
     * @param key a key reference for your value
     * @param value value
     */
    public ItemBuilder setPersistentDataContainerValue(final JavaPlugin plugin, final String key, final String value) {
        NamespacedKey namespacedKey = new NamespacedKey(plugin, key);
        meta.getPersistentDataContainer().set(namespacedKey, PersistentDataType.STRING, value);
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
        meta.setUnbreakable(unbreakable);
        return this;
    }

}