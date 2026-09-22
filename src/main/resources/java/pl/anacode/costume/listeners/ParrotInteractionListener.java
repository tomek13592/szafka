package pl.anacode.costume.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import pl.anacode.costume.CostumePlugin;

public class ParrotInteractionListener implements Listener {

    private final CostumePlugin plugin;

    public ParrotInteractionListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onParrotInteract(PlayerInteractEntityEvent event) {
        if (!(event.getRightClicked() instanceof Parrot parrot)) return;

        Player player = event.getPlayer();
        if (!plugin.getParrotSpawnManager().isPluginParrot(parrot)) return;

        event.setCancelled(true);

        if (!plugin.getParrotSpawnManager().isOwner(parrot, player)) {
            player.sendMessage(ChatColor.RED + "Ta papuga nalezy do innego gracza!");
            return;
        }

        plugin.getParrotManager().openParrotMenu(player);
    }
}
