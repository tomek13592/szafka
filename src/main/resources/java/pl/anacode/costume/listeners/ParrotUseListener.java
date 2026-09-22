package pl.anacode.costume.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.anacode.costume.CostumePlugin;

public class ParrotUseListener implements Listener {

    private final CostumePlugin plugin;

    public ParrotUseListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onParrotVoucherUse(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_AIR && event.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        ItemStack item = event.getItem();
        if (item == null) return;

        String parrotType = plugin.getParrotManager().getTypeFromParrotItem(item);
        if (parrotType == null) return;

        event.setCancelled(true);
        Player player = event.getPlayer();

        item.setAmount(item.getAmount() - 1);
        plugin.getPetManager().unlockPet(player.getUniqueId(), "papuga");
        plugin.getPetManager().equipPet(player, "papuga");
        plugin.getParrotManager().setParrotType(player.getUniqueId(), parrotType);
        plugin.getParrotSpawnManager().spawnParrotForPlayer(player);

        player.sendMessage(ChatColor.GREEN + "Pomyslnie aktywowano papuge typu: " + parrotType.toUpperCase() + "!");
    }
}
