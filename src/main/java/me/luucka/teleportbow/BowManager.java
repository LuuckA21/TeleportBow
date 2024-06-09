package me.luucka.teleportbow;

import de.tr7zw.changeme.nbtapi.NBT;
import me.luucka.teleportbow.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import static me.luucka.teleportbow.utils.Color.colorize;

public final class BowManager {

	private BowManager() {
	}

	public static ItemStack createBow() {
		return new ItemBuilder(Material.BOW)
				.setDisplayName(colorize(Settings.BOW_NAME))
				.setLore(colorize(Settings.BOW_LORE))
				.setUnbreakable(true)
				.hideAttributes()
				.hideUnbreakable()
				.tag("tpbow", "TpBow")
				.make();
	}

	public static void giveBow(final Player player) {
		player.getInventory().setItem(Settings.BOW_SLOT, createBow());
		player.getInventory().setItem(Settings.ARROW_SLOT, new ItemStack(Material.ARROW, 1));
	}

	public static boolean checkBow(final ItemStack bow) {
		if (bow.getType() != Material.BOW) return false;

		String key = NBT.get(bow, nbt -> {
			return nbt.getString("tpbow");
		});

		if (key == null) return false;
		return key.equals("TpBow");
	}
}
