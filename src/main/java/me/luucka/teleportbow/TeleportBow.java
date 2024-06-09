package me.luucka.teleportbow;

import me.luucka.teleportbow.commands.TpBowCommand;
import me.luucka.teleportbow.listeners.MainListener;
import me.luucka.teleportbow.listeners.SwapHandListener;
import me.luucka.teleportbow.utils.ServerVersion;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportBow extends JavaPlugin {

	private static TeleportBow instance;

	private Settings settings;

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

	public Settings getSettings() {
		return settings;
	}
}
