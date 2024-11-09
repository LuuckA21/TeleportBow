package me.luucka.teleportbow;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import de.tr7zw.changeme.nbtapi.NBT;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.util.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static me.luucka.teleportbow.util.Color.colorize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BowManager {

	@Getter
	private static final Multimap<UUID, Integer> tpArrows = MultimapBuilder.hashKeys().arrayListValues().build();

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

	public static boolean isValidBow(final ItemStack bow) {
		if (bow.getType() != Material.BOW) return false;

		String key = NBT.get(bow, nbt -> {
			return nbt.getString("tpbow");
		});

		if (key == null || key.isEmpty()) return false;
		return key.equals("TpBow");
	}
}
