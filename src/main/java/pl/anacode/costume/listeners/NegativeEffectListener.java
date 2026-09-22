package pl.anacode.costume.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.potion.PotionEffectType;
import pl.anacode.costume.CostumePlugin;

public class NegativeEffectListener implements Listener {

    private final CostumePlugin plugin;

    public NegativeEffectListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onEffectAdd(EntityPotionEffectEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (event.getNewEffect() == null) return;

        String costume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (costume == null) return;

        PotionEffectType type = event.getNewEffect().getType();

        if (costume.equalsIgnoreCase("przeciwzakazeniowy")) {
            if (type.equals(PotionEffectType.POISON) || type.equals(PotionEffectType.WITHER) || type.equals(PotionEffectType.HUNGER)) {
                event.setCancelled(true);
            }
        }

        if (costume.equalsIgnoreCase("wampir")) {
            if (type.equals(PotionEffectType.REGENERATION) && event.getCause() == EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD) {
                event.setCancelled(true);
            }
        }
    }
}
