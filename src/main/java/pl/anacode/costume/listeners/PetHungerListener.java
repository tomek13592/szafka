package pl.anacode.costume.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import pl.anacode.costume.CostumePlugin;

public class PetHungerListener implements Listener {

    private final CostumePlugin plugin;

    public PetHungerListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFeed(PlayerInteractEntityEvent event) {
        // Placeholder - obsluga karmienia petow (integracja z ParrotManager)
        plugin.getParrotManager().handleFeedInteraction(event);
    }
}
