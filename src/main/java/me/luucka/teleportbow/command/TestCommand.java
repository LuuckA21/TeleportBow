package me.luucka.teleportbow.command;

import me.luucka.teleportbow.Settings;
import me.luucka.teleportbow.data.ParticleEffectsData;
import me.luucka.teleportbow.js.JavaScriptManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static me.luucka.teleportbow.util.Color.colorize;

public class TestCommand implements TabExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(colorize(Settings.NO_CONSOLE));
			return true;
		}
		final Player player = (Player) sender;
		String effect = args[0];
		final Optional<ParticleEffectsData> particleEffectsData = ParticleEffectsData.get(effect);
		if (!particleEffectsData.isPresent()) {
			player.sendMessage("Effect not found!");
			return true;
		}
		ParticleEffectsData data = particleEffectsData.get();
		if (data.getMode() == ParticleEffectsData.Mode.SIMPLE) {
			player.getLocation().getWorld().spawnParticle(data.getParticle(), player.getLocation(), 10, 0, 0, 0, 0);
		} else {
			JavaScriptManager.getInstance().eval(data, player, player.getLocation());
		}

//		player.getLocation().getWorld().spawnParticle(data.getParticle(), player.getLocation(), 10, 0, 0, 0, 0);
		player.playEffect();

		player.sendMessage(data.toString());
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		return Arrays.asList(
				"tornado",
				"cone",
				"inverted-cone"
		);
	}
}
