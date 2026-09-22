package pl.anacode.costume.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import pl.anacode.costume.CostumePlugin;
import pl.anacode.costume.gui.ListaKostiumowGUI;
import pl.anacode.costume.gui.PetyGUI;
import pl.anacode.costume.gui.SzafkaGUI;

public class GUIClickListener implements Listener {

    private final CostumePlugin plugin;

    public GUIClickListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;

        String title = event.getView().getTitle();

        if (title.equals(SzafkaGUI.TITLE)) {
            event.setCancelled(true);
            ItemStack current = event.getCurrentItem();
            if (current == null || current.getType() == Material.AIR || current.getType() == Material.PURPLE_STAINED_GLASS_PANE) return;

            int slot = event.getRawSlot();

            if (slot == 40) {
                plugin.getCostumeManager().unequipCostume(player);
                player.sendMessage(ChatColor.GREEN + "Kostium zostal zdjety.");
                player.closeInventory();
                return;
            }

            if (slot == 36) {
                ListaKostiumowGUI.open(player, plugin);
                return;
            }

            if (slot == 44) {
                PetyGUI.open(player, plugin);
                return;
            }

            String costumeId = plugin.getCostumeManager().getIdFromDisplayItem(current);
            if (costumeId != null) {
                plugin.getCostumeManager().equipCostume(player, costumeId);
                player.closeInventory();
            }
            return;
        }

        if (title.equals(ListaKostiumowGUI.TITLE)) {
            event.setCancelled(true);
            ItemStack current = event.getCurrentItem();
            if (current == null || current.getType() == Material.AIR || current.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

            if (event.getRawSlot() == 49) {
                SzafkaGUI.open(player, plugin);
            }
            return;
        }

        if (title.equals(PetyGUI.TITLE)) {
            event.setCancelled(true);
            ItemStack current = event.getCurrentItem();
            if (current == null || current.getType() == Material.AIR || current.getType() == Material.CYAN_STAINED_GLASS_PANE) return;

            int slot = event.getRawSlot();

            if (slot == 31) {
                plugin.getPetManager().unequipPet(player);
                player.sendMessage(ChatColor.GREEN + "Odwolano zwierzaka.");
                player.closeInventory();
                return;
            }

            if (slot == 11) {
                handlePetToggle(player, "papuga");
            } else if (slot == 13) {
                handlePetToggle(player, "delfin");
            } else if (slot == 15) {
                handlePetToggle(player, "krolik");
            }
        }
    }

    private void handlePetToggle(Player player, String petId) {
        if (!plugin.getPetManager().hasPetUnlocked(player.getUniqueId(), petId)) {
            player.sendMessage(ChatColor.RED + "Nie posiadasz tego zwierzaka!");
            return;
        }

        String active = plugin.getPetManager().getActivePet(player.getUniqueId());
        if (petId.equalsIgnoreCase(active)) {
            plugin.getPetManager().unequipPet(player);
            player.sendMessage(ChatColor.YELLOW + "Schowano zwierzaka: " + petId);
        } else {
            plugin.getPetManager().equipPet(player, petId);
            player.sendMessage(ChatColor.GREEN + "Aktywowano zwierzaka: " + petId);
        }
        player.closeInventory();
    }
}
