package me.luucka.teleportbow;

import me.luucka.teleportbow.util.MinecraftVersion;
import org.bukkit.Material;
import org.bukkit.Sound;

import java.util.Collections;
import java.util.List;

public final class Settings {

	private Settings() {
	}

	//------------------------------------------------------------------------------------------------------------------
	// BOW
	//------------------------------------------------------------------------------------------------------------------

	public static Material BOW_TYPE = Material.BOW;

	public static String BOW_NAME = "&b&lTeleport&3&lBow";

	public static List<String> BOW_LORE = Collections.singletonList("&7Shoot an arrow to teleport");

	public static int BOW_SLOT = 0;

	public static int ARROW_SLOT = 9;

	public static boolean GIVE_ON_JOIN = true;

	public static boolean CAN_BE_MOVED_IN_INVENTORY = true;

	public static boolean CAN_BE_DROPPED = true;

	public static boolean CAN_BE_SWAPPED = true;

	//------------------------------------------------------------------------------------------------------------------
	// TELEPORT
	//------------------------------------------------------------------------------------------------------------------

	public static Sound TELEPORT_SOUND_TYPE = Sound.ENTITY_PLAYER_TELEPORT;

	public static float TELEPORT_SOUND_VOLUME = 1.0F;

	public static float TELEPORT_SOUND_PITCH = 1.0F;

	public static boolean TELEPORT_SOUND_CHECK_PERM = false;

	//------------------------------------------------------------------------------------------------------------------
	// MESSAGE
	//------------------------------------------------------------------------------------------------------------------

	public static String PREFIX = "&7[&b&lTeleport&3&lBow&7] ";

	public static String RELOAD = "&2Plugin reload!";

	public static String NO_CONSOLE = "&cYou cannot run this command!";

	public static String NO_PERM = "&cYou do not have the permission!";

	public static String USAGE = "&aUsage:";

	public static String PLAYER_NOT_FOUND = "&cThe player &7{player} &cdoes not exists";

	public static boolean CHECK_FOR_UPDATES = true;

	public static void load() {
		TeleportBow.getInstance().saveDefaultConfig();

		newFieldsFromV171ToV180();
		newFieldsFromV181ToV190();

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
			TeleportBow.getInstance().getLogger().warning("Invalid Material name. Use BOW as default.");
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

		try {
			final String teleportSoundTypeConfig = TeleportBow.getInstance().getConfig().getString("teleport.sound.type", "ENTITY_PLAYER_TELEPORT").toUpperCase();
			TELEPORT_SOUND_TYPE = Sound.valueOf(teleportSoundTypeConfig);
		} catch (IllegalArgumentException e) {
			TeleportBow.getInstance().getLogger().warning("Invalid Sound name. Use ENTITY_PLAYER_TELEPORT as default.");
			TELEPORT_SOUND_TYPE = Sound.ENTITY_PLAYER_TELEPORT;
		}
		TELEPORT_SOUND_VOLUME = (float) TeleportBow.getInstance().getConfig().getDouble("teleport.sound.volume", 1.0D);
		TELEPORT_SOUND_PITCH = (float) TeleportBow.getInstance().getConfig().getDouble("teleport.sound.pitch", 1.0D);
		TELEPORT_SOUND_CHECK_PERM = TeleportBow.getInstance().getConfig().getBoolean("teleport.sound.check-perm");

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
		return (prefix == null || prefix.isEmpty()) ? "" : prefix + " ";
	}

	private static void newFieldsFromV171ToV180() {
		if (!TeleportBow.getInstance().getConfig().isSet("bow.type")) {
			TeleportBow.getInstance().getConfig().set("bow.type", "BOW");
			TeleportBow.getInstance().saveConfig();
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing 'bow.type' with default value 'BOW'.");
		}
	}

	private static void newFieldsFromV181ToV190() {
		if (!TeleportBow.getInstance().getConfig().isSet("teleport.sound.type")) {
			TeleportBow.getInstance().getConfig().set("teleport.sound.type", "ENTITY_PLAYER_TELEPORT");
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing 'teleport.sound.type' with default value 'ENTITY_PLAYER_TELEPORT'.");
		}

		if (!TeleportBow.getInstance().getConfig().isSet("teleport.sound.volume")) {
			TeleportBow.getInstance().getConfig().set("teleport.sound.volume", 1.0F);
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing 'teleport.sound.volume' with default value '1.0'.");
		}

		if (!TeleportBow.getInstance().getConfig().isSet("teleport.sound.pitch")) {
			TeleportBow.getInstance().getConfig().set("teleport.sound.pitch", 1.0F);
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing 'teleport.sound.pitch' with default value '1.0'.");
		}

		if (!TeleportBow.getInstance().getConfig().isSet("teleport.sound.check-perm")) {
			TeleportBow.getInstance().getConfig().set("teleport.sound.check-perm", false);
			TeleportBow.getInstance().getLogger().info("Configuration: Added missing 'teleport.sound.check-perm' with default value 'false'.");
		}

		TeleportBow.getInstance().saveConfig();
	}

}
