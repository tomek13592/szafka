package pl.anacode.costume.listeners;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import pl.anacode.costume.CostumePlugin;

public class SnowTrailListener implements Listener {

    private final CostumePlugin plugin;

    public SnowTrailListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (event.getFrom().distanceSquared(event.getTo()) < 0.01) return;

        String costume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (costume == null || !costume.equalsIgnoreCase("mikolaj")) return;

        Location loc = player.getLocation();
        player.getWorld().spawnParticle(Particle.SNOWFLAKE, loc, 5, 0.3, 0.1, 0.3, 0.01);
    }
}
