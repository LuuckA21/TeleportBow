package me.luucka.teleportbow.util;

import lombok.Getter;
import org.bukkit.Bukkit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the current Minecraft version the plugin is loaded on.
 * <p>
 * Comparisons are performed using only major.minor:
 * - 1.20 == 1.20.6
 * - 26.1 == 26.1.1
 * <p>
 * Patch is still available through getCurrentPatch() if needed.
 * <p>
 * Must be initialized explicitly via {@link #init()} before any other method is called,
 * typically from your plugin's onEnable().
 */
public final class MinecraftVersion {

	private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+\\.\\d+(?:\\.\\d+)?)");

	@Getter
	private static Version current;

	private MinecraftVersion() {
	}

	/**
	 * Initializes the version by reading from Bukkit.
	 * Must be called once from onEnable() before any comparison method is used.
	 *
	 * @throws IllegalStateException if already initialized
	 * @throws RuntimeException      if the Bukkit version string cannot be parsed
	 */
	public static void init() {
		if (current != null) {
			throw new IllegalStateException("MinecraftVersion has already been initialized");
		}

		final String bukkitVersion = Bukkit.getBukkitVersion();
		final Matcher matcher = VERSION_PATTERN.matcher(bukkitVersion);

		if (!matcher.find()) {
			throw new RuntimeException("No numeric version prefix found in '" + bukkitVersion + "'");
		}

		final String[] parts = matcher.group(1).split("\\.");

		if (parts.length < 2 || parts.length > 3) {
			throw new RuntimeException(
					"Cannot read Bukkit version '" + bukkitVersion + "', expected <major>.<minor>[.<patch>]");
		}

		try {
			final int major = Integer.parseInt(parts[0]);
			final int minor = Integer.parseInt(parts[1]);
			final int patch = parts.length == 3 ? Integer.parseInt(parts[2]) : 0;

			if (major < 0 || minor < 0 || patch < 0) {
				throw new RuntimeException(
						"Negative version component in '" + bukkitVersion + "'");
			}

			current = new Version(major, minor, patch);

		} catch (NumberFormatException ex) {
			throw new RuntimeException("Cannot parse Bukkit version '" + bukkitVersion + "'", ex);
		}
	}

	/**
	 * Returns the patch component of the current version.
	 * Examples: 6 in 1.20.6 | 1 in 26.1.1 | 0 if absent (e.g. 1.21 or 26.1)
	 */
	public static int getCurrentPatch() {
		return current.getPatch();
	}

	/**
	 * Known base versions you may want to compare against.
	 * <p>
	 * Legacy format : 1.8, 1.20, 1.21...
	 * New format    : 26.1, 26.2... (Minecraft unified versioning)
	 */
	@Getter
	public enum V {
		v1_3_AND_BELOW(1, 3),
		v1_4(1, 4),
		v1_5(1, 5),
		v1_6(1, 6),
		v1_7(1, 7),
		v1_8(1, 8),
		v1_9(1, 9),
		v1_10(1, 10),
		v1_11(1, 11),
		v1_12(1, 12),
		v1_13(1, 13),
		v1_14(1, 14),
		v1_15(1, 15),
		v1_16(1, 16),
		v1_17(1, 17),
		v1_18(1, 18),
		v1_19(1, 19),
		v1_20(1, 20),
		v1_21(1, 21),

		v26_1(26, 1),
		v26_2(26, 2),
		v26_3(26, 3),
		v26_4(26, 4);

		private final int major;
		private final int minor;

		V(int major, int minor) {
			this.major = major;
			this.minor = minor;
		}

		Version toVersion() {
			return new Version(major, minor, 0);
		}

		@Override
		public String toString() {
			return major + "." + minor;
		}
	}

	// --- Comparison methods (enum) ---

	public static boolean equals(V version) {
		return compareBase(current, version.toVersion()) == 0;
	}

	public static boolean olderThan(V version) {
		return compareBase(current, version.toVersion()) < 0;
	}

	public static boolean newerThan(V version) {
		return compareBase(current, version.toVersion()) > 0;
	}

	public static boolean atLeast(V version) {
		return compareBase(current, version.toVersion()) >= 0;
	}

	/**
	 * Returns the full version string, e.g.: 1.20.6 | 26.1 | 26.1.1
	 */
	public static String getFullVersion() {
		return current.toString();
	}

	/**
	 * Compares only major.minor, ignoring patch.
	 */
	private static int compareBase(Version a, Version b) {
		if (a.getMajor() != b.getMajor()) {
			return Integer.compare(a.getMajor(), b.getMajor());
		}
		return Integer.compare(a.getMinor(), b.getMinor());
	}

	/**
	 * Immutable parsed version with full major.minor.patch support.
	 */
	@Getter
	public static final class Version implements Comparable<Version> {
		private final int major;
		private final int minor;
		private final int patch;

		public Version(int major, int minor, int patch) {
			this.major = major;
			this.minor = minor;
			this.patch = patch;
		}

		/**
		 * Full comparison including patch.
		 */
		@Override
		public int compareTo(Version other) {
			if (major != other.major)
				return Integer.compare(major, other.major);

			if (minor != other.minor)
				return Integer.compare(minor, other.minor);

			return Integer.compare(patch, other.patch);
		}

		@Override
		public String toString() {
			return patch > 0 ? major + "." + minor + "." + patch : major + "." + minor;
		}
	}
}