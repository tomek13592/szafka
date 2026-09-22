package pl.anacode.costume.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.anacode.costume.CostumePlugin;

public class InfectionListener implements Listener {

    private final CostumePlugin plugin;

    public InfectionListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInfect(EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player victim)) return;

        Player attacker = null;
        if (event.getDamager() instanceof Player p) {
            attacker = p;
        } else if (event.getDamager() instanceof Projectile proj && proj.getShooter() instanceof Player p) {
            attacker = p;
        }

        if (attacker == null || attacker.equals(victim)) return;

        String victimCostume = plugin.getCostumeManager().getActiveCostume(victim.getUniqueId());
        if (victimCostume != null && victimCostume.equalsIgnoreCase("przeciwzakazeniowy")) {
            return;
        }

        String attackerCostume = plugin.getCostumeManager().getActiveCostume(attacker.getUniqueId());
        if (attackerCostume == null || !attackerCostume.equalsIgnoreCase("mim")) return;

        if (plugin.getCooldownManager().isOnCooldown(attacker.getUniqueId(), "mim_infection")) {
            return;
        }

        // Zakażenie: 8 sekund efektu ciemno-fioletowego/zielonego glowingu
        plugin.getGlowingManager().setGlowing(victim, ChatColor.DARK_PURPLE, 8);
        victim.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 160, 0, false, true, true));
        victim.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 160, 0, false, true, true));

        plugin.getCooldownManager().setCooldown(attacker.getUniqueId(), "mim_infection", 30);

        attacker.sendMessage(ChatColor.DARK_PURPLE + "Zakaziles gracza " + victim.getName() + "!");
        victim.sendMessage(ChatColor.DARK_PURPLE + "Zostales zakazony przez Mima " + attacker.getName() + "!");
    }
}
