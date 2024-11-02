package me.luucka.teleportbow;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.tr7zw.changeme.nbtapi.NBT;
import me.luucka.teleportbow.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static me.luucka.teleportbow.util.Color.colorize;

public final class BowManager {

	private BowManager() {
	}

	private static final Multimap<UUID, Integer> TP_ARROW = MultimapBuilder.hashKeys().arrayListValues().build();

	public static Multimap<UUID, Integer> getTpArrow() {
		return TP_ARROW;
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
