package pl.anacode.costume.managers;

import org.bukkit.entity.Player;
import pl.anacode.costume.CostumePlugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DisguiseManager {

    private final CostumePlugin plugin;
    private final Set<UUID> disguisedPlayers = new HashSet<>();

    public DisguiseManager(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    public void applyDisguise(Player player, String costumeId) {
        if (player == null) return;
        disguisedPlayers.add(player.getUniqueId());
        // Integracja z pakietami wyglądu lub LibsDisguises (jeśli obecne)
    }

    public void removeDisguise(Player player) {
        if (player == null) return;
        disguisedPlayers.remove(player.getUniqueId());
    }

    public boolean isDisguised(Player player) {
        return player != null && disguisedPlayers.contains(player.getUniqueId());
    }

    public void removeAllDisguises() {
        disguisedPlayers.clear();
    }
}
