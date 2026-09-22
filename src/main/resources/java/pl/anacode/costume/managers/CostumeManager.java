package pl.anacode.costume.managers;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import pl.anacode.costume.CostumePlugin;

import java.io.File;
import java.util.*;

public class CostumeManager {

    private final CostumePlugin plugin;
    private final NamespacedKey costumeKey;
    private final NamespacedKey armorCostumeKey;
    private final Map<UUID, String> activeCostumes = new HashMap<>();
    private FileConfiguration costumeConfig;

    public CostumeManager(CostumePlugin plugin) {
        this.plugin = plugin;
        this.costumeKey = new NamespacedKey(plugin, "costume_id");
        this.armorCostumeKey = new NamespacedKey(plugin, "costume_armor_id");
        loadConfiguration();
    }

    public void reload() {
        loadConfiguration();
    }

    private void loadConfiguration() {
        File file = new File(plugin.getDataFolder(), "kostiumConfiguration.yml");
        if (!file.exists()) {
            plugin.saveResource("kostiumConfiguration.yml", false);
        }
        this.costumeConfig = YamlConfiguration.loadConfiguration(file);
    }

    public void equipCostume(Player player, String costumeId) {
        if (player == null || costumeId == null) return;
        String lower = costumeId.toLowerCase();

        unequipCostume(player);

        activeCostumes.put(player.getUniqueId(), lower);
        reapplyCostumeEffects(player, lower);

        player.sendMessage(ChatColor.GREEN + "Zalozono kostium: " + getCostumeDisplayName(lower));
        plugin.getNametagLineManager().updatePlayer(player);
        plugin.getBelowNamePreviewManager().updatePlayer(player);
    }

    public void unequipCostume(Player player) {
        if (player == null) return;
        String previous = activeCostumes.remove(player.getUniqueId());
        if (previous != null) {
            plugin.getHeartManager().resetExtraHearts(player);
            plugin.getGlowingManager().removeGlowing(player);
            plugin.getDisguiseManager().removeDisguise(player);
        }
        plugin.getNametagLineManager().updatePlayer(player);
        plugin.getBelowNamePreviewManager().updatePlayer(player);
    }

    public void reapplyCostumeEffects(Player player, String costumeId) {
        if (costumeId.equalsIgnoreCase("wampir")) {
            plugin.getHeartManager().setExtraHearts(player, 8.0);
        } else if (costumeId.equalsIgnoreCase("mikolaj")) {
            plugin.getHeartManager().setExtraHearts(player, 4.0);
        }

        ConfigurationSection sec = costumeConfig.getConfigurationSection("costumes." + costumeId.toLowerCase() + ".effects");
        if (sec != null) {
            List<String> effectList = costumeConfig.getStringList("costumes." + costumeId.toLowerCase() + ".effects");
            for (String eff : effectList) {
                String[] parts = eff.split(":");
                if (parts.length >= 3) {
                    try {
                        PotionEffectType type = PotionEffectType.getByName(parts[0].toUpperCase());
                        int amp = Integer.parseInt(parts[1]);
                        int dur = Integer.parseInt(parts[2]);
                        if (type != null) {
                            player.addPotionEffect(new PotionEffect(type, dur * 20, amp, false, false, true));
                        }
                    } catch (Exception ignored) {}
                }
            }
        }
    }

    public String getActiveCostume(UUID uuid) {
        return activeCostumes.get(uuid);
    }

    public String getActiveCostumeDisplayName(UUID uuid) {
        String active = activeCostumes.get(uuid);
        return active != null ? getCostumeDisplayName(active) : "Brak";
    }

    public String getCostumeDisplayName(String costumeId) {
        String name = costumeConfig.getString("costumes." + costumeId.toLowerCase() + ".display-name");
        return name != null ? ChatColor.translateAlternateColorCodes('&', name) : costumeId;
    }

    public Set<String> getAllCostumeIds() {
        ConfigurationSection sec = costumeConfig.getConfigurationSection("costumes");
        return (sec != null) ? sec.getKeys(false) : Set.of();
    }

    public String getRandomCostumeId() {
        List<String> list = new ArrayList<>(getAllCostumeIds());
        if (list.isEmpty()) return null;
        return list.get(new Random().nextInt(list.size()));
    }

    public ItemStack createCostumeItem(String costumeId) {
        String path = "costumes." + costumeId.toLowerCase();
        if (!costumeConfig.contains(path)) return null;

        String matStr = costumeConfig.getString(path + ".material", "LEATHER_CHESTPLATE");
        Material mat = Material.matchMaterial(matStr);
        if (mat == null) mat = Material.LEATHER_CHESTPLATE;

        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(getCostumeDisplayName(costumeId));

            List<String> lore = costumeConfig.getStringList(path + ".lore");
            List<String> coloredLore = new ArrayList<>();
            for (String l : lore) {
                coloredLore.add(ChatColor.translateAlternateColorCodes('&', l));
            }
            meta.setLore(coloredLore);

            int cmd = costumeConfig.getInt(path + ".custom-model-data", 0);
            if (cmd > 0) {
                meta.setCustomModelData(cmd);
            }

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.getPersistentDataContainer().set(costumeKey, PersistentDataType.STRING, costumeId.toLowerCase());
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack getDisplayItem(String costumeId, Player player) {
        ItemStack item = createCostumeItem(costumeId);
        if (item == null) return new ItemStack(Material.BARRIER);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add(" ");
            boolean unlocked = plugin.getDataManager().hasUnlocked(player.getUniqueId(), costumeId);
            if (unlocked) {
                lore.add(ChatColor.GREEN + "Posiadasz ten kostium!");
            } else {
                lore.add(ChatColor.RED + "Nie odblokowano!");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public ItemStack createWardrobeDisplayItem(String costumeId, boolean isActive) {
        ItemStack item = createCostumeItem(costumeId);
        if (item == null) return new ItemStack(Material.BARRIER);

        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            List<String> lore = meta.getLore() != null ? new ArrayList<>(meta.getLore()) : new ArrayList<>();
            lore.add(" ");
            if (isActive) {
                lore.add(ChatColor.GREEN + "[AKTYWNY] Kliknij, aby zdjac");
            } else {
                lore.add(ChatColor.YELLOW + "Kliknij, aby zalozyc");
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    public String getIdFromCostumeItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(costumeKey, PersistentDataType.STRING);
    }

    public String getIdFromArmorPiece(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        return item.getItemMeta().getPersistentDataContainer().get(armorCostumeKey, PersistentDataType.STRING);
    }

    public String getIdFromDisplayItem(ItemStack item) {
        return getIdFromCostumeItem(item);
    }
}
