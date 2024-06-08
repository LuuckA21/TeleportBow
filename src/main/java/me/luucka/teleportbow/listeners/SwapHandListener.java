package me.luucka.teleportbow.listeners;

import me.luucka.teleportbow.Settings;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class SwapHandListener implements Listener {

	private final TeleportBow plugin;
	private final Settings settings;

	public SwapHandListener(final TeleportBow plugin) {
		this.plugin = plugin;
		settings = plugin.getSettings();
	}

	@EventHandler
	public void onSwapHandItem(final PlayerSwapHandItemsEvent event) {
		final ItemStack mainHand = event.getMainHandItem();
		final ItemStack offHand = event.getOffHandItem();

		if (mainHand == null && offHand == null) return;

		boolean isMainHand = false;
		if (mainHand != null) {
			if (plugin.checkBow(mainHand)) isMainHand = true;
		}

		boolean isOffHand = false;
		if (offHand != null) {
			if (plugin.checkBow(offHand)) isOffHand = true;
		}

		if (!settings.isCanBeSwapped() && (isMainHand || isOffHand)) event.setCancelled(true);
	}
}
