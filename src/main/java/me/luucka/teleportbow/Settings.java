package me.luucka.teleportbow;

import java.util.Collections;
import java.util.List;

public final class Settings {

	private Settings() {
	}

	public static String BOW_NAME = "&b&lTeleport&3&lBow";

	public static List<String> BOW_LORE = Collections.singletonList("&7Shoot an arrow to teleport");

	public static int BOW_SLOT = 0;

	public static int ARROW_SLOT = 9;

	public static boolean GIVE_ON_JOIN = true;

	public static boolean CAN_BE_MOVED_IN_INVENTORY = true;

	public static boolean CAN_BE_DROPPED = true;

	public static boolean CAN_BE_SWAPPED = true;

	public static String PREFIX = "&7[&b&lTeleport&3&lBow&7] ";

	public static String RELOAD = "&2Plugin reload!";

	public static String NO_CONSOLE = "&cYou cannot run this command!";

	public static String NO_PERM = "&cYou do not have the permission!";

	public static String USAGE = "&aUsage:";

	public static String PLAYER_NOT_FOUND = "&cThe player &7{player} &cdoes not exists";

	public static boolean CHECK_FOR_UPDATES = true;

	public static void init() {
		TeleportBow.getInstance().saveDefaultConfig();
		TeleportBow.getInstance().reloadConfig();
		BOW_NAME = TeleportBow.getInstance().getConfig().getString("bow.name");
		BOW_LORE = TeleportBow.getInstance().getConfig().getStringList("bow.lore");
		BOW_SLOT = TeleportBow.getInstance().getConfig().getInt("bow.slot");
		ARROW_SLOT = TeleportBow.getInstance().getConfig().getInt("bow.arrow-slot");
		GIVE_ON_JOIN = TeleportBow.getInstance().getConfig().getBoolean("bow.give-on-join");
		CAN_BE_MOVED_IN_INVENTORY = TeleportBow.getInstance().getConfig().getBoolean("bow.can-be-moved-in-inventory");
		CAN_BE_DROPPED = TeleportBow.getInstance().getConfig().getBoolean("bow.can-be-dropped");
		CAN_BE_SWAPPED = TeleportBow.getInstance().getConfig().getBoolean("bow.can-be-swapped");

		PREFIX = _getPrefix();
		RELOAD = PREFIX + TeleportBow.getInstance().getConfig().getString("message.reload");
		NO_CONSOLE = PREFIX + TeleportBow.getInstance().getConfig().getString("message.no-console");
		NO_PERM = PREFIX + TeleportBow.getInstance().getConfig().getString("message.no-perm");
		USAGE = PREFIX + TeleportBow.getInstance().getConfig().getString("message.usage");
		PLAYER_NOT_FOUND = PREFIX + TeleportBow.getInstance().getConfig().getString("message.player-not-found");

		CHECK_FOR_UPDATES = TeleportBow.getInstance().getConfig().getBoolean("check-for-updates");
	}

	private static String _getPrefix() {
		String prefix = TeleportBow.getInstance().getConfig().getString("message.prefix");
		if (prefix == null) return "";
		return prefix.isEmpty() ? "" : prefix + " ";
	}

}
