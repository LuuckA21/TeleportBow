package me.luucka.teleportbow.data;

import com.cryptomorin.xseries.particles.XParticle;
import lombok.Getter;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Particle;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;

public final class ParticleEffectsData extends YamlFile {

	private static final Map<String, ParticleEffectsData> particleEffects = new HashMap<>();

	@Getter
	private final String name;

	@Getter
	private final Particle particle;

	@Getter
	private Mode mode;

	@Getter
	private String script;

	public ParticleEffectsData(final File file) {
		super(file);

		name = config.getString("name");

		final Optional<XParticle> xParticle = XParticle.of(config.getString("particle"));
		particle = xParticle.isPresent() ? xParticle.get().get() : Particle.FLAME;

		try {
			mode = Mode.valueOf(config.getString("mode").toUpperCase());
		} catch (IllegalArgumentException e) {
			mode = Mode.SIMPLE;
		}

		final File scriptFile = new File(TeleportBow.getInstance().getScriptFolder(), config.getString("script-file"));
		try {
			final List<String> lines = Files.readAllLines(scriptFile.toPath());
			script = String.join("\n", lines);
		} catch (IOException e) {
			script = "";
		}

		TeleportBow.getInstance().getLogger().log(Level.INFO, "ParticleEffectData " + name + " loaded!");
	}

	public static void load() {
		particleEffects.clear();
		final File[] listOfFiles = TeleportBow.getInstance().getParticlesFolder().listFiles();
		if (listOfFiles.length >= 1) {
			for (final File file : listOfFiles) {
				String fileName = file.getName();
				if (file.isFile() && fileName.endsWith(".yml")) {
					try {
						particleEffects.put(fileName.substring(0, fileName.length() - 4), new ParticleEffectsData(file));
					} catch (final Exception ex) {
						TeleportBow.getInstance().getLogger().log(Level.WARNING, "ParticleEffectData file " + fileName + " loading error!");
					}
				}
			}
		}
	}

	public enum Mode {
		SIMPLE,
		SCRIPT
	}

	public static Optional<ParticleEffectsData> get(final String effectName) {
		return Optional.ofNullable(particleEffects.get(effectName));
	}

	@Override
	public String toString() {
		return "Name:" + name +
				" Particle:" + particle.name() +
				" Mode:" + mode.name();
//				" Script:" + script;
	}
}
