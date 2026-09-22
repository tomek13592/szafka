package pl.anacode.costume.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import pl.anacode.costume.CostumePlugin;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class GlowingManager {

    private final CostumePlugin plugin;
    private static final String TEAM_PREFIX = "cpl_";

    // UUID -> ID zadania wygaszającego
    private final Map<UUID, BukkitTask> glowingTasks = new ConcurrentHashMap<>();
    // UUID -> Nazwa gracza (zapisana jako String na wypadek gdyby gracz zniknął/wyszedł/był ukryty)
    private final Map<UUID, String> playerNames = new ConcurrentHashMap<>();
    // UUID -> ChatColor aktualnego glowingu
    private final Map<UUID, ChatColor> activeColors = new ConcurrentHashMap<>();

    public GlowingManager(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Nadaje graczowi efekt Glowingu z określonym kolorem na dany czas (w sekundach).
     * W pełni bezpieczne w przypadku klonów Citizens, ukrywania gracza oraz nagłych rozłączeń.
     */
    public void setGlowing(Player player, ChatColor color, int durationSeconds) {
        if (player == null) return;

        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        // 1. Zapisujemy nazwę gracza i kolor w pamięci
        playerNames.put(uuid, playerName);
        activeColors.put(uuid, color);

        // 2. Anulujemy poprzedni task wygaszający jeśli istniał
        BukkitTask previousTask = glowingTasks.remove(uuid);
        if (previousTask != null) {
            try {
                previousTask.cancel();
            } catch (Throwable ignored) {}
        }

        // 3. Wykonujemy operację na głównym wątku serwera
        runSync(() -> {
            try {
                Scoreboard sb = getScoreboard();
                if (sb == null) return;

                // Bezpiecznie usuwamy gracza ze starych drużyn tego pluginu
                safeRemoveFromAllOurTeams(sb, playerName);

                // Pobieramy lub tworzymy team dla danego koloru
                String teamName = getTeamNameForColor(color);
                Team team = sb.getTeam(teamName);
                if (team == null) {
                    team = sb.registerNewTeam(teamName);
                }
                team.setColor(color);
                team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);

                if (!team.hasEntry(playerName)) {
                    team.addEntry(playerName);
                }

                // Dodajemy efekt potki tylko jeśli gracz jest w pełni online i zespawnowany
                if (player.isOnline() && player.isValid()) {
                    player.addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, durationSeconds * 20, 0, false, false, true));
                }
            } catch (Throwable t) {
                plugin.getLogger().warning("Niegrozny blad podczas nadawania glowingu: " + t.getMessage());
            }
        });

        // 4. Planujemy automatyczne i bezpieczne usunięcie efektu po czasie
        if (durationSeconds > 0 && plugin.isEnabled()) {
            BukkitTask task = Bukkit.getScheduler().runTaskLater(plugin, () -> {
                // Usuwamy efekt niezależnie od tego, czy gracz jest online, ukryty przez Citizens czy na innym świecie
                removeGlowingInternal(uuid, playerName);
            }, durationSeconds * 20L);

            glowingTasks.put(uuid, task);
        }
    }

    /**
     * Bezpiecznie zdejmuje glowing z gracza.
     */
    public void removeGlowing(Player player) {
        if (player == null) return;
        removeGlowingInternal(player.getUniqueId(), player.getName());
    }

    /**
     * Zdejmuje glowing po samym UUID (np. gdy gracz jest offline lub sklonowany).
     */
    public void removeGlowingByUuid(UUID uuid) {
        if (uuid == null) return;
        String name = playerNames.get(uuid);
        removeGlowingInternal(uuid, name);
    }

    /**
     * Wewnętrzna metoda czyszcząca – całkowicie odporna na nulle, brak entity i błędy Netty.
     */
    private void removeGlowingInternal(UUID uuid, String playerName) {
        // Anulujemy task
        BukkitTask task = glowingTasks.remove(uuid);
        if (task != null) {
            try {
                task.cancel();
            } catch (Throwable ignored) {}
        }

        activeColors.remove(uuid);
        if (playerName == null) {
            playerName = playerNames.get(uuid);
        }
        playerNames.remove(uuid);

        final String finalName = playerName;

        runSync(() -> {
            try {
                // 1. Czyszczenie ze scoreboardu po nazwie String (działa nawet jak gracz jest despawnowany przez Citizens)
                Scoreboard sb = getScoreboard();
                if (sb != null && finalName != null && !finalName.isEmpty()) {
                    safeRemoveFromAllOurTeams(sb, finalName);
                }

                // 2. Jeśli gracz jest fizycznie online – zdejmij z niego efekt potki
                Player p = Bukkit.getPlayer(uuid);
                if (p != null && p.isOnline() && p.isValid()) {
                    try {
                        p.removePotionEffect(PotionEffectType.GLOWING);
                    } catch (Throwable ignored) {}
                }
            } catch (Throwable t) {
                // Ignorujemy błędy pakietowe, aby nigdy nie wywalić gracza z serwera
            }
        });
    }

    /**
     * Sprawdza, czy gracz aktualnie posiada aktywny glowing z pluginu.
     */
    public boolean isGlowing(Player player) {
        if (player == null) return false;
        return glowingTasks.containsKey(player.getUniqueId()) || activeColors.containsKey(player.getUniqueId());
    }

    /**
     * Wywoływane przy PlayerQuitEvent – natychmiast czyści ślady, by po ponownym wejściu nie było bugów z kolorem.
     */
    public void cleanupPlayer(Player player) {
        if (player == null) return;
        removeGlowingInternal(player.getUniqueId(), player.getName());
    }

    public void cleanup(Player player) {
        cleanupPlayer(player);
    }

    /**
     * Synchronizacja dla nowo wchodzącego gracza lub po respawnie/reappear klona Citizens.
     */
    public void syncToNewPlayer(Player player) {
        if (player == null || !player.isOnline()) return;

        runSync(() -> {
            try {
                Scoreboard sb = getScoreboard();
                if (sb == null) return;

                String name = player.getName();
                // Jeśli gracz NIE powinien się świecić, upewnijmy się, że nie został w żadnym starym teamie cpl_
                if (!isGlowing(player)) {
                    safeRemoveFromAllOurTeams(sb, name);
                }
            } catch (Throwable ignored) {}
        });
    }

    /**
     * Czyści wszystkie teamy i efekty przy wyłączaniu pluginu (onDisable).
     */
    public void cleanupAll() {
        for (BukkitTask task : glowingTasks.values()) {
            if (task != null) {
                try {
                    task.cancel();
                } catch (Throwable ignored) {}
            }
        }
        glowingTasks.clear();
        activeColors.clear();
        playerNames.clear();

        try {
            Scoreboard sb = getScoreboard();
            if (sb != null) {
                safeUnregisterAllOurTeams(sb);
            }
        } catch (Throwable ignored) {}
    }

    // ==========================================
    // Metody pomocnicze - maksymalne bezpieczeństwo
    // ==========================================

    private Scoreboard getScoreboard() {
        try {
            var manager = Bukkit.getScoreboardManager();
            return (manager != null) ? manager.getMainScoreboard() : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private String getTeamNameForColor(ChatColor color) {
        String name = TEAM_PREFIX + color.name().toLowerCase();
        if (name.length() > 16) {
            name = name.substring(0, 16);
        }
        return name;
    }

    private void safeRemoveFromAllOurTeams(Scoreboard sb, String playerName) {
        if (sb == null || playerName == null || playerName.isEmpty()) return;
        try {
            for (Team team : new HashSet<>(sb.getTeams())) {
                if (team.getName().startsWith(TEAM_PREFIX)) {
                    try {
                        if (team.hasEntry(playerName)) {
                            team.removeEntry(playerName);
                        }
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
    }

    private void safeUnregisterAllOurTeams(Scoreboard sb) {
        if (sb == null) return;
        try {
            for (Team team : new HashSet<>(sb.getTeams())) {
                if (team.getName().startsWith(TEAM_PREFIX)) {
                    try {
                        team.unregister();
                    } catch (Throwable ignored) {}
                }
            }
        } catch (Throwable ignored) {}
    }

    private void runSync(Runnable runnable) {
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        } else if (plugin.isEnabled()) {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }
}
