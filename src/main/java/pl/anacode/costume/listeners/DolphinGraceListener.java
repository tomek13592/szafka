package pl.anacode.costume.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.anacode.costume.CostumePlugin;

public class DolphinGraceListener implements Listener {

    private final CostumePlugin plugin;

    public DolphinGraceListener(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        String pet = plugin.getPetManager().getActivePet(player.getUniqueId());
        if (pet == null || !pet.equalsIgnoreCase("delfin")) return;

        if (player.isInWater()) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 40, 0, false, false, true));
        }
    }
}
