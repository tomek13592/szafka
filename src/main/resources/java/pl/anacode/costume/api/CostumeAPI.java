package pl.anacode.costume.api;

import org.bukkit.entity.Player;
import pl.anacode.costume.CostumePlugin;

public final class CostumeAPI {

    private CostumeAPI() {}

    public static String getActiveCostume(Player player) {
        CostumePlugin plugin = CostumePlugin.getInstance();
        if (plugin == null || player == null) return null;
        return plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
    }

    public static boolean hasCostumeEquipped(Player player, String costumeId) {
        if (player == null || costumeId == null) return false;
        String active = getActiveCostume(player);
        return costumeId.equalsIgnoreCase(active);
    }

    public static void setCostume(Player player, String costumeId) {
        CostumePlugin plugin = CostumePlugin.getInstance();
        if (plugin == null || player == null) return;
        plugin.getCostumeManager().equipCostume(player, costumeId);
    }

    public static void removeCostume(Player player) {
        CostumePlugin plugin = CostumePlugin.getInstance();
        if (plugin == null || player == null) return;
        plugin.getCostumeManager().unequipCostume(player);
    }

    public static boolean isGlowing(Player player) {
        CostumePlugin plugin = CostumePlugin.getInstance();
        if (plugin == null || player == null) return false;
        return plugin.getGlowingManager().isGlowing(player);
    }

    public static String getActivePet(Player player) {
        CostumePlugin plugin = CostumePlugin.getInstance();
        if (plugin == null || player == null) return null;
        return plugin.getPetManager().getActivePet(player.getUniqueId());
    }
}
