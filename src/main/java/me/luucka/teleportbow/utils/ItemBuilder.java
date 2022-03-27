package me.luucka.teleportbow.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.Repairable;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * This class help you to create a new {@link ItemStack} from a {@link Material}.
 */
@SuppressWarnings("unused")
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
     * Sets the localized name.
     *
     * @param name the name to set
     */
    public ItemBuilder setLocalizedName(final String name) {
        meta.setLocalizedName(name);
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
     * Sets the lore for this item.
     * Removes lore when given null.
     *
     * @param lore the lore that will be set
     */
    public ItemBuilder setLore(final String... lore) {
        return setLore(Arrays.asList(lore));
    }

    public ItemBuilder setDamage(final int damage) {
        ((Damageable) meta).setDamage(damage);
        return this;
    }

    /**
     * Adds the specified enchantments to this item stack.
     * <p>
     * This method is the same as calling {@link
     * #addEnchantment(Enchantment, int)} for each
     * element of the map.
     *
     * @param enchantments Enchantments to add
     */
    public ItemBuilder addEnchantments(final Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            _addEnchant(entry.getKey(), entry.getValue(), false);
        }
        return this;
    }

    /**
     * Adds the specified {@link Enchantment} to this item stack.
     * <p>
     * If this item stack already contained the given enchantment (at any
     * level), it will be replaced.
     *
     * @param enchantment Enchantment to add
     * @param level Level of the enchantment
     */
    public ItemBuilder addEnchantment(final Enchantment enchantment, final int level) {
        _addEnchant(enchantment, level, false);
        return this;
    }

    /**
     * Adds the specified enchantments to this item stack in an unsafe manner.
     * <p>
     * This method is the same as calling {@link
     * #addUnsafeEnchantment(Enchantment, int)} for
     * each element of the map.
     *
     * @param enchantments Enchantments to add
     */
    public ItemBuilder addUnsafeEnchantments(final Map<Enchantment, Integer> enchantments) {
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            _addEnchant(entry.getKey(), entry.getValue(), true);
        }
        return this;
    }

    /**
     * Adds the specified {@link Enchantment} to this item stack.
     * <p>
     * If this item stack already contained the given enchantment (at any
     * level), it will be replaced.
     * <p>
     * This method is unsafe and will ignore level restrictions or item type.
     * Use at your own discretion.
     *
     * @param enchantment Enchantment to add
     * @param level Level of the enchantment
     */
    public ItemBuilder addUnsafeEnchantment(final Enchantment enchantment, final int level) {
        _addEnchant(enchantment, level, true);
        return this;
    }

    /**
     * Adds the specified enchantment to this item meta.
     *
     * @param enchantment Enchantment to add
     * @param level Level for the enchantment
     * @param ignoreLevelRestriction this indicates the enchantment should be
     *     applied, ignoring the level limit
     */
    private void _addEnchant(final Enchantment enchantment, final int level, final boolean ignoreLevelRestriction) {
        meta.addEnchant(enchantment, level, ignoreLevelRestriction);
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
     *
     * @param flags The hideflags which shouldn't be rendered
     */
    public ItemBuilder addItemFlags(final ItemFlag... flags) {
        meta.addItemFlags(flags);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hideEnchants() {
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
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
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hideDestroys() {
        meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hidePlacedOn() {
        meta.addItemFlags(ItemFlag.HIDE_PLACED_ON);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hidePotionEffects() {
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        return this;
    }

    /**
     * Set itemflags which should be ignored when rendering a ItemStack in the Client.
     * <p>
     * This Method does silently ignore double set itemFlags.
     */
    public ItemBuilder hideDye() {
        meta.addItemFlags(ItemFlag.HIDE_DYE);
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

    /**
     * Sets the repair penalty
     *
     * @param cost repair penalty
     */
    public ItemBuilder setRepairCost(final int cost) {
        ((Repairable) meta).setRepairCost(cost);
        return this;
    }

}