package me.luucka.teleportbow;

import com.cryptomorin.xseries.XSound;
import me.luucka.teleportbow.util.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public final class Settings {

	private Settings() {
	}

	// BOW -------------------------------------------------------------------------------------------------------------

	public static Material BOW_TYPE = Material.BOW;

	public static String BOW_NAME = "&b&lTeleport&3&lBow";

	public static List<String> BOW_LORE = Collections.singletonList("&7Shoot an arrow to teleport");

	public static int BOW_SLOT = 0;

	public static int ARROW_SLOT = 9;

	public static boolean GIVE_ON_JOIN = true;

	public static boolean CAN_BE_MOVED_IN_INVENTORY = true;

	public static boolean CAN_BE_DROPPED = true;

	public static boolean CAN_BE_SWAPPED = true;

	// WORLDS ----------------------------------------------------------------------------------------------------------

	public static String WORLDS_LIST_TYPE = "none";

	public static List<String> WORLDS_LIST = Collections.emptyList();

	// TELEPORT - SOUND ------------------------------------------------------------------------------------------------

	public static XSound SOUND_TYPE = XSound.ENTITY_ENDERMAN_TELEPORT;

	public static float SOUND_VOLUME = 1.0f;

	public static float SOUND_PITCH = 1.0f;

	public static boolean SOUND_ENABLE = true;

	// MESSAGE ---------------------------------------------------------------------------------------------------------

	public static String PREFIX = "&7[&b&lTeleport&3&lBow&7] ";

	public static String RELOAD = "&2Plugin reload!";

	public static String NO_CONSOLE = "&cYou cannot run this command!";

	public static String NO_PERM = "&cYou do not have the permission!";

	public static String USAGE = "&aUsage:";

	public static String PLAYER_NOT_FOUND = "&cThe player &7{player} &cdoes not exists";

	public static String WORLD_NOT_ALLOWED = "&cThe Bow in this world is not allowed";


	// OTHER -----------------------------------------------------------------------------------------------------------

	public static boolean CHECK_FOR_UPDATES = true;

	public static void load() {
		TeleportBow.getInstance().saveDefaultConfig();
		newFieldsFromV171ToV180();
		newFieldsFromV184ToV190();
		reload();
	}

	public static void reload() {
		final JavaPlugin plugin = TeleportBow.getInstance();
		final FileConfiguration config = TeleportBow.getInstance().getConfig();
		plugin.reloadConfig();

		try {
			final String bowTypeConfig = config.getString("bow.type", "BOW").toUpperCase();
			final Material bowType = Material.valueOf(bowTypeConfig);

			// Check compatibility based on Minecraft version
			if (MinecraftVersion.olderThan(MinecraftVersion.V.v1_14)) {
				// For versions older than 1.14, only allow BOW
				BOW_TYPE = Material.BOW;
			} else {
				// For versions 1.14 and newer, allow BOW or CROSSBOW
				BOW_TYPE = (bowType == Material.BOW || bowType == Material.CROSSBOW) ? bowType : Material.BOW;
			}
		} catch (IllegalArgumentException e) {
			BOW_TYPE = Material.BOW;
		}

		BOW_NAME = config.getString("bow.name");
		BOW_LORE = config.getStringList("bow.lore");
		BOW_SLOT = config.getInt("bow.slot");
		ARROW_SLOT = config.getInt("bow.arrow-slot");
		GIVE_ON_JOIN = config.getBoolean("bow.give-on-join");
		CAN_BE_MOVED_IN_INVENTORY = config.getBoolean("bow.can-be-moved-in-inventory");
		CAN_BE_DROPPED = config.getBoolean("bow.can-be-dropped");
		CAN_BE_SWAPPED = config.getBoolean("bow.can-be-swapped");

		final Set<String> validWorldsListType = new HashSet<>(Arrays.asList("none", "whitelist", "blacklist"));
		final String worldsListType = config.getString("worlds.list-type", "none");
		WORLDS_LIST_TYPE = validWorldsListType.contains(worldsListType.toLowerCase()) ? worldsListType : "none";
		WORLDS_LIST = config.getStringList("worlds.list");

		final Optional<XSound> optionalSoundType = XSound.of(config.getString("teleport.sound.type"));
		SOUND_TYPE = optionalSoundType.orElse(XSound.ENTITY_ENDERMAN_TELEPORT);
		SOUND_VOLUME = (float) config.getDouble("teleport.sound.volume");
		SOUND_PITCH = (float) config.getDouble("teleport.sound.pitch");
		SOUND_ENABLE = config.getBoolean("teleport.sound.enable");

		PREFIX = _getPrefix();
		RELOAD = PREFIX + config.getString("message.reload");
		NO_CONSOLE = PREFIX + config.getString("message.no-console");
		NO_PERM = PREFIX + config.getString("message.no-perm");
		USAGE = PREFIX + config.getString("message.usage");
		PLAYER_NOT_FOUND = PREFIX + config.getString("message.player-not-found");
		WORLD_NOT_ALLOWED = PREFIX + config.getString("message.world-not-allowed");

		CHECK_FOR_UPDATES = config.getBoolean("check-for-updates");
	}

	private static String _getPrefix() {
		String prefix = TeleportBow.getInstance().getConfig().getString("message.prefix");
		return (prefix == null || prefix.isEmpty()) ? "" : prefix + " ";
	}

	private static void newFieldsFromV171ToV180() {
		final JavaPlugin plugin = TeleportBow.getInstance();
		final FileConfiguration config = TeleportBow.getInstance().getConfig();

		setIfMissing(config, "bow.type", "BOW");

		plugin.saveConfig();
	}

	private static void newFieldsFromV184ToV190() {
		final JavaPlugin plugin = TeleportBow.getInstance();
		final FileConfiguration config = TeleportBow.getInstance().getConfig();

		setIfMissing(config, "worlds.list-type", "none");
		setIfMissing(config, "worlds.list", Arrays.asList("world", "world_nether"));

		setIfMissing(config, "teleport.sound.type", "ENTITY_ENDERMAN_TELEPORT");
		setIfMissing(config, "teleport.sound.volume", 1.0);
		setIfMissing(config, "teleport.sound.pitch", 1.0);
		setIfMissing(config, "teleport.sound.enable", true);

		setIfMissing(config, "message.world-not-allowed", "&cThe Bow in this world is not allowed");

		plugin.saveConfig();
	}

	private static void setIfMissing(FileConfiguration config, String path, Object defaultValue) {
		if (!config.isSet(path)) {
			config.set(path, defaultValue);
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing '" + path + "' with default value '" + defaultValue + "'.");
		}
	}


}
