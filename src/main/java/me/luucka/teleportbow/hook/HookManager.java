package me.luucka.teleportbow.hook;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HookManager {

	private static WorldEditHook worldeditHook;
	private static WorldGuardHook worldguardHook;

	public static void loadDependencies() {
		if (isPluginInstalled("WorldEdit") || isPluginInstalled("FastAsyncWorldEdit")) {
			worldeditHook = new WorldEditHook();
		}

		if (isPluginInstalled("WorldGuard")) {
			worldguardHook = new WorldGuardHook(worldeditHook);
		}
	}

	/**
	 * Return a list of regions at the given location, or an empty list if
	 * there are none.
	 *
	 * @param loc the location to check.
	 */
	public static List<String> getRegions(final Location loc) {
		return worldguardHook != null ? worldguardHook.getRegionsAt(loc) : new ArrayList<>();
	}

	private static boolean isPluginInstalled(final String name) {
		Plugin lookup = null;

		for (final Plugin otherPlugin : Bukkit.getPluginManager().getPlugins())
			if (otherPlugin.getDescription().getName().equals(name)) {
				lookup = otherPlugin;

				break;
			}

		final Plugin found = lookup;

		if (found == null)
			return false;

		// Warn if the plugin is still disabled after server has finished loading.
		if (!found.isEnabled())
			Bukkit.getScheduler().runTaskAsynchronously(TeleportBow.getInstance(), () -> {
				if (!found.isEnabled())
					TeleportBow.getInstance().getLogger().warning(TeleportBow.getInstance().getName() + " could not hook into " + name + " as the plugin is disabled! (DO NOT REPORT THIS TO "
							+ TeleportBow.getInstance().getName() + ", look for errors above and contact support of '" + name + "')");
			});

		return true;
	}

}

class WorldEditHook {

	public final boolean legacy;

	public WorldEditHook() {
		boolean ok = false;
		try {
			Class.forName("com.sk89q.worldedit.world.World");
			ok = true;
		} catch (final ClassNotFoundException e) {
		}

		this.legacy = !ok;
	}
}

class WorldGuardHook {

	private final boolean legacy;

	// Reflection resolved on first use, the regions are checked on every shot
	private Method legacyGetRegionManager;
	private Method legacyGetApplicableRegions;
	private Object regionContainer;
	private Method regionContainerGet;
	private Constructor<?> bukkitWorldConstructor;

	public WorldGuardHook(final WorldEditHook we) {
		final Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");

		this.legacy = !wg.getDescription().getVersion().startsWith("7") || we != null && we.legacy;
	}

	public List<String> getRegionsAt(final Location location) {
		final List<String> list = new ArrayList<>();

		this.getApplicableRegions(location).forEach(region -> {

			if (!region.getId().startsWith("__"))
				list.add(region.getId());
		});

		return list;
	}

	@SuppressWarnings("unchecked")
	private Iterable<ProtectedRegion> getApplicableRegions(final Location loc) {
		final Object rm = this.getRegionManager(loc.getWorld());
		// null when regions are disabled in this world
		if (rm == null) return Collections.emptyList();

		if (this.legacy)
			try {
				if (legacyGetApplicableRegions == null) {
					legacyGetApplicableRegions = rm.getClass().getMethod("getApplicableRegions", Location.class);
				}
				return (Iterable<ProtectedRegion>) legacyGetApplicableRegions.invoke(rm, loc);

			} catch (final Throwable t) {
				t.printStackTrace();

				throw new RuntimeException("Failed WorldEdit 6 legacy hook, see above and report");
			}

		return ((com.sk89q.worldguard.protection.managers.RegionManager) rm)
				.getApplicableRegions(com.sk89q.worldedit.math.BlockVector3.at(loc.getX(), loc.getY(), loc.getZ()));
	}

	private Object getRegionManager(final World w) {
		if (this.legacy)
			try {
				if (legacyGetRegionManager == null) {
					legacyGetRegionManager = Class.forName("com.sk89q.worldguard.bukkit.WGBukkit").getMethod("getRegionManager", World.class);
				}
				return legacyGetRegionManager.invoke(null, w);

			} catch (final Throwable t) {
				t.printStackTrace();

				throw new RuntimeException("Failed WorldGuard 6 legacy hook, see above and report");
			}

		// Dynamically load modern WorldEdit, referencing its classes directly causes class errors
		try {
			if (regionContainer == null) {
				bukkitWorldConstructor = Class.forName("com.sk89q.worldedit.bukkit.BukkitWorld").getConstructor(World.class);

				Object t = Class.forName("com.sk89q.worldguard.WorldGuard").getMethod("getInstance").invoke(null);
				t = t.getClass().getMethod("getPlatform").invoke(t);
				t = t.getClass().getMethod("getRegionContainer").invoke(t);
				regionContainerGet = t.getClass().getMethod("get", Class.forName("com.sk89q.worldedit.world.World"));
				regionContainer = t;
			}
			return regionContainerGet.invoke(regionContainer, bukkitWorldConstructor.newInstance(w));

		} catch (final Throwable t) {
			t.printStackTrace();

			throw new RuntimeException("Failed WorldGuard hook, see above and report");
		}
	}
}
