package pl.anacode.costume.listeners;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import pl.anacode.costume.CostumePlugin;

public class ShiftListener implements Listener {

    private final CostumePlugin plugin;

    public ShiftListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent event) {
        if (!event.isSneaking()) return;

        Player player = event.getPlayer();
        String costume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (costume == null) return;

        if (costume.equalsIgnoreCase("maly-urwis")) {
            if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId(), "urwis_jumpscare")) return;

            for (Player nearby : player.getWorld().getPlayers()) {
                if (nearby.equals(player)) continue;
                if (nearby.getLocation().distanceSquared(player.getLocation()) < 25) {
                    nearby.playSound(nearby.getLocation(), Sound.ENTITY_GHAST_SCREAM, 1.5f, 2.0f);
                    nearby.sendMessage(ChatColor.YELLOW + "BOO! " + ChatColor.GRAY + "Wystraszyl Cie " + player.getName());
                }
            }
            plugin.getCooldownManager().setCooldown(player.getUniqueId(), "urwis_jumpscare", 45);
        }
    }
}
