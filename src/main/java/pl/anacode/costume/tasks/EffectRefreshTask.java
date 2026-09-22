package pl.anacode.costume.tasks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import pl.anacode.costume.CostumePlugin;

public class EffectRefreshTask extends BukkitRunnable {

    private final CostumePlugin plugin;

    public EffectRefreshTask(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.isValid() || !player.isOnline()) continue;

            String activeCostume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
            if (activeCostume != null) {
                applyCostumePassiveEffects(player, activeCostume);
            }

            String activePet = plugin.getPetManager().getActivePet(player.getUniqueId());
            if (activePet != null) {
                applyPetPassiveEffects(player, activePet);
            }
        }
    }

    private void applyCostumePassiveEffects(Player player, String costumeId) {
        switch (costumeId.toLowerCase()) {
            case "nurek" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, 40, 0, false, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 220, 0, false, false, true));
            }
            case "wampir" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, false, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 220, 0, false, false, true));
            }
            case "zabojczy-krolik" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 40, 1, false, false, true));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 40, 0, false, false, true));
            }
            case "mikolaj" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 40, 0, false, false, true));
            }
            default -> {
            }
        }
    }

    private void applyPetPassiveEffects(Player player, String petId) {
        switch (petId.toLowerCase()) {
            case "delfin" -> {
                if (player.isInWater()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 40, 0, false, false, true));
                }
            }
            case "krolik" -> {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 40, 0, false, false, true));
            }
            default -> {
            }
        }
    }
}
