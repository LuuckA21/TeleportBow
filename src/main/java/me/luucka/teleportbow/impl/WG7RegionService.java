package me.luucka.teleportbow.impl;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;

public final class WG7RegionService implements IWorldGuardRegionService {

	@Override
	public boolean canUseBowInRegion(final Player player) {
//		if (required == null || required.isEmpty()) return true;

		Set<String> regionListFromConfig = new HashSet<>();
		regionListFromConfig.add("test");

		ApplicableRegionSet set = WorldGuard.getInstance()
				.getPlatform()
				.getRegionContainer()
				.createQuery()
				.getApplicableRegions(BukkitAdapter.adapt(player.getLocation()));

		for (final ProtectedRegion r : set.getRegions()) {
			if (regionListFromConfig.contains(r.getId().toLowerCase())) {
				return false;
			}
		}
		return true;
	}
}
