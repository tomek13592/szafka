package pl.anacode.costume.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.anacode.costume.CostumePlugin;

public class CostumeUseListener implements Listener {

    private final CostumePlugin plugin;

    public CostumeUseListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onRightClickCostumeItem(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null || !item.hasItemMeta()) return;

        String costumeId = plugin.getCostumeManager().getIdFromCostumeItem(item);
        if (costumeId == null) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        if (plugin.getDataManager().hasUnlocked(player.getUniqueId(), costumeId)) {
            player.sendMessage(ChatColor.YELLOW + "Posiadasz juz ten kostium w szafie (/kostium)!");
            return;
        }

        item.setAmount(item.getAmount() - 1);
        plugin.getDataManager().unlockCostume(player.getUniqueId(), costumeId);
        player.sendMessage(ChatColor.GREEN + "Pomyslnie dodano kostium " + costumeId + " do Twojej szafy!");
    }
}
