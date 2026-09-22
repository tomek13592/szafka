package pl.anacode.costume.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import pl.anacode.costume.CostumePlugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataManager {

    private final CostumePlugin plugin;
    private final File dataFolder;
    private final Map<UUID, Set<String>> unlockedCostumes = new HashMap<>();

    public DataManager(CostumePlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "userdata");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public void loadAll() {
        File[] files = dataFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                String name = file.getName().replace(".yml", "");
                UUID uuid = UUID.fromString(name);
                loadPlayerData(uuid);
            } catch (IllegalArgumentException ignored) {}
        }
    }

    public void saveAll() {
        for (UUID uuid : unlockedCostumes.keySet()) {
            savePlayerData(uuid);
        }
    }

    public void reload() {
        saveAll();
        unlockedCostumes.clear();
        loadAll();
    }

    public void loadPlayerData(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        if (!file.exists()) {
            unlockedCostumes.put(uuid, new HashSet<>());
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<String> costumes = config.getStringList("unlocked-costumes");
        unlockedCostumes.put(uuid, new HashSet<>(costumes));

        List<String> pets = config.getStringList("unlocked-pets");
        plugin.getPetManager().setUnlockedPets(uuid, new HashSet<>(pets));

        String activeCostume = config.getString("active-costume");
        if (activeCostume != null && !activeCostume.isEmpty()) {
            plugin.getCostumeManager().equipCostume(plugin.getServer().getPlayer(uuid), activeCostume);
        }

        String parrotType = config.getString("parrot-type");
        if (parrotType != null) {
            plugin.getParrotManager().setParrotType(uuid, parrotType);
        }
    }

    public void savePlayerData(UUID uuid) {
        File file = new File(dataFolder, uuid.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();

        Set<String> costumes = unlockedCostumes.getOrDefault(uuid, Collections.emptySet());
        config.set("unlocked-costumes", new ArrayList<>(costumes));

        Set<String> pets = plugin.getPetManager().getUnlockedPets(uuid);
        config.set("unlocked-pets", new ArrayList<>(pets));

        String active = plugin.getCostumeManager().getActiveCostume(uuid);
        config.set("active-costume", active);

        String parrotType = plugin.getParrotManager().getParrotType(uuid);
        config.set("parrot-type", parrotType);

        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Nie udalo sie zapisac danych gracza: " + uuid);
        }
    }

    public boolean hasUnlocked(UUID uuid, String costumeId) {
        Set<String> set = unlockedCostumes.get(uuid);
        return set != null && set.contains(costumeId.toLowerCase());
    }

    public void unlockCostume(UUID uuid, String costumeId) {
        unlockedCostumes.computeIfAbsent(uuid, k -> new HashSet<>()).add(costumeId.toLowerCase());
        savePlayerData(uuid);
    }

    public Set<String> getUnlockedCostumes(UUID uuid) {
        return unlockedCostumes.getOrDefault(uuid, Collections.emptySet());
    }
}
