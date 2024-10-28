package me.luucka.teleportbow;

import me.luucka.teleportbow.command.TpBowCommand;
import me.luucka.teleportbow.listener.MainListener;
import me.luucka.teleportbow.listener.SwapHandListener;
import me.luucka.teleportbow.util.ServerVersion;
import me.luucka.teleportbow.util.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportBow extends JavaPlugin {

	private static TeleportBow instance;

	@Override
	public void onEnable() {
		instance = this;

		Settings.load();

		if (Settings.CHECK_FOR_UPDATES) checkForUpdates();

		getCommand("tpbow").setExecutor(new TpBowCommand());

		getServer().getPluginManager().registerEvents(new MainListener(), this);

		if (ServerVersion.MINOR >= 9) {
			getServer().getPluginManager().registerEvents(new SwapHandListener(), this);
		}
	}

	public static TeleportBow getInstance() {
		return instance;
	}

	private void checkForUpdates() {
		new UpdateChecker(this, 89723).getVersion(version -> {
			if (getDescription().getVersion().equals(version)) {
				getLogger().info("There is not a new update available.");
			} else {
				getLogger().info("There is a new update available.");
				getLogger().info("Current version: " + getDescription().getVersion());
				getLogger().info("Latest version: " + version);
				getLogger().info("Download at: https://www.spigotmc.org/resources/teleportbow.89723/");
			}
		});
	}

}
