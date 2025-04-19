package me.luucka.teleportbow.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.TeleportBow;

import java.io.File;
import java.net.URL;
import java.security.CodeSource;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Util {

	public static void copyResourcesFromJar(String resourceFolderPath, File destination) {
		try {
			final CodeSource codeSource = TeleportBow.getInstance().getClass().getProtectionDomain().getCodeSource();
			if (codeSource == null) return;

			final URL jarUrl = codeSource.getLocation();
			try (final JarFile jar = new JarFile(new File(jarUrl.toURI()))) {
				final Enumeration<JarEntry> entries = jar.entries();

				while (entries.hasMoreElements()) {
					final JarEntry entry = entries.nextElement();
					final String name = entry.getName();

					if (name.startsWith(resourceFolderPath + "/") && !entry.isDirectory()) {
						final String relativePath = name.substring(resourceFolderPath.length() + 1);
						File outFile = new File(destination, relativePath);

						if (!outFile.getParentFile().exists()) {
							outFile.getParentFile().mkdirs();
						}

						if (!outFile.exists()) {
							TeleportBow.getInstance().saveResource(name, false);
						}
					}
				}
			}
		} catch (Exception e) {
			TeleportBow.getInstance().getLogger().severe("Failed to copy resources from " + resourceFolderPath + ": " + e.getMessage());
			e.printStackTrace();
		}
	}


}
