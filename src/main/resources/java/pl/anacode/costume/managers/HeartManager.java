package pl.anacode.costume.managers;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import pl.anacode.costume.CostumePlugin;

import java.util.UUID;

public class HeartManager {

    private final CostumePlugin plugin;
    private static final double DEFAULT_MAX_HEALTH = 20.0;

    public HeartManager(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    public void setExtraHearts(Player player, double extraHealth) {
        if (player == null || !player.isOnline()) return;

        AttributeInstance attr = getHealthAttribute(player);
        if (attr != null) {
            attr.setBaseValue(DEFAULT_MAX_HEALTH + extraHealth);
        }
    }

    public void resetExtraHearts(Player player) {
        if (player == null) return;

        AttributeInstance attr = getHealthAttribute(player);
        if (attr != null) {
            attr.setBaseValue(DEFAULT_MAX_HEALTH);
            if (player.getHealth() > DEFAULT_MAX_HEALTH) {
                player.setHealth(DEFAULT_MAX_HEALTH);
            }
        }
    }

    public void resetAll() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            resetExtraHearts(player);
        }
    }

    private AttributeInstance getHealthAttribute(Player player) {
        try {
            AttributeInstance attr = player.getAttribute(Attribute.MAX_HEALTH);
            if (attr != null) return attr;
        } catch (Throwable ignored) {}

        try {
            return player.getAttribute(Attribute.valueOf("GENERIC_MAX_HEALTH"));
        } catch (Throwable ignored) {
            return null;
        }
    }
}
