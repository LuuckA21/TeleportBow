package me.luucka.teleportbow;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import me.luucka.teleportbow.command.TpBowCommand;
import me.luucka.teleportbow.hook.HookManager;
import me.luucka.teleportbow.listener.TeleportBowListener;
import me.luucka.teleportbow.setting.Settings;
import me.luucka.teleportbow.util.MinecraftVersion;
import me.luucka.teleportbow.util.UpdateChecker;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportBow extends JavaPlugin {

	@Getter
	private static TeleportBow instance;

	@Getter
	private static final String TAG_PREFIX = "TeleportBow_";

	@Override
	public void onEnable() {

		if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_21)) {
			getLogger().severe("Minecraft version " + MinecraftVersion.getFullVersion() + " is not supported!");
			getLogger().severe("Use at least Minecraft version " + MinecraftVersion.V.v1_21);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		instance = this;

		Settings.load();

		HookManager.loadDependencies();

		if (Settings.BSTATS) {
			getLogger().info("bStats instance loaded.");
			Metrics metrics = new Metrics(this, 27393);
		}

		if (Settings.CHECK_FOR_UPDATES) {
			checkForUpdates();
		}

//		getCommand("tpbow").setExecutor(new TpBowCommand());
		this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
			commands.registrar().register(TpBowCommand.build());
		});
		getServer().getPluginManager().registerEvents(new TeleportBowListener(), this);
	}

	private void checkForUpdates() {
		new UpdateChecker(this, 89723).getVersion(version -> {
			if (getDescription().getVersion().equals(version)) {
				getLogger().info("There is no new update available.");
			} else {
				getLogger().info("There is a new update available.");
				getLogger().info("Current version: " + getDescription().getVersion());
				getLogger().info("Latest version: " + version);
				getLogger().info("Download at: https://www.spigotmc.org/resources/teleportbow.89723/");
			}
		});
	}

}
