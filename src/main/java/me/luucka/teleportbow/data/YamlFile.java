package me.luucka.teleportbow.data;

import me.luucka.teleportbow.TeleportBow;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class YamlFile {

	private final File file;
	protected YamlConfiguration config;

	public YamlFile(final File file) {
		this.file = file;
		config = YamlConfiguration.loadConfiguration(file);
	}

	public YamlFile(final String resourcePath) {
		this(new File(TeleportBow.getInstance().getDataFolder(), resourcePath));
		if (!file.exists()) {
			TeleportBow.getInstance().saveResource(resourcePath, false);
		}
	}

	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			e.printStackTrace();
			TeleportBow.getInstance().getLogger().severe("Error saving " + file.getName());
		}
	}

	public void reload() {
		config = YamlConfiguration.loadConfiguration(file);
	}
}


