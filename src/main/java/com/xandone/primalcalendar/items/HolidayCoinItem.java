package com.xandone.primalcalendar.items;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class HolidayCoinItem {

    private final XandonePrimalCalendar plugin;
    private final NamespacedKey holidayCoinKey;

    public HolidayCoinItem(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
        this.holidayCoinKey = new NamespacedKey(plugin, "holiday_coin");
    }

    public ItemStack createHolidayCoin(int amount) {
        String materialName = plugin.getConfig().getString("holiday-coin.material", "SUNFLOWER");
        Material material;
        
        try {
            material = Material.valueOf(materialName.toUpperCase());
        } catch (IllegalArgumentException e) {
            plugin.getLogger().warning("Invalid material for holiday coin: " + materialName + ", using SUNFLOWER");
            material = Material.SUNFLOWER;
        }

        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            String name = plugin.getConfig().getString("holiday-coin.name", "&6Holiday Coin");
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));

            List<String> lore = new ArrayList<>();
            for (String line : plugin.getConfig().getStringList("holiday-coin.lore")) {
                lore.add(ChatColor.translateAlternateColorCodes('&', line));
            }
            meta.setLore(lore);

            // Mark as holiday coin using persistent data
            meta.getPersistentDataContainer().set(holidayCoinKey, PersistentDataType.BYTE, (byte) 1);

            item.setItemMeta(meta);
        }

        return item;
    }

    public boolean isHolidayCoin(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(holidayCoinKey, PersistentDataType.BYTE);
    }

    public NamespacedKey getHolidayCoinKey() {
        return holidayCoinKey;
    }
}
