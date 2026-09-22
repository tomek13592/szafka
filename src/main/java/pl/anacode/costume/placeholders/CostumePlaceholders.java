package pl.anacode.costume.placeholders;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.anacode.costume.CostumePlugin;

public class CostumePlaceholders extends PlaceholderExpansion {

    private final CostumePlugin plugin;

    public CostumePlaceholders(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "costume";
    }

    @Override
    public @NotNull String getAuthor() {
        return "anacode";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";

        String p = params.toLowerCase();

        if (p.equals("active") || p.equals("aktywny")) {
            String active = plugin.getCostumeManager().getActiveCostume(player.getUniqueId());
            return (active != null) ? active : "Brak";
        }

        if (p.equals("active_formatted") || p.equals("aktywny_nazwa")) {
            return plugin.getCostumeManager().getActiveCostumeDisplayName(player.getUniqueId());
        }

        if (p.equals("pet") || p.equals("zwierzak")) {
            String pet = plugin.getPetManager().getActivePet(player.getUniqueId());
            return (pet != null) ? pet : "Brak";
        }

        if (p.equals("is_glowing") || p.equals("swieci")) {
            return plugin.getGlowingManager().isGlowing(player) ? "Tak" : "Nie";
        }

        if (p.equals("parrot_type") || p.equals("papuga_typ")) {
            return plugin.getParrotManager().getParrotType(player.getUniqueId());
        }

        if (p.equals("prefix")) {
            return plugin.getNametagLineManager().getPrefix(player);
        }

        if (p.equals("belowname")) {
            return plugin.getBelowNamePreviewManager().getPreviewText(player);
        }

        return null;
    }
}
