package pl.anacode.costume.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import pl.anacode.costume.CostumePlugin;

import java.util.*;

public class ParrotManager {

    private final CostumePlugin plugin;
    private final NamespacedKey voucherKey;
    private final Map<UUID, String> playerParrotTypes = new HashMap<>();
    private final Map<UUID, Integer> playerParrotHunger = new HashMap<>();

    public ParrotManager(CostumePlugin plugin) {
        this.plugin = plugin;
        this.voucherKey = new NamespacedKey(plugin, "parrot_item_type");
    }

    public ItemStack createParrotItem(String type) {
        ItemStack item = new ItemStack(Material.FEATHER);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Papuga: " + ChatColor.AQUA + type.toUpperCase());
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Prawym przyciskiem myszy, aby",
                    ChatColor.GRAY + "dodac papuge do swoich zwierzakow!"
            ));
            meta.getPersistentDataContainer().set(voucherKey, PersistentDataType.STRING, type.toLowerCase());
            item.setItemMeta(meta);
        }
        return item;
    }

    public String getTypeFromParrotItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return null;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().get(voucherKey, PersistentDataType.STRING);
    }

    public String getParrotType(UUID uuid) {
        return playerParrotTypes.getOrDefault(uuid, "red");
    }

    public void setParrotType(UUID uuid, String type) {
        playerParrotTypes.put(uuid, type.toLowerCase());
    }

    public void rerollParrotStats(Player player) {
        if (player == null) return;
        String[] types = {"red", "blue", "green", "cyan", "gray", "golden"};
        String chosen = types[new Random().nextInt(types.length)];
        setParrotType(player.getUniqueId(), chosen);
        plugin.getParrotSpawnManager().spawnParrotForPlayer(player);
    }

    public void handleFeedInteraction(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack hand = player.getInventory().getItemInMainHand();

        if (hand.getType() == Material.WHEAT_SEEDS || hand.getType() == Material.MELON_SEEDS || hand.getType() == Material.PUMPKIN_SEEDS) {
            UUID uuid = player.getUniqueId();
            int current = playerParrotHunger.getOrDefault(uuid, 100);
            if (current >= 100) {
                player.sendMessage(ChatColor.YELLOW + "Twoja papuga jest juz najedzona!");
                return;
            }
            hand.setAmount(hand.getAmount() - 1);
            playerParrotHunger.put(uuid, Math.min(100, current + 25));
            player.sendMessage(ChatColor.GREEN + "Nakarmiles swoja papuge!");
        }
    }

    public void openParrotMenu(Player player) {
        Inventory inv = Bukkit.createInventory(null, 27, ChatColor.DARK_GREEN + "Zarzadzanie Papuga");

        ItemStack info = new ItemStack(Material.FEATHER);
        ItemMeta meta = info.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(ChatColor.GOLD + "Informacje o Papudze");
            meta.setLore(Arrays.asList(
                    ChatColor.GRAY + "Typ: " + ChatColor.AQUA + getParrotType(player.getUniqueId()),
                    ChatColor.GRAY + "Glod: " + ChatColor.GREEN + playerParrotHunger.getOrDefault(player.getUniqueId(), 100) + "/100"
            ));
            info.setItemMeta(meta);
        }
        inv.setItem(13, info);

        ItemStack feed = new ItemStack(Material.WHEAT_SEEDS);
        ItemMeta feedMeta = feed.getItemMeta();
        if (feedMeta != null) {
            feedMeta.setDisplayName(ChatColor.YELLOW + "Nakarm papuge");
            feedMeta.setLore(List.of(ChatColor.GRAY + "Kliknij nasionami na papuge na swiecie!"));
            feed.setItemMeta(feedMeta);
        }
        inv.setItem(11, feed);

        player.openInventory(inv);
    }
}
