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

public class ListaKostiumowGUI {

    public static final String TITLE = ChatColor.DARK_GRAY + "Dostepne Kostiumy";

    public static void open(Player player, CostumePlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 54, TITLE);

        Set<String> costumeIds = plugin.getCostumeManager().getAllCostumeIds();
        int slot = 10;

        for (String id : costumeIds) {
            if (slot > 43) break;
            if (slot % 9 == 8) slot += 2;

            ItemStack item = plugin.getCostumeManager().getDisplayItem(id, player);
            inv.setItem(slot, item);
            slot++;
        }

        ItemStack glass = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }

        for (int i = 0; i < 54; i++) {
            if (inv.getItem(i) == null) {
                inv.setItem(i, glass);
            }
        }

        ItemStack back = new ItemStack(Material.BARRIER);
        ItemMeta backMeta = back.getItemMeta();
        if (backMeta != null) {
            backMeta.setDisplayName(ChatColor.RED + "Powrot do Szafy");
            back.setItemMeta(backMeta);
        }
        inv.setItem(49, back);

        player.openInventory(inv);
    }
}
