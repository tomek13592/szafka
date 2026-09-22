package pl.anacode.costume.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.anacode.costume.CostumePlugin;

public class NametagLineManager {

    private final CostumePlugin plugin;

    public NametagLineManager(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    public void updatePlayer(Player player) {
        if (player == null || !player.isOnline()) return;
        // Aktualizacja nametagu wedlug konfiguracji
    }

    public String getPrefix(Player player) {
        if (player == null) return "";
        String costume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (costume == null) return "";
        return ChatColor.translateAlternateColorCodes('&', "&8[&e" + costume.toUpperCase() + "&8] ");
    }
}
