package me.luucka.teleportbow.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Color {

    private static final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    private Color() {}

    public static String colorize(String input) {
        if (ServerVersion.MINOR >= 16) {
            Matcher match = pattern.matcher(input);
            while (match.find()) {
                String color = input.substring(match.start(), match.end());
                input = input.replace(color, ChatColor.of(color) + "");
                match = pattern.matcher(input);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', input);
    }

    public static List<String> colorize(List<String> inputs) {
        List<String> output = new ArrayList<>();
        inputs.forEach(input -> output.add(colorize(input)));
        return output;
    }

}
