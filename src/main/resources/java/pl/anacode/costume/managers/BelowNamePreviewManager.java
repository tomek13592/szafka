package pl.anacode.costume.managers;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import pl.anacode.costume.CostumePlugin;

public class BelowNamePreviewManager {

    private final CostumePlugin plugin;

    public BelowNamePreviewManager(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    public void updatePlayer(Player player) {
        if (player == null || !player.isOnline()) return;
    }

    public String getPreviewText(Player player) {
        if (player == null) return "";
        String costume = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
        if (costume == null) return ChatColor.GRAY + "Brak";
        return ChatColor.GOLD + costume;
    }
}
