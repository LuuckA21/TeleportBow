package me.luucka.teleportbow;

import java.util.List;

public class Settings {

	public static String BOW_NAME;

	public static List<String> BOW_LORE;

	public static int BOW_SLOT;

	public static int ARROW_SLOT;

	public static boolean GIVE_ON_JOIN;

	public static boolean CAN_BE_MOVED_IN_INVENTORY;

	public static boolean CAN_BE_DROPPED;

	public static boolean CAN_BE_SWAPPED;

	public static String PREFIX;

	public static String RELOAD;

	public static String NO_CONSOLE;

	public static String NO_PERM;

	public static String USAGE;

	public static String PLAYER_NOT_FOUND;

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
	}

	private static String _getPrefix() {
		String prefix = TeleportBow.getInstance().getConfig().getString("message.prefix");
		if (prefix == null) return "";
		return prefix.isEmpty() ? "" : prefix + " ";
	}

}
