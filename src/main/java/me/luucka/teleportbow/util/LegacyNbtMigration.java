package me.luucka.teleportbow.util;

import de.tr7zw.changeme.nbtapi.NBT;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.bukkit.inventory.ItemStack;

/**
 * Phase 1 of the NBT -> PDC migration, the only class that uses NBT-API.
 * <p>
 * Up to 1.9.6 the bow tag was written as NBT on every version. On 1.14+ those bows have no PDC tag,
 * so the NBT tag is read here once and copied to the PDC. The NBT tag is left in place, so a downgrade still works.
 * <p>
 * Phase 2: delete this class, its usages (BowManager, TestBowCommand) and item-nbt-api from pom.xml.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LegacyNbtMigration {

	/**
	 * Copies a legacy NBT tag to the PDC. 1.14+ only.
	 *
	 * @return the migrated value, or null if the item has no legacy tag
	 */
	public static String migrate(final ItemStack item, final String key) {
		if (!item.hasItemMeta()) return null;

		final String value = NBT.get(item, nbt -> {
			return nbt.hasTag(key) ? nbt.getString(key) : null;
		});
		if (value == null || value.isEmpty()) return null;

		NbtPdcUtil.setTag(item, key, value);
		return value;
	}

	/**
	 * Writes a tag the way 1.9.6 did, to test the migration.
	 */
	public static void writeLegacyTag(final ItemStack item, final String key, final String value) {
		NBT.modify(item, nbt -> {
			nbt.setString(key, value);
		});
	}

}
