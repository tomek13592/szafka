package pl.anacode.costume.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import pl.anacode.costume.CostumePlugin;

public class ExtraPetListener implements Listener {

    private final CostumePlugin plugin;

    public ExtraPetListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onWorldChange(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        String pet = plugin.getPetManager().getActivePet(player.getUniqueId());
        if (pet != null && pet.equalsIgnoreCase("papuga")) {
            plugin.getParrotSpawnManager().despawnParrot(player.getUniqueId());
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline()) plugin.getParrotSpawnManager().spawnParrotForPlayer(player);
            }, 5L);
        }
    }

    @EventHandler
    public void onTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        String pet = plugin.getPetManager().getActivePet(player.getUniqueId());
        if (pet != null && pet.equalsIgnoreCase("papuga")) {
            plugin.getParrotSpawnManager().teleportParrotToPlayer(player);
        }
    }

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;
            String pet = plugin.getPetManager().getActivePet(player.getUniqueId());
            if (pet != null && pet.equalsIgnoreCase("papuga")) {
                plugin.getParrotSpawnManager().spawnParrotForPlayer(player);
            }
        }, 20L);
    }
}
