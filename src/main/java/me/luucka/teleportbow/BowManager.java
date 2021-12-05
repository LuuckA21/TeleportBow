package me.luucka.teleportbow;

import me.luucka.lcore.item.ItemBuilder;
import me.luucka.lcore.utils.ColorTranslate;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;

public class BowManager {

    public static ItemStack createTpBow() {

        ItemBuilder itemBuilder = new ItemBuilder(Material.BOW);
        itemBuilder.setDisplayName(ColorTranslate.translate(Objects.requireNonNull(TeleportBow.getPlugin().getConfig().getString("bow.name"))))
                .setLore(ColorTranslate.translate(TeleportBow.getPlugin().getConfig().getStringList("bow.lore")))
                .setUnbreakable(true)
                .hideUnbreakable()
                .hideAttributes()
                .setPersistentDataContainer(TeleportBow.getPlugin(), "tpbow", "TpBow");

        return itemBuilder.toItemStack();
    }

}
