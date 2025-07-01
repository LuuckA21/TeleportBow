package me.luucka.teleportbow.library;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.TeleportBow;
import me.luucka.teleportbow.util.Util;
import net.byteflux.libby.Library;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LibrarySetup {

	public static void load() {
		TeleportBow.getInstance().getBukkitLibraryManager().addMavenCentral();
		TeleportBow.getInstance().getBukkitLibraryManager().addRepository("https://repo.codemc.io/repository/maven-public/");

		TeleportBow.getInstance().getLogger().info("Java version: " + Util.getJavaVersion());
		for (Libraries lib : Libraries.values()) {
			if (!(Util.getJavaVersion() >= lib.getFromJavaVersion())) continue;
			TeleportBow.getInstance().getLogger().info("Loading: " + lib.getGroupId() + ":" + lib.getArtifactId() + ":" + lib.getVersion() + " (from Java " + lib.getFromJavaVersion() + ")");
			TeleportBow.getInstance().getBukkitLibraryManager().loadLibrary(
					Library.builder()
							.groupId(lib.getGroupId())
							.artifactId(lib.getArtifactId())
							.version(lib.getVersion())
							.relocate(lib.getOldRelocation(), lib.getNewRelocation())
							.build()
			);
		}
	}
}
