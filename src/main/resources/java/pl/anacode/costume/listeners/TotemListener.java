package pl.anacode.costume.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityResurrectEvent;
import pl.anacode.costume.CostumePlugin;

public class TotemListener implements Listener {

    private final CostumePlugin plugin;

    public TotemListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onResurrect(EntityResurrectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        String costume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (costume == null) return;

        if (costume.equalsIgnoreCase("wampir") || costume.equalsIgnoreCase("mikolaj")) {
            player.sendMessage(ChatColor.GOLD + "Twoj kostium wzmocnil moc totemu!");
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) {
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + 6));
                }
            }, 1L);
        }
    }
}
