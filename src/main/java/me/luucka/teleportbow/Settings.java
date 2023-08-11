package me.luucka.teleportbow;

import lombok.Getter;

import java.util.List;

public class Settings {

    private final TeleportBow plugin;

    public Settings(final TeleportBow plugin) {
        this.plugin = plugin;
        this.plugin.saveDefaultConfig();
        reload();
    }

    @Getter
    private String bowName;

    @Getter
    private List<String> bowLore;

    @Getter
    private int bowSlot;

    @Getter
    private int arrowSlot;

    @Getter
    private boolean giveOnJoin;

    @Getter
    private boolean canBeMovedInInventory;

    @Getter
    private boolean canBeDropped;

    @Getter
    private boolean canBeSwapped;

    private String prefix;

    private String reload;

    private String noConsole;

    private String noPerm;

    private String usage;

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
