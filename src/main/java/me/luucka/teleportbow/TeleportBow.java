package me.luucka.teleportbow;

import lombok.Getter;
import me.luucka.teleportbow.command.TpBowCommand;
import me.luucka.teleportbow.hook.HookManager;
import me.luucka.teleportbow.listener.TeleportBowListener;
import me.luucka.teleportbow.setting.Settings;
import me.luucka.teleportbow.util.MinecraftVersion;
import me.luucka.teleportbow.util.UpdateChecker;
import net.byteflux.libby.BukkitLibraryManager;
import net.byteflux.libby.Library;
import org.bukkit.plugin.java.JavaPlugin;

public final class TeleportBow extends JavaPlugin {

	@Getter
	private static TeleportBow instance;


	private BukkitLibraryManager libraryManager;

	@Override
	public void onEnable() {
		if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_7)) {
			getLogger().severe("Minecraft version " + MinecraftVersion.getFullVersion() + " is not supported!");
			getLogger().severe("Use at least Minecraft version " + MinecraftVersion.V.v1_7);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		instance = this;

		this.libraryManager = new BukkitLibraryManager(this);
		libraryManager.addMavenCentral();
		loadLibraries();

		Settings.load();

//		this.worldGuardRegionService = hookWorldGuard();
		HookManager.loadDependencies();

		if (Settings.BSTATS) {
			getLogger().info("bStats instance loaded.");
			Metrics metrics = new Metrics(this, 27393);
		}

		if (Settings.CHECK_FOR_UPDATES) {
			checkForUpdates();
		}

		getCommand("tpbow").setExecutor(new TpBowCommand());

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

	private void loadLibraries() {
		final Library lib = Library.builder()
				.groupId("com{}github{}cryptomorin")
				.artifactId("XSeries")
				.version("13.4.0")

				.build();
		libraryManager.loadLibrary(lib);
	}

}
