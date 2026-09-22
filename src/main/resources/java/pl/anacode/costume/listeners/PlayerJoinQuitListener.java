package pl.anacode.costume.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import pl.anacode.costume.CostumePlugin;

public class PlayerJoinQuitListener implements Listener {

    private final CostumePlugin plugin;

    public PlayerJoinQuitListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        plugin.getDataManager().loadPlayerData(player.getUniqueId());

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!player.isOnline()) return;

            plugin.getGlowingManager().syncToNewPlayer(player);
            plugin.getNametagLineManager().updatePlayer(player);
            plugin.getBelowNamePreviewManager().updatePlayer(player);

            String activeCostume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
            if (activeCostume != null) {
                plugin.getCostumeManager().reapplyCostumeEffects(player, activeCostume);
            }

            String activePet = plugin.getPetManager().getActivePet(player.getUniqueId());
            if (activePet != null && activePet.equalsIgnoreCase("papuga")) {
                plugin.getParrotSpawnManager().spawnParrotForPlayer(player);
            }
        }, 10L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        // 1. Natychmiastowe czyszczenie Glowingu przed rozłączeniem (zapobiega zostawaniu kolorów i crashom)
        plugin.getGlowingManager().cleanupPlayer(player);

        // 2. Despawn papug i przebrań
        plugin.getParrotSpawnManager().despawnParrot(player.getUniqueId());
        plugin.getDisguiseManager().removeDisguise(player);

        // 3. Reset serduszek
        plugin.getHeartManager().resetExtraHearts(player);

        // 4. Zapis danych
        plugin.getDataManager().savePlayerData(player.getUniqueId());
    }
}
