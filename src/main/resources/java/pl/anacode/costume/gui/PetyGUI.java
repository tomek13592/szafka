package pl.anacode.costume.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.anacode.costume.CostumePlugin;

import java.util.Arrays;
import java.util.List;

public class PetyGUI {

    public static final String TITLE = ChatColor.DARK_AQUA + "Twoje Zwierzaki";

    public static void open(Player player, CostumePlugin plugin) {
        Inventory inv = Bukkit.createInventory(null, 36, TITLE);

        ItemStack glass = new ItemStack(Material.CYAN_STAINED_GLASS_PANE);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null) {
            glassMeta.setDisplayName(" ");
            glass.setItemMeta(glassMeta);
        }

        for (int i = 0; i < 36; i++) {
            inv.setItem(i, glass);
        }

        String activePet = plugin.getPetManager().getActivePet(player.getUniqueId());

        inv.setItem(11, createPetIcon(plugin, player, "papuga", Material.FEATHER, "Papuga", "Towarzysz na Twoim ramieniu", activePet));
        inv.setItem(13, createPetIcon(plugin, player, "delfin", Material.PRISMARINE_SHARD, "Delfin", "Daje szybsze plywanie pod woda", activePet));
        inv.setItem(15, createPetIcon(plugin, player, "krolik", Material.RABBIT_FOOT, "Krolik", "Daje zwiekszony skok", activePet));

        ItemStack remove = new ItemStack(Material.BARRIER);
        ItemMeta removeMeta = remove.getItemMeta();
        if (removeMeta != null) {
            removeMeta.setDisplayName(ChatColor.RED + "Schowaj zwierzaka");
            removeMeta.setLore(List.of(ChatColor.GRAY + "Kliknij, aby odwolac aktywnego peta."));
            remove.setItemMeta(removeMeta);
        }
        inv.setItem(31, remove);

        player.openInventory(inv);
    }

    private static ItemStack createPetIcon(CostumePlugin plugin, Player player, String petId, Material mat, String name, String desc, String activePet) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            boolean has = plugin.getPetManager().hasPetUnlocked(player.getUniqueId(), petId);
            boolean isActive = petId.equalsIgnoreCase(activePet);

            meta.setDisplayName(ChatColor.GOLD + name + (isActive ? ChatColor.GREEN + " [AKTYWNY]" : ""));
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + desc,
                    " ",
                    has ? (isActive ? ChatColor.YELLOW + "Kliknij, aby schowac" : ChatColor.GREEN + "Kliknij, aby aktywowac")
                        : ChatColor.RED + "Nie odblokowano tego zwierzaka"
            ));
            item.setItemMeta(meta);
        }
        return item;
    }
}
