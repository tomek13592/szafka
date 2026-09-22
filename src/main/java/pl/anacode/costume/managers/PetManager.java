package pl.anacode.costume.managers;

import org.bukkit.entity.Player;
import pl.anacode.costume.CostumePlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PetManager {

    private final CostumePlugin plugin;
    private final Map<UUID, String> activePets = new HashMap<>();
    private final Map<UUID, Set<String>> unlockedPets = new HashMap<>();

    public PetManager(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    public void equipPet(Player player, String petId) {
        if (player == null || petId == null) return;
        activePets.put(player.getUniqueId(), petId.toLowerCase());
        if (petId.equalsIgnoreCase("papuga")) {
            plugin.getParrotSpawnManager().spawnParrotForPlayer(player);
        } else {
            plugin.getParrotSpawnManager().despawnParrot(player.getUniqueId());
        }
    }

    public void unequipPet(Player player) {
        if (player == null) return;
        activePets.remove(player.getUniqueId());
        plugin.getParrotSpawnManager().despawnParrot(player.getUniqueId());
    }

    public String getActivePet(UUID uuid) {
        return activePets.get(uuid);
    }

    public boolean hasPetUnlocked(UUID uuid, String petId) {
        Set<String> set = unlockedPets.get(uuid);
        return set != null && set.contains(petId.toLowerCase());
    }

    public void unlockPet(UUID uuid, String petId) {
        unlockedPets.computeIfAbsent(uuid, k -> new HashSet<>()).add(petId.toLowerCase());
    }

    public void setUnlockedPets(UUID uuid, Set<String> pets) {
        unlockedPets.put(uuid, new HashSet<>(pets));
    }

    public Set<String> getUnlockedPets(UUID uuid) {
        return unlockedPets.getOrDefault(uuid, Set.of());
    }
}
