package me.luucka.teleportbow;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import lombok.Getter;
import me.luucka.pillars.PillarPlugin;
import me.luucka.pillars.setting.Lang;
import me.luucka.pillars.util.PillarLog;
import me.luucka.teleportbow.command.TpBowCommand;
import me.luucka.teleportbow.hook.HookManager;
import me.luucka.teleportbow.listener.TeleportBowListener;
import me.luucka.teleportbow.setting.Settings;
import me.luucka.teleportbow.setting.SettingsV2;
import me.luucka.teleportbow.util.MinecraftVersion;
import me.luucka.teleportbow.util.UpdateChecker;

public final class TeleportBow extends PillarPlugin {

	@Getter
	private static TeleportBow instance;

	public static final String TAG_PREFIX = "TeleportBow_";

	@Override
	public void onEnable() {

		if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_21)) {
			getLogger().severe("Minecraft version " + MinecraftVersion.getFullVersion() + " is not supported!");
			getLogger().severe("Use at least Minecraft version " + MinecraftVersion.V.v1_21);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		instance = this;

		this.loadLibrary(
				"com.github.cryptomorin",
				"XSeries",
				"13.6.0"
		);

//		Settings.load();
		SettingsV2.load();
		Lang.load();

		HookManager.loadDependencies();

		if (Settings.BSTATS) {
			getLogger().info("bStats instance loaded.");
			Metrics metrics = new Metrics(this, 27393);
		}

		if (Settings.CHECK_FOR_UPDATES) {
			checkForUpdates();
		}

		this.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, commands -> {
			commands.registrar().register(TpBowCommand.build());
		});
		getServer().getPluginManager().registerEvents(new TeleportBowListener(), this);

		String test = SettingsV2.getInstance().getSettings().getString("uno", "test");
		PillarLog.error(test);
	}

	private void checkForUpdates() {
		new UpdateChecker(this, 89723).getVersion(version -> {
			if (getPluginMeta().getVersion().equals(version)) {
				getLogger().info("There is no new update available.");
			} else {
				getLogger().info("There is a new update available.");
				getLogger().info("Current version: " + getPluginMeta().getVersion());
				getLogger().info("Latest version: " + version);
				getLogger().info("Download at: https://www.spigotmc.org/resources/teleportbow.89723/");
			}
		});
	}

}
