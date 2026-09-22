package pl.anacode.costume.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.anacode.costume.CostumePlugin;

public class PlaceholderManager {

    private final CostumePlugin plugin;

    public PlaceholderManager(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    public void reload() {
        // Przeladowanie cache'owanych formatow placeholderow
    }

    public String format(Player player, String text) {
        if (text == null) return "";
        String out = text.replace("{name}", player != null ? player.getName() : "");
        String activeCostume = player != null ? plugin.getCostumeManager().getActiveCostume(player.getUniqueId()) : null;
        out = out.replace("{costume}", activeCostume != null ? activeCostume : "Brak");
        return ChatColor.translateAlternateColorCodes('&', out);
    }
}
