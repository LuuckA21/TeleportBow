package me.luucka.teleportbow.setting;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.luucka.pillars.PillarPlugin;
import me.luucka.pillars.config.YamlFile;

import java.io.File;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SettingsV2 {

	@Getter
	private static final SettingsV2 instance = new SettingsV2();

	@Getter
	private YamlFile settings;

	public static void load() {
		instance.settings = new YamlFile(
				new File(PillarPlugin.getInstance().getDataFolder(), "settings.yml"),
				"/settings.yml",
				PillarPlugin.getInstance().getClass()
		);
		reload();
	}

	public static void reload() {
		instance.settings.load();
	}

}
