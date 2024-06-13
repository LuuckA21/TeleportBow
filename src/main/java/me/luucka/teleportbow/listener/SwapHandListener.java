package me.luucka.teleportbow.listener;

import me.luucka.teleportbow.BowManager;
import me.luucka.teleportbow.Settings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class SwapHandListener implements Listener {

	@EventHandler
	public void onSwapHandItem(final PlayerSwapHandItemsEvent event) {
		final ItemStack mainHand = event.getMainHandItem();
		final ItemStack offHand = event.getOffHandItem();

		if (mainHand == null && offHand == null) return;

		boolean isMainHand = false;
		if (mainHand != null) {
			if (BowManager.checkBow(mainHand)) isMainHand = true;
		}

		boolean isOffHand = false;
		if (offHand != null) {
			if (BowManager.checkBow(offHand)) isOffHand = true;
		}

		if (!Settings.CAN_BE_SWAPPED && (isMainHand || isOffHand)) event.setCancelled(true);
	}
}
