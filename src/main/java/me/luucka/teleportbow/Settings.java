package me.luucka.teleportbow;

import java.util.List;

public class Settings {

	private final TeleportBow plugin;

	public Settings(final TeleportBow plugin) {
		this.plugin = plugin;
		this.plugin.saveDefaultConfig();
		reload();
	}

	private String bowName;

	private List<String> bowLore;

	private int bowSlot;

	private int arrowSlot;

	private boolean giveOnJoin;

	private boolean canBeMovedInInventory;

	private boolean canBeDropped;

	private boolean canBeSwapped;

	private String prefix;

	private String reload;

	private String noConsole;

	private String noPerm;

	private String usage;

	public String getBowName() {
		return bowName;
	}

	public List<String> getBowLore() {
		return bowLore;
	}

	public int getBowSlot() {
		return bowSlot;
	}

	public int getArrowSlot() {
		return arrowSlot;
	}

	public boolean isGiveOnJoin() {
		return giveOnJoin;
	}

	public boolean isCanBeMovedInInventory() {
		return canBeMovedInInventory;
	}

	public boolean isCanBeDropped() {
		return canBeDropped;
	}

	public boolean isCanBeSwapped() {
		return canBeSwapped;
	}

	public String getReload() {
		return prefix + reload;
	}

	public String getNoConsole() {
		return prefix + noConsole;
	}

	public String getNoPerm() {
		return prefix + noPerm;
	}

	public String getUsage() {
		return prefix + usage;
	}

	public void reload() {
		plugin.reloadConfig();
		bowName = plugin.getConfig().getString("bow.name");
		bowLore = plugin.getConfig().getStringList("bow.lore");
		bowSlot = plugin.getConfig().getInt("bow.slot");
		arrowSlot = plugin.getConfig().getInt("bow.arrow-slot");
		giveOnJoin = plugin.getConfig().getBoolean("bow.give-on-join");
		canBeMovedInInventory = plugin.getConfig().getBoolean("bow.can-be-moved-in-inventory");
		canBeDropped = plugin.getConfig().getBoolean("bow.can-be-dropped");
		canBeSwapped = plugin.getConfig().getBoolean("bow.can-be-swapped");
		prefix = _getPrefix();
		reload = plugin.getConfig().getString("message.reload");
		noConsole = plugin.getConfig().getString("message.no-console");
		noPerm = plugin.getConfig().getString("message.no-perm");
		usage = plugin.getConfig().getString("message.usage", "&cUsage: /tpbow [reload]");
	}

	private String _getPrefix() {
		String p = plugin.getConfig().getString("message.prefix");
		if (p == null) return "";
		return p.isEmpty() ? "" : p + " ";
	}

}
