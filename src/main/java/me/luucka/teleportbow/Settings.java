package me.luucka.teleportbow;

import lombok.Getter;

import java.util.List;

public class Settings {

    private final TeleportBow PLUGIN;

    public Settings(final TeleportBow plugin) {
        this.PLUGIN = plugin;
        this.PLUGIN.saveDefaultConfig();
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
        PLUGIN.reloadConfig();
        bowName = _getBowName();
        bowLore = _getBowLore();
        bowSlot = _getBowSlot();
        arrowSlot = _getArrowSlot();
        giveOnJoin = _giveOnJoin();
        prefix = _getPrefix();
        reload = _getReload();
        noConsole = _getNoConsole();
        noPerm = _getNoPerm();
        usage = "&cUsage: /tpbow [reload]";
    }

    private String _getBowName() {
        return PLUGIN.getConfig().getString("bow.name");
    }

    private List<String> _getBowLore() {
        return PLUGIN.getConfig().getStringList("bow.lore");
    }

    private int _getBowSlot() {
        return PLUGIN.getConfig().getInt("bow.slot");
    }

    private int _getArrowSlot() {
        return PLUGIN.getConfig().getInt("bow.arrow-slot");
    }

    private boolean _giveOnJoin() {
        return PLUGIN.getConfig().getBoolean("bow.give-on-join");
    }

    private String _getPrefix() {
        String p = PLUGIN.getConfig().getString("message.prefix");
        return p.isEmpty() ? "" : p + " ";
    }

    private String _getReload() {
        return PLUGIN.getConfig().getString("message.reload");
    }

    private String _getNoConsole() {
        return PLUGIN.getConfig().getString("message.no-console");
    }

    private String _getNoPerm() {
        return PLUGIN.getConfig().getString("message.no-perm");
    }

}
