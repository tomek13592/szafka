package pl.anacode.costume.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.anacode.costume.CostumePlugin;

public class DripstoneListener implements Listener {

    private final CostumePlugin plugin;

    public DripstoneListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(ignoreCancelled = true)
    public void onDripstoneDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALLING_BLOCK) return;

        String costume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (costume == null) return;

        if (costume.equalsIgnoreCase("zabojczy-krolik") || costume.equalsIgnoreCase("nurek")) {
            event.setDamage(event.getDamage() * 0.4);
        }
    }
}
