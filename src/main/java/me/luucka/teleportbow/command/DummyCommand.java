package me.luucka.teleportbow.command;

import com.cryptomorin.xseries.particles.ParticleDisplay;
import com.cryptomorin.xseries.particles.Particles;
import com.cryptomorin.xseries.particles.XParticle;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DummyCommand implements CommandExecutor {
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final Player player = (Player) sender;
		final Location playerLocation = player.getLocation();

		ParticleDisplay display = ParticleDisplay.of(XParticle.FLAME)
//				.offset(0.5, 0.5, 0.5)
//				.withCount(30)
				.withLocation(playerLocation);
		Particles.diamond(2.0, 1.0, 16.0, display);

		return true;
	}
}
