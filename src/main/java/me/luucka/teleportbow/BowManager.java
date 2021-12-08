package me.luucka.teleportbow;

import me.luucka.lcore.item.ItemBuilder;
import me.luucka.lcore.utils.ColorTranslate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BowManager {

    public static ItemStack createTpBow() {

        ItemBuilder itemBuilder = new ItemBuilder(Material.BOW)
                .setDisplayName(ColorTranslate.translate(Objects.requireNonNull(TeleportBow.getPlugin().getConfig().getString("bow.name"))))
                .setLore(ColorTranslate.translate(TeleportBow.getPlugin().getConfig().getStringList("bow.lore")))
                .setPersistentDataContainer(TeleportBow.getPlugin(), "tpbow", "TpBow")
                .setUnbreakable(true)
                .hideAttributes()
                .hideUnbreakable();

        return itemBuilder.toItemStack();
    }

}
