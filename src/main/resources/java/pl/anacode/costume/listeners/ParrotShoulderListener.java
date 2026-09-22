package pl.anacode.costume.listeners;

import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityMountEvent;
import pl.anacode.costume.CostumePlugin;

public class ParrotShoulderListener implements Listener {

    private final CostumePlugin plugin;

    public ParrotShoulderListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onShoulderSit(EntityMountEvent event) {
        if (event.getMount() instanceof Player && event.getEntity() instanceof Parrot parrot) {
            if (plugin.getParrotSpawnManager().isPluginParrot(parrot)) {
                event.setCancelled(true);
            }
        }
    }
}
