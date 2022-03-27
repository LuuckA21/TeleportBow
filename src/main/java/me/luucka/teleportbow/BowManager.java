package me.luucka.teleportbow;

import me.luucka.teleportbow.utils.Color;
import me.luucka.teleportbow.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public final class BowManager {

    public static ItemStack createTpBow() {

        ItemBuilder itemBuilder = new ItemBuilder(Material.BOW)
                .setDisplayName(Color.colorize(Objects.requireNonNull(TeleportBow.getPlugin().getConfig().getString("bow.name"))))
                .setLore(Color.colorize(TeleportBow.getPlugin().getConfig().getStringList("bow.lore")))
                .setPersistentDataContainerValue(TeleportBow.getPlugin(), "tpbow", "TpBow")
                .setUnbreakable(true)
                .hideAttributes()
                .hideUnbreakable();

        return itemBuilder.toItemStack();
    }

}
