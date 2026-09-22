package pl.anacode.costume.listeners;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import pl.anacode.costume.CostumePlugin;

public class DamageListener implements Listener {

    private final CostumePlugin plugin;

    public DamageListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        String active = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (active == null) return;

        if (active.equalsIgnoreCase("pirat") && event.getCause() == EntityDamageEvent.DamageCause.DROWNING) {
            event.setCancelled(true);
            return;
        }

        if (active.equalsIgnoreCase("mikolaj") && (event.getCause() == EntityDamageEvent.DamageCause.FIRE || event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK || event.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
            event.setDamage(event.getDamage() * 0.5);
        }

        if (active.equalsIgnoreCase("zabojczy-krolik") && event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setDamage(event.getDamage() * 0.3);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPvPDamage(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = null;
        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        }

        if (attacker == null || attacker.equals(victim)) return;

        String attackerCostume = plugin.getCostumeManager().getActiveCostume(attacker.getUniqueId());
        if (attackerCostume != null) {
            if (attackerCostume.equalsIgnoreCase("wampir")) {
                double healAmount = event.getFinalDamage() * 0.15;
                attacker.setHealth(Math.min(attacker.getMaxHealth(), attacker.getHealth() + healAmount));
            }
        }
    }
}
