package pl.anacode.costume.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import pl.anacode.costume.CostumePlugin;

public class MarkingListener implements Listener {

    private final CostumePlugin plugin;

    public MarkingListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttackMark(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = null;
        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        }

        if (attacker == null || attacker.equals(victim)) return;

        String costume = plugin.getCostumeManager().getActiveCostume(attacker.getUniqueId());
        if (costume == null || !costume.equalsIgnoreCase("prezydent")) return;

        if (plugin.getCooldownManager().isOnCooldown(attacker.getUniqueId(), "prezydent_marking")) {
            return;
        }

        // 10 sekund oznaczania (kolor AQUA)
        plugin.getGlowingManager().setGlowing(victim, ChatColor.AQUA, 10);
        plugin.getCooldownManager().setCooldown(attacker.getUniqueId(), "prezydent_marking", 25);

        attacker.sendMessage(ChatColor.GREEN + "Oznaczyles gracza " + victim.getName() + " na 10 sekund!");
        victim.sendMessage(ChatColor.RED + "Zostales oznaczony przez Prezydenta " + attacker.getName() + "!");
    }
}
