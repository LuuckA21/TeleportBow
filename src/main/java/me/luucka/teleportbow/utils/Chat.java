package me.luucka.teleportbow.utils;

import me.luucka.lcore.utils.ColorTranslate;
import me.luucka.teleportbow.TeleportBow;

public class Chat {

    public static String message(String s) {
        return ColorTranslate.translate(TeleportBow.getPlugin().getConfig().getString("message.prefix") + s);
    }

}
