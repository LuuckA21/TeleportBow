package me.luucka.teleportbow.setting;

import com.cryptomorin.xseries.XSound;
import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

import static me.luucka.teleportbow.util.Color.colorize;

/**
 * Values of config.yml. The defaults are only in the config.yml of the jar: Bukkit uses it for missing keys.
 * Messages, bow name and lore are colorized once here.
 */
public final class Settings {

	private Settings() {
	}

	// BOW -------------------------------------------------------------------------------------------------------------

	public static Material BOW_TYPE;

	public static String BOW_NAME;

	public static List<String> BOW_LORE;

	public static int BOW_SLOT;

	public static int ARROW_SLOT;

	public static boolean ARROW_DAMAGE;

	public static boolean NEEDED_PERMISSION;

	public static boolean GIVE_ON_JOIN;

	public static boolean CAN_BE_MOVED_IN_INVENTORY;

	public static boolean CAN_BE_DROPPED;

	public static boolean CAN_BE_SWAPPED;

	public static boolean KEEP_ON_DEATH;

	// WORLDS ----------------------------------------------------------------------------------------------------------

	private static final Set<String> LIST_TYPES = new HashSet<>(Arrays.asList("none", "whitelist", "blacklist"));

	public static String WORLDS_LIST_TYPE;

	public static List<String> WORLDS_LIST;

	// REGIONS ---------------------------------------------------------------------------------------------------------

	public static String REGIONS_LIST_TYPE;

	public static List<String> REGIONS_LIST;

	// TELEPORT - SOUND ------------------------------------------------------------------------------------------------

	public static XSound SOUND_TYPE;

	public static float SOUND_VOLUME;

	public static float SOUND_PITCH;

	public static boolean SOUND_ENABLE;

	// MESSAGE ---------------------------------------------------------------------------------------------------------

	public static String RELOAD;

	public static String NO_CONSOLE;

	public static String NO_PERM;

	public static String USAGE;

	public static String PLAYER_NOT_FOUND;

	public static String WORLD_NOT_ALLOWED;

	public static String REGION_NOT_ALLOWED;

	public static String BOW_NOT_ALLOWED;

	// OTHER -----------------------------------------------------------------------------------------------------------

	public static boolean CHECK_FOR_UPDATES;

	public static boolean BSTATS;

	public static void load() {
		TeleportBow.getInstance().saveDefaultConfig();
		addMissingKeys();
		reload();
	}

	public static void reload() {
		final JavaPlugin plugin = TeleportBow.getInstance();
		plugin.reloadConfig();
		final FileConfiguration config = plugin.getConfig();

		try {
			final Material bowType = Material.valueOf(config.getString("bow.type").toUpperCase());
			BOW_TYPE = BowManager.isBowMaterial(bowType) ? bowType : Material.BOW;
		} catch (IllegalArgumentException e) {
			// Unknown material, e.g. CROSSBOW before 1.14
			BOW_TYPE = Material.BOW;
		}

		BOW_NAME = colorize(config.getString("bow.name"));
		BOW_LORE = colorize(config.getStringList("bow.lore"));
		BOW_SLOT = config.getInt("bow.slot");
		ARROW_SLOT = config.getInt("bow.arrow-slot");
		if (BOW_SLOT < 0 || BOW_SLOT > 35 || ARROW_SLOT < 0 || ARROW_SLOT > 35 || BOW_SLOT == ARROW_SLOT) {
			plugin.getLogger().warning("bow.slot and bow.arrow-slot must be two different slots from 0 to 35, using 0 and 9");
			BOW_SLOT = 0;
			ARROW_SLOT = 9;
		}
		ARROW_DAMAGE = config.getBoolean("bow.arrow-damage");
		NEEDED_PERMISSION = config.getBoolean("bow.needed-permission");
		GIVE_ON_JOIN = config.getBoolean("bow.give-on-join");
		CAN_BE_MOVED_IN_INVENTORY = config.getBoolean("bow.can-be-moved-in-inventory");
		CAN_BE_DROPPED = config.getBoolean("bow.can-be-dropped");
		CAN_BE_SWAPPED = config.getBoolean("bow.can-be-swapped");
		KEEP_ON_DEATH = config.getBoolean("bow.keep-on-death");

		WORLDS_LIST_TYPE = getListType(config, "worlds.list-type");
		WORLDS_LIST = new ArrayList<>(config.getStringList("worlds.list"));

		REGIONS_LIST_TYPE = getListType(config, "regions.list-type");
		REGIONS_LIST = new ArrayList<>(config.getStringList("regions.list"));

		SOUND_TYPE = XSound.of(config.getString("teleport.sound.type")).orElse(XSound.ENTITY_ENDERMAN_TELEPORT);
		SOUND_VOLUME = (float) config.getDouble("teleport.sound.volume");
		SOUND_PITCH = (float) config.getDouble("teleport.sound.pitch");
		SOUND_ENABLE = config.getBoolean("teleport.sound.enable");

		final String prefix = config.getString("message.prefix");
		final String messagePrefix = prefix == null || prefix.isEmpty() ? "" : prefix + " ";
		RELOAD = colorize(messagePrefix + config.getString("message.reload"));
		NO_CONSOLE = colorize(messagePrefix + config.getString("message.no-console"));
		NO_PERM = colorize(messagePrefix + config.getString("message.no-perm"));
		USAGE = colorize(messagePrefix + config.getString("message.usage"));
		PLAYER_NOT_FOUND = colorize(messagePrefix + config.getString("message.player-not-found"));
		WORLD_NOT_ALLOWED = colorize(messagePrefix + config.getString("message.world-not-allowed"));
		REGION_NOT_ALLOWED = colorize(messagePrefix + config.getString("message.region-not-allowed"));
		BOW_NOT_ALLOWED = colorize(messagePrefix + config.getString("message.bow-not-allowed"));

		CHECK_FOR_UPDATES = config.getBoolean("check-for-updates");

		BSTATS = config.getBoolean("bstats");
	}

	private static String getListType(final FileConfiguration config, final String path) {
		final String type = config.getString(path).toLowerCase();
		return LIST_TYPES.contains(type) ? type : "none";
	}

	/**
	 * Writes to the user's file the options added by plugin updates, saving it only if something is missing.
	 */
	private static void addMissingKeys() {
		final JavaPlugin plugin = TeleportBow.getInstance();
		final FileConfiguration config = plugin.getConfig();
		final Configuration defaults = config.getDefaults();
		if (defaults == null) return;

		// Without copyDefaults, getKeys only returns the keys of the user's file
		final Set<String> present = config.getKeys(true);
		boolean changed = false;
		for (final String path : defaults.getKeys(true)) {
			if (present.contains(path) || defaults.isConfigurationSection(path)) continue;

			config.set(path, defaults.get(path));
			plugin.getLogger().info("Configuration: Added missing '" + path + "' with default value '" + defaults.get(path) + "'.");
			changed = true;
		}

		if (changed) plugin.saveConfig();
	}

}
