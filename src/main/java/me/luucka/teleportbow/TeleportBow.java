package me.luucka.teleportbow;

import lombok.Getter;
import me.luucka.teleportbow.command.TestCommand;
import me.luucka.teleportbow.command.TpBowCommand;
import me.luucka.teleportbow.data.ParticleEffectsData;
import me.luucka.teleportbow.library.LibrarySetup;
import me.luucka.teleportbow.listener.TeleportBowListener;
import me.luucka.teleportbow.util.MinecraftVersion;
import me.luucka.teleportbow.util.UpdateChecker;
import me.luucka.teleportbow.util.Util;
import net.byteflux.libby.BukkitLibraryManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class TeleportBow extends JavaPlugin {

	@Getter
	private static TeleportBow instance;

	@Getter
	private File particlesFolder;

	@Getter
	private File scriptFolder;

	@Getter
	private BukkitLibraryManager bukkitLibraryManager;

	@Override
	public void onEnable() {
		if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_7)) {
			getLogger().severe("Minecraft version " + MinecraftVersion.getFullVersion() + " is not supported!");
			getLogger().severe("Use at least Minecraft version " + MinecraftVersion.V.v1_7);
			getServer().getPluginManager().disablePlugin(this);
			return;
		}

		instance = this;

		bukkitLibraryManager = new BukkitLibraryManager(this);
		LibrarySetup.load();

		Settings.load();

		particlesFolder = new File(getDataFolder(), "particles");
		if (!particlesFolder.exists()) {
			particlesFolder.mkdirs();
		}

		scriptFolder = new File(particlesFolder, "script");
		if (!scriptFolder.exists()) {
			scriptFolder.mkdirs();
		}

		Util.copyResourcesFromJar("particles", particlesFolder);

		ParticleEffectsData.load();

		if (Settings.CHECK_FOR_UPDATES) {
			checkForUpdates();
		}

		getCommand("tpbow").setExecutor(new TpBowCommand());
		getCommand("testparticle").setExecutor(new TestCommand());

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
