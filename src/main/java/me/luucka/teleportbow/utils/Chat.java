package me.luucka.teleportbow.utils;

import me.luucka.lcore.utils.ColorTranslate;
import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Chat {

    public static String message(String s) {
        return ColorTranslate.translate(TeleportBow.getPlugin().getConfig().getString("reload") + s);
    }

}
