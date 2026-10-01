package me.luucka.teleportbow.command;

import me.luucka.teleportbow.setting.Settings;
import me.luucka.teleportbow.util.ItemBuilder;
import me.luucka.teleportbow.util.LegacyNbtMigration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static me.luucka.teleportbow.util.Color.colorize;

public class TestBowCommand implements TabExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(colorize(Settings.NO_CONSOLE));
			return true;
		}
		final Player player = (Player) sender;

		if (args.length == 0) {
			player.sendMessage(colorize("Error!"));
			return true;
		}
		if ("nbt".equals(args[0])) {
			// Bow as created up to 1.9.6, to test the NBT -> PDC migration
			ItemStack bowNbt = new ItemBuilder(Settings.BOW_TYPE)
					.setDisplayName(colorize("Bow NBT"))
					.setUnbreakable(true)
					.hideAttributes()
					.hideUnbreakable()
					.make();
			LegacyNbtMigration.writeLegacyTag(bowNbt, "tpbow", "TpBow");
			player.getInventory().addItem(bowNbt);
		} else if ("pdc".equals(args[0])) {
			ItemStack bowPdc = new ItemBuilder(Settings.BOW_TYPE)
					.setDisplayName(colorize("Bow PDC"))
					.setUnbreakable(true)
					.hideAttributes()
					.hideUnbreakable()
					.addTag("tpbow", "TpBow")
					.make();
			player.getInventory().addItem(bowPdc);
		} else {
			player.sendMessage(colorize("Error!"));
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> suggestions = new ArrayList<>();
		suggestions.add("nbt");
		suggestions.add("pdc");
		return suggestions;
	}
}
