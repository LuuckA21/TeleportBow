package me.luucka.teleportbow.util;

import org.bukkit.Bukkit;

public final class ServerVersion {

	public static final int MAJOR;

	public static final int MINOR;

	private ServerVersion() {
	}

	static {
		final String[] version = Bukkit.getBukkitVersion().substring(0, Bukkit.getBukkitVersion().indexOf("-")).split("\\.");
		MAJOR = Integer.parseInt(version[0]);
		MINOR = Integer.parseInt(version[1]);
	}
}
