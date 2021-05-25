package me.luucka.teleportbow;

import me.luucka.teleportbow.utility.ChatUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class BowManager {

    public static ItemStack createTpBow() {
        ItemStack tpBow = new ItemStack(Material.BOW);
        ItemMeta meta = tpBow.getItemMeta();

        meta.setDisplayName(ChatUtils.hexColor(TeleportBow.plugin().getConfig().getString("bow.name")));
        List<String> lore = new ArrayList<>();
        lore.add(ChatUtils.hexColor(TeleportBow.plugin().getConfig().getString("bow.lore")));
        meta.setLore(lore);
        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);

        NamespacedKey key = new NamespacedKey(TeleportBow.plugin(), "tpbow");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "TpBow");

        tpBow.setItemMeta(meta);

        return tpBow;
    }

}
