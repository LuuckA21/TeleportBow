package me.luucka.teleportbow;

import de.tr7zw.changeme.nbtapi.NBT;
import me.luucka.teleportbow.commands.TpBowCommand;
import me.luucka.teleportbow.listeners.PluginListener;
import me.luucka.teleportbow.listeners.SwapHandListener;
import me.luucka.teleportbow.utils.ItemBuilder;
import me.luucka.teleportbow.utils.ServerVersion;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import static me.luucka.teleportbow.utils.Color.colorize;

public final class TeleportBow extends JavaPlugin {

	private Settings settings;

	@Override
	public void onEnable() {
		settings = new Settings(this);
		getCommand("tpbow").setExecutor(new TpBowCommand(this));

		getServer().getPluginManager().registerEvents(new PluginListener(this), this);

		if (ServerVersion.MINOR >= 9) {
			getServer().getPluginManager().registerEvents(new SwapHandListener(this), this);
		}
	}

	public void giveBow(final Player player) {
		player.getInventory().setItem(settings.getBowSlot(), getBow());
		player.getInventory().setItem(settings.getArrowSlot(), new ItemStack(Material.ARROW, 1));
	}

	private ItemStack getBow() {
		return new ItemBuilder(Material.BOW)
				.setDisplayName(colorize(settings.getBowName()))
				.setLore(colorize(settings.getBowLore()))
				.setUnbreakable(true)
				.hideAttributes()
				.hideUnbreakable()
				.tag("tpbow", "TpBow")
				.toItemStack();
	}

	public boolean checkBow(final ItemStack item) {
		if (item.getType() != Material.BOW) return false;

		String key = NBT.get(item, nbt -> {
			return nbt.getString("tpbow");
		});

		if (key == null) return false;
		return key.equals("TpBow");
	}

	public Settings getSettings() {
		return settings;
	}
}
