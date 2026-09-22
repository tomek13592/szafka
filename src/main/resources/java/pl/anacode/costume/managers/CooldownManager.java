package pl.anacode.costume.managers;

import pl.anacode.costume.CostumePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CooldownManager {

    private final CostumePlugin plugin;
    private final Map<UUID, Map<String, Long>> cooldowns = new ConcurrentHashMap<>();

    public CooldownManager(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    public void setCooldown(UUID uuid, String ability, int seconds) {
        long expireTime = System.currentTimeMillis() + (seconds * 1000L);
        cooldowns.computeIfAbsent(uuid, k -> new HashMap<>()).put(ability.toLowerCase(), expireTime);
    }

    public boolean isOnCooldown(UUID uuid, String ability) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return false;

        Long expireTime = playerCooldowns.get(ability.toLowerCase());
        if (expireTime == null) return false;

        if (System.currentTimeMillis() < expireTime) {
            return true;
        }

        playerCooldowns.remove(ability.toLowerCase());
        return false;
    }

    public long getRemainingSeconds(UUID uuid, String ability) {
        Map<String, Long> playerCooldowns = cooldowns.get(uuid);
        if (playerCooldowns == null) return 0;

        Long expireTime = playerCooldowns.get(ability.toLowerCase());
        if (expireTime == null) return 0;

        long diff = expireTime - System.currentTimeMillis();
        return diff > 0 ? (diff / 1000L) : 0;
    }

    public void clearCooldowns(UUID uuid) {
        cooldowns.remove(uuid);
    }
}
