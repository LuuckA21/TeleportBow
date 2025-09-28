package me.luucka.teleportbow.impl;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * RegionChecker per WorldGuard 6 usando reflection.
 * Non richiede dipendenze WG6 in compilazione.
 */
public class WG6RegionService implements IWorldGuardRegionService {

	private final JavaPlugin plugin;

	// Reflection cache
	private Class<?> clsWGBukkit;
	private Method mGetRegionManager;      // RegionManager getRegionManager(World)
	private Class<?> clsRegionManager;
	private Method mGetApplicableRegions;  // ApplicableRegionSet getApplicableRegions(Location)
	private Class<?> clsApplicableRegionSet;
	private Method mGetRegions;            // Set<ProtectedRegion> getRegions()
	private Class<?> clsProtectedRegion;
	private Method mGetId;                 // String getId()

	private boolean initialized;
	private boolean available; // true se WG6 è presente e metodi trovati

	public WG6RegionService(JavaPlugin plugin) {
		this.plugin = plugin;
		tryInit();
	}

	private void tryInit() {
		try {
			// com.sk89q.worldguard.bukkit.WGBukkit
			clsWGBukkit = Class.forName("com.sk89q.worldguard.bukkit.WGBukkit");

			// public static RegionManager getRegionManager(org.bukkit.World)
			mGetRegionManager = clsWGBukkit.getMethod("getRegionManager", World.class);

			// RegionManager (tipo dinamico)
			clsRegionManager = Class.forName("com.sk89q.worldguard.protection.managers.RegionManager");

			// ApplicableRegionSet (tipo dinamico)
			clsApplicableRegionSet = Class.forName("com.sk89q.worldguard.protection.ApplicableRegionSet");

			// RegionManager#getApplicableRegions(org.bukkit.Location)
			// (In WG6 esiste una overload bukkit-friendly; se non c'è, falliremo e andremo fail-open)
			mGetApplicableRegions = clsRegionManager.getMethod("getApplicableRegions", Location.class);

			// Set<ProtectedRegion> getRegions()
			mGetRegions = clsApplicableRegionSet.getMethod("getRegions");

			// ProtectedRegion#getId()
			clsProtectedRegion = Class.forName("com.sk89q.worldguard.protection.regions.ProtectedRegion");
			mGetId = clsProtectedRegion.getMethod("getId");

			available = true;
		} catch (Exception ex) {
			// WG6 non presente o API diversa → useremo fail-open
			available = false;
		} finally {
			initialized = true;
		}
	}

	@Override
	public boolean canUseBowInRegion(Player player) {
		if (!initialized) tryInit();

		// Nessun vincolo configurato → consenti
//		if (requiredRegionIds == null || requiredRegionIds.isEmpty()) {
//			return true;
//		}

		// Se WG6 reflection non disponibile → fail-open per non bloccare inutilmente
		if (!available) {
			return true;
		}

		try {
			Object regionManager = mGetRegionManager.invoke(null, player.getWorld());
			// Se il mondo non ha un RegionManager (es. non gestito da WG) → consenti
			if (regionManager == null) {
				return true;
			}

			Object applicableRegionSet = mGetApplicableRegions.invoke(regionManager, player.getLocation());
			if (applicableRegionSet == null) {
				return false;
			}

			@SuppressWarnings("unchecked")
			Set<Object> regions = (Set<Object>) mGetRegions.invoke(applicableRegionSet);
			if (regions == null || regions.isEmpty()) {
				return false;
			}

			// Normalizza gli ID richiesti
			Set<String> reqLower = new HashSet<>();
			reqLower.add("test");
//			for (String s : requiredRegionIds) {
//				if (s != null) {
//					reqLower.add(s.toLowerCase(Locale.ROOT));
//				}
//			}

			// Cerca almeno una corrispondenza
			for (Object pr : regions) {
				String id = (String) mGetId.invoke(pr);
				if (id != null && reqLower.contains(id.toLowerCase(Locale.ROOT))) {
					return true;
				}
			}
			return false;

		} catch (Exception e) {
			// Qualsiasi errore reflection → fail-open (evita di rompere il gameplay)
			return true;
		}
	}

	private void debug(String msg) {
		// opzionale: abilita un flag di debug nel tuo config e logga di conseguenza
		// plugin.getLogger().fine(msg); // Java Util Logging
		// oppure:
		// plugin.getLogger().info("[WG6Reflect] " + msg);
	}
}
