package pl.anacode.costume.listeners;

import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;
import pl.anacode.costume.CostumePlugin;

public class DoubleJumpListener implements Listener {

    private final CostumePlugin plugin;

    public DoubleJumpListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        String costume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (costume == null || !costume.equalsIgnoreCase("maly-urwis")) {
            if (player.getAllowFlight() && !player.isFlying()) {
                // Nie ruszaj graczy z creative
            }
            return;
        }

        if (player.isOnGround()) {
            player.setAllowFlight(true);
        }
    }

    @EventHandler
    public void onFlightToggle(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR) return;

        String costume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (costume == null || !costume.equalsIgnoreCase("maly-urwis")) return;

        if (player.isOnGround()) return;

        event.setCancelled(true);
        player.setAllowFlight(false);
        player.setFlying(false);

        Vector velocity = player.getLocation().getDirection().multiply(0.5).setY(0.9);
        player.setVelocity(velocity);
        player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0f, 1.2f);
    }
}
