package me.luucka.teleportbow;

import me.luucka.teleportbow.util.MinecraftVersion;
import org.bukkit.Material;

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

		TeleportBow.getInstance().reloadConfig();

		try {
			final String bowTypeConfig = TeleportBow.getInstance().getConfig().getString("bow.type", "BOW").toUpperCase();
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

		BOW_NAME = TeleportBow.getInstance().getConfig().getString("bow.name");
		BOW_LORE = TeleportBow.getInstance().getConfig().getStringList("bow.lore");
		BOW_SLOT = TeleportBow.getInstance().getConfig().getInt("bow.slot");
		ARROW_SLOT = TeleportBow.getInstance().getConfig().getInt("bow.arrow-slot");
		GIVE_ON_JOIN = TeleportBow.getInstance().getConfig().getBoolean("bow.give-on-join");
		CAN_BE_MOVED_IN_INVENTORY = TeleportBow.getInstance().getConfig().getBoolean("bow.can-be-moved-in-inventory");
		CAN_BE_DROPPED = TeleportBow.getInstance().getConfig().getBoolean("bow.can-be-dropped");
		CAN_BE_SWAPPED = TeleportBow.getInstance().getConfig().getBoolean("bow.can-be-swapped");

		final Set<String> validWorldsListType = new HashSet<>(Arrays.asList("none", "whitelist", "blacklist"));
		final String worldsListType = TeleportBow.getInstance().getConfig().getString("worlds.list-type", "none");
		WORLDS_LIST_TYPE = validWorldsListType.contains(worldsListType.toLowerCase()) ? worldsListType : "none";
		WORLDS_LIST = TeleportBow.getInstance().getConfig().getStringList("worlds.list");

		PREFIX = _getPrefix();
		RELOAD = PREFIX + TeleportBow.getInstance().getConfig().getString("message.reload");
		NO_CONSOLE = PREFIX + TeleportBow.getInstance().getConfig().getString("message.no-console");
		NO_PERM = PREFIX + TeleportBow.getInstance().getConfig().getString("message.no-perm");
		USAGE = PREFIX + TeleportBow.getInstance().getConfig().getString("message.usage");
		PLAYER_NOT_FOUND = PREFIX + TeleportBow.getInstance().getConfig().getString("message.player-not-found");
		WORLD_NOT_ALLOWED = PREFIX + TeleportBow.getInstance().getConfig().getString("message.world-not-allowed");

		CHECK_FOR_UPDATES = TeleportBow.getInstance().getConfig().getBoolean("check-for-updates");
	}

	private static String _getPrefix() {
		String prefix = TeleportBow.getInstance().getConfig().getString("message.prefix");
		return (prefix == null || prefix.isEmpty()) ? "" : prefix + " ";
	}

	private static void newFieldsFromV171ToV180() {
		if (!TeleportBow.getInstance().getConfig().isSet("bow.type")) {
			TeleportBow.getInstance().getConfig().set("bow.type", "BOW");
			TeleportBow.getInstance().saveConfig();
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing 'bow.type' with default value 'BOW'.");
		}
	}

	private static void newFieldsFromV184ToV190() {
		if (!TeleportBow.getInstance().getConfig().isSet("worlds.list-type")) {
			TeleportBow.getInstance().getConfig().set("worlds.list-type", "none");
			TeleportBow.getInstance().saveConfig();
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing 'worlds.list-type' with default value 'none'.");
		}

		if (!TeleportBow.getInstance().getConfig().isSet("worlds.list")) {
			TeleportBow.getInstance().getConfig().set("worlds.list", Arrays.asList("world", "world_nether"));
			TeleportBow.getInstance().saveConfig();
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing 'worlds.list' with default value 'world, world_nether'.");
		}

		if (!TeleportBow.getInstance().getConfig().isSet("message.world-not-allowed")) {
			TeleportBow.getInstance().getConfig().set("message.world-not-allowed", "&cThe Bow in this world is not allowed");
			TeleportBow.getInstance().saveConfig();
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing 'message.world-not-allowed' with default value 'The Bow in this world is not allowed'.");
		}
	}

}
