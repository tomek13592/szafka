package pl.anacode.costume.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.anacode.costume.CostumePlugin;

public class ShulkerOpenListener implements Listener {

    private final CostumePlugin plugin;

    public ShulkerOpenListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onShulkerUse(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (item == null) return;
        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
        if (name == null || !name.equalsIgnoreCase("Skrzynia Kostiumow")) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        player.sendMessage(ChatColor.YELLOW + "Otwierasz skrzynie kostiumow...");

        String randomCostume = plugin.getCostumeManager().getRandomCostumeId();
        if (randomCostume == null) {
            player.sendMessage(ChatColor.RED + "Brak dostepnych kostiumow w konfiguracji!");
            return;
        }

        item.setAmount(item.getAmount() - 1);
        plugin.getDataManager().unlockCostume(player.getUniqueId(), randomCostume);
        player.sendMessage(ChatColor.GREEN + "Wylosowano kostium: " + randomCostume + "!");
    }
}
