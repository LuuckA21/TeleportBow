package me.luucka.teleportbow.utils;

import me.luucka.teleportbow.TeleportBow;

public final class Chat {

    private Chat() {}

    public static String message(String s) {
        return Color.colorize(TeleportBow.getPlugin().getConfig().getString("message.prefix") + s);
    }

}
