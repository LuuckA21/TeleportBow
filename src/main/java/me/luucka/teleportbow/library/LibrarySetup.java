package me.luucka.teleportbow.library;

import com.alessiodp.libby.Library;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.TeleportBow;
import me.luucka.teleportbow.util.Util;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LibrarySetup {

	public static void load() {
		TeleportBow.getInstance().getBukkitLibraryManager().addMavenCentral();
		TeleportBow.getInstance().getBukkitLibraryManager().addRepository("https://repo.codemc.io/repository/maven-public/");

		for (Libraries lib : Libraries.values()) {
			if (!(Util.getJavaVersion() >= lib.getFromJavaVersion())) continue;
			TeleportBow.getInstance().getBukkitLibraryManager().loadLibrary(
					Library.builder()
							.groupId(lib.getGroupId())
							.artifactId(lib.getArtifactId())
							.version(lib.getVersion())
							.resolveTransitiveDependencies(true)
							.relocate(lib.getOldRelocation(), lib.getNewRelocation())
							.build()
			);
		}
	}
}
