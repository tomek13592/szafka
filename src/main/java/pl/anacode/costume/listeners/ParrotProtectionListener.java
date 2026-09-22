package pl.anacode.costume.listeners;

import org.bukkit.entity.Parrot;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import pl.anacode.costume.CostumePlugin;

public class ParrotProtectionListener implements Listener {

    private final CostumePlugin plugin;

    public ParrotProtectionListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onParrotDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Parrot parrot && plugin.getParrotSpawnManager().isPluginParrot(parrot)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onParrotTarget(EntityTargetEvent event) {
        if (event.getTarget() instanceof Parrot parrot && plugin.getParrotSpawnManager().isPluginParrot(parrot)) {
            event.setCancelled(true);
        }
    }
}
