package me.luucka.teleportbow;

import me.luucka.teleportbow.command.TpBowCommand;
import me.luucka.teleportbow.listener.MainListener;
import me.luucka.teleportbow.listener.SwapHandListener;
import me.luucka.teleportbow.util.ServerVersion;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportBow extends JavaPlugin {

	private static TeleportBow instance;

	@Override
	public void onEnable() {
		instance = this;

		Settings.init();

		getCommand("tpbow").setExecutor(new TpBowCommand());

		getServer().getPluginManager().registerEvents(new MainListener(), this);

		if (ServerVersion.MINOR >= 9) {
			getServer().getPluginManager().registerEvents(new SwapHandListener(), this);
		}
	}

	public static TeleportBow getInstance() {
		return instance;
	}

}
