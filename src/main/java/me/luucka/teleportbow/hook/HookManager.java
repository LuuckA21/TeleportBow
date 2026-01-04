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
import java.util.List;
import java.util.Map;

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
	 * Is WorldEdit loaded?
	 *
	 * @return
	 */
	public static boolean isWorldEditLoaded() {
		return worldeditHook != null || isFAWELoaded();
	}

	/**
	 * Is FastAsyncWorldEdit loaded?
	 *
	 * @return
	 */
	public static boolean isFAWELoaded() {

		// Check for FastAsyncWorldEdit directly.
		final Plugin fawe = Bukkit.getPluginManager().getPlugin("FastAsyncWorldEdit");

		if (fawe != null && fawe.isEnabled())
			return true;

		// Check for legacy FastAsyncWorldEdit installations.
		final Plugin worldEdit = Bukkit.getPluginManager().getPlugin("WorldEdit");

		return worldEdit != null && worldEdit.isEnabled() && "Fast Async WorldEdit plugin".equals(worldEdit.getPluginMeta().getDescription());
	}

	/**
	 * Is WorldGuard loaded?
	 *
	 * @return
	 */
	public static boolean isWorldGuardLoaded() {
		return worldguardHook != null;
	}

	// ------------------------------------------------------------------------------------------------------------
	// WorldGuard
	// ------------------------------------------------------------------------------------------------------------

	/**
	 * Return a list of regions at the given location, or an empty list if
	 * there are none.
	 *
	 * @param loc the location to check.
	 * @return
	 */
	public static List<String> getRegions(final Location loc) {
		return isWorldGuardLoaded() ? worldguardHook.getRegionsAt(loc) : new ArrayList<>();
	}

	/**
	 * Return a list of loaded regions, or an empty list if there are none.
	 *
	 * @return
	 */
	public static List<String> getRegions() {
		return isWorldGuardLoaded() ? worldguardHook.getAllRegions() : new ArrayList<>();
	}

	// ------------------------------------------------------------------------------------------------------------
	// Utility
	// ------------------------------------------------------------------------------------------------------------

	private static boolean isPluginInstalled(final String name) {
		Plugin lookup = null;

		for (final Plugin otherPlugin : Bukkit.getPluginManager().getPlugins())
			if (otherPlugin.getPluginMeta().getName().equals(name)) {
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
		} catch (final ClassNotFoundException ignored) {
		}

		this.legacy = !ok;
	}
}

class WorldGuardHook {

	private final boolean legacy;

	public WorldGuardHook(final WorldEditHook we) {
		final Plugin wg = Bukkit.getPluginManager().getPlugin("WorldGuard");

		this.legacy = !wg.getPluginMeta().getVersion().startsWith("7") || we != null && we.legacy;
	}

	public List<String> getRegionsAt(final Location location) {
		final List<String> list = new ArrayList<>();

		this.getApplicableRegions(location).forEach(region -> {

			if (!region.getId().startsWith("__"))
				list.add(region.getId());
		});

		return list;
	}

	public List<String> getAllRegions() {
		final List<String> list = new ArrayList<>();

		for (final World w : Bukkit.getWorlds()) {
			final Object rm = this.getRegionManager(w);
			if (this.legacy)
				try {
					final Map<?, ?> regionMap = (Map<?, ?>) rm.getClass().getMethod("getRegions").invoke(rm);
					Method getId = null;
					for (final Object regObj : regionMap.values()) {
						if (regObj == null)
							continue;
						if (getId == null)
							getId = regObj.getClass().getMethod("getId");

//						final String name = CompChatColor.stripColorCodes(getId.invoke(regObj).toString());
						final String name = getId.invoke(regObj).toString();

						if (!name.startsWith("__"))
							list.add(name);
					}
				} catch (final Throwable t) {
					t.printStackTrace();

					throw new RuntimeException("Failed WorldEdit 6 legacy hook, see above and report");
				}
			else
				((com.sk89q.worldguard.protection.managers.RegionManager) rm)
						.getRegions().values().forEach(reg -> {
							if (reg == null || reg.getId() == null)
								return;

//							final String name = CompChatColor.stripColorCodes(reg.getId());
							final String name = reg.getId();

							if (!name.startsWith("__"))
								list.add(name);
						});
		}

		return list;
	}

	private Iterable<ProtectedRegion> getApplicableRegions(final Location loc) {
		final Object rm = this.getRegionManager(loc.getWorld());

		if (this.legacy)
			try {
				return (Iterable<ProtectedRegion>) rm.getClass().getMethod("getApplicableRegions", Location.class).invoke(rm, loc);

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
				return Class.forName("com.sk89q.worldguard.bukkit.WGBukkit").getMethod("getRegionManager", World.class).invoke(null, w);

			} catch (final Throwable t) {
				t.printStackTrace();

				throw new RuntimeException("Failed WorldGuard 6 legacy hook, see above and report");
			}

		// Causes class errors.
		//return com.sk89q.worldguard.WorldGuard.getInstance().getPlatform().getRegionContainer().get(new com.sk89q.worldedit.bukkit.BukkitWorld(w));
		// Dynamically load modern WorldEdit.
		try {

			final Class<?> bwClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitWorld");
			final Constructor<?> bwClassNew = bwClass.getConstructor(World.class);

			Object t = Class.forName("com.sk89q.worldguard.WorldGuard").getMethod("getInstance").invoke(null);
			t = t.getClass().getMethod("getPlatform").invoke(t);
			t = t.getClass().getMethod("getRegionContainer").invoke(t);
			return t.getClass().getMethod("get", Class.forName("com.sk89q.worldedit.world.World")).invoke(t, bwClassNew.newInstance(w));

		} catch (final Throwable t) {
			t.printStackTrace();

			throw new RuntimeException("Failed WorldGuard hook, see above and report");
		}
	}
}
