package me.luucka.teleportbow.library;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum Libraries {
	NASHORN(
			"org.openjdk.nashorn",
			"nashorn-core",
			"15.6",
			"nashorn-core",
			"",//"org.openjdk.nashorn",
			"",//"me.luucka.teleportbow.lib.nashorn",
			15
	),
	XSERIES(
			"com.github.cryptomorin",
			"XSeries",
			"13.2.0",
			"XSeries",
			"com.github.cryptomorin",
			"me.luucka.teleportbow.lib.xseries",
			0
	);
	//	ASM(
//			"org.ow2.asm",
//			"asm-util",
//			"9.7.1",
//			"asm-util",
//			"org.ow2.asm",
//			"me.luucka.teleportbow.lib.asm",
//			0
//	);

	private final String groupId;
	private final String artifactId;
	private final String version;
	private final String id;
	private final String oldRelocation;
	private final String newRelocation;
	private final int fromJavaVersion;
}

