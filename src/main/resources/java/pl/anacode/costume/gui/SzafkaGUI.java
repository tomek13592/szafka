package pl.anacode.costume.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.anacode.costume.CostumePlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SzafkaGUI {

    public static final String TITLE = ChatColor.DARK_PURPLE + "Twoja Szafa Kostiumow";

    public static void open(Player player, CostumePlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 45, TITLE);

        ItemStack glass = new ItemStack(Material.PURPLE_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }

        for (int i = 0; i < 45; i++) {
            inv.setItem(i, glass);
        }

        Set<String> unlocked = plugin.getDataManager().getUnlockedCostumes(player.getUniqueId());
        String active = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());

        int slot = 10;
        for (String id : unlocked) {
            if (slot > 34) break;
            if (slot % 9 == 8) slot += 2;

            ItemStack item = plugin.getCostumeManager().createWardrobeDisplayItem(id, id.equalsIgnoreCase(active));
            inv.setItem(slot, item);
            slot++;
        }

        ItemStack removeBtn = new ItemStack(Material.BARRIER);
        ItemMeta removeMeta = removeBtn.getItemMeta();
        if (removeMeta != null) {
            removeMeta.setDisplayName(ChatColor.RED + "Zdejmij Kostium");
            removeMeta.setLore(List.of(ChatColor.GRAY + "Kliknij, aby zdjac aktualny kostium."));
            removeBtn.setItemMeta(removeMeta);
        }
        inv.setItem(40, removeBtn);

        ItemStack catalogBtn = new ItemStack(Material.BOOK);
        ItemMeta catalogMeta = catalogBtn.getItemMeta();
        if (catalogMeta != null) {
            catalogMeta.setDisplayName(ChatColor.GOLD + "Katalog Kostiumow");
            catalogMeta.setLore(List.of(ChatColor.GRAY + "Przegladaj wszystkie dostepne kostiumy na serwerze."));
            catalogBtn.setItemMeta(catalogMeta);
        }
        inv.setItem(36, catalogBtn);

        ItemStack petsBtn = new ItemStack(Material.LEAD);
        ItemMeta petsMeta = petsBtn.getItemMeta();
        if (petsMeta != null) {
            petsMeta.setDisplayName(ChatColor.AQUA + "Menu Zwierzakow");
            petsMeta.setLore(List.of(ChatColor.GRAY + "Zarzadzaj swoimi zwierzakami i papuga."));
            petsBtn.setItemMeta(petsMeta);
        }
        inv.setItem(44, petsBtn);

        player.openInventory(inv);
    }
}
