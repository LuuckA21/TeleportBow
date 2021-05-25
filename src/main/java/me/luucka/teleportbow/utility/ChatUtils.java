package me.luucka.teleportbow.utility;

import me.luucka.teleportbow.TeleportBow;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static final Pattern pattern = Pattern.compile("&#[a-fA-F0-9]{6}");

    public static List<String> hexColor(List<String> list) {
        list.forEach(ChatUtils::hexColor);
        return list;
    }

    public static String hexColor(String content) {
        if (Bukkit.getVersion().contains("1.16")) {
            if (content.contains("&#")) {
                Matcher match = pattern.matcher(content);
                while (match.find()) {
                    String color = content.substring(match.start(), match.end());
                    content = content.replace(color, net.md_5.bungee.api.ChatColor.of(color.replaceAll("&", "")) + "");
                    match = pattern.matcher(content);
                }
                return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', content);
            }
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', content);
    }

    public static String message(String s) {
        return hexColor(TeleportBow.plugin().getConfig().getString("message.prefix") + s);
    }

}
