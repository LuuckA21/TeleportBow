package me.luucka.teleportbow;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import de.tr7zw.changeme.nbtapi.NBT;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.util.ItemBuilder;
import me.luucka.teleportbow.util.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static me.luucka.teleportbow.util.Color.colorize;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BowManager {

	@Getter
	private static final Multimap<UUID, Integer> tpArrows = ArrayListMultimap.create();

	public static ItemStack createBow() {
		return new ItemBuilder(Settings.BOW_TYPE)
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
		if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_14)) {
			// For versions older than 1.14, only allow BOW
			if (bow.getType() != Material.BOW) return false;
		} else {
			// For versions 1.14 and newer, allow BOW or CROSSBOW
			if (bow.getType() != Material.BOW && bow.getType() != Material.CROSSBOW) return false;
		}

		String key = NBT.get(bow, nbt -> {
			return nbt.getString("tpbow");
		});

		if (key == null || key.isEmpty()) return false;
		return key.equals("TpBow");
	}
}
