package pl.anacode.costume.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.anacode.costume.CostumePlugin;

public class ArmorPreviewListener implements Listener {

    private final CostumePlugin plugin;

    public ArmorPreviewListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onArmorEquip(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;

        String costumeId = plugin.getCostumeManager().getIdFromArmorPiece(item);
        if (costumeId == null) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        plugin.getCostumeManager().equipCostume(player, costumeId);
    }

    @EventHandler
    public void onInventoryDrag(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        ItemStack current = event.getCurrentItem();
        if (current == null) return;

        String costumeId = plugin.getCostumeManager().getIdFromArmorPiece(current);
        if (costumeId == null) return;

        if (event.getSlotType() == org.bukkit.event.inventory.InventoryType.SlotType.ARMOR) {
            event.setCancelled(true);
        }
    }
}
