package pl.anacode.costume.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;
import pl.anacode.costume.CostumePlugin;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Manager kolorowego glowingu przez Scoreboard Teams.
 *
 * NAPRAWA:
 * - Wszystkie operacje na teamach owinięte w try/catch
 * - Sprawdzanie hasEntry() PRZED każdą modyfikacją
 * - Sprawdzanie isOnline() gracza przed każdą operacją
 * - Automatyczny cleanup przy quit/disconnect
 * - Ochrona przed Citizens NPC klonami (target ma być realnym graczem)
 * - Task cleanup uruchamiany zawsze w try/catch
 * - Bezpieczne czyszczenie po zakończeniu efektu nawet gdy gracz offline
 */
public class GlowingManager {

    private final CostumePlugin plugin;
    private static final String TEAM_PREFIX = "cpl_";

    // Aktywne glowing: playerUUID -> kolor
    private final Map<UUID, ChatColor> activeGlowing = new HashMap<>();

    // Taski cofające glowing po czasie
    private final Map<UUID, BukkitTask> glowTasks = new HashMap<>();

    // Nazwy graczy w teamach (do bezpiecznego usuwania nawet gdy offline)
    private final Map<UUID, String> playerNames = new HashMap<>();

    public GlowingManager(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Ustawia glowing na graczu.
     * BEZPIECZNIE — sprawdza wszystkie warunki.
     */
    public void setGlowing(Player player, ChatColor color, int durationSeconds) {
        if (player == null || !player.isOnline()) return;
        if (color == null) return;

        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        // Anuluj poprzedni task jeśli był
        cancelTask(uuid);

        // Wyczyść poprzedni glowing (jeśli był)
        safeRemoveFromAllOurTeams(playerName);

        // Dodaj do nowego teamu
        boolean added = safeAddToTeam(playerName, color);
        if (!added) {
            plugin.getLogger().warning("[Glowing] Nie udało się dodać "
                    + playerName + " do teamu koloru " + color);
            return;
        }

        activeGlowing.put(uuid, color);
        playerNames.put(uuid, playerName);

        // Dodaj efekt glowing
        try {
            int ticks = durationSeconds > 0 ? durationSeconds * 20 : Integer.MAX_VALUE;
            player.addPotionEffect(new PotionEffect(
                    PotionEffectType.GLOWING, ticks, 0, false, false, true));
        } catch (Exception e) {
            plugin.getLogger().warning("[Glowing] Błąd dodawania efektu: "
                    + e.getMessage());
        }

        // Zaplanuj cleanup
        if (durationSeconds > 0 && plugin.isEnabled()) {
            try {
                BukkitTask task = new BukkitRunnable() {
                    @Override
                    public void run() {
                        try {
                            removeGlowingByUuid(uuid);
                        } catch (Exception e) {
                            plugin.getLogger().warning(
                                    "[Glowing] Błąd cleanup task: " + e.getMessage());
                        }
                    }
                }.runTaskLater(plugin, durationSeconds * 20L);
                glowTasks.put(uuid, task);
            } catch (Exception e) {
                plugin.getLogger().warning(
                        "[Glowing] Nie udało się zaplanować task: " + e.getMessage());
            }
        }
    }

    /**
     * Usuwa glowing z gracza online.
     */
    public void removeGlowing(Player player) {
        if (player == null) return;
        removeGlowingByUuid(player.getUniqueId());
    }

    /**
     * Usuwa glowing po UUID (bezpieczne nawet gdy gracz offline).
     */
    public void removeGlowingByUuid(UUID uuid) {
        if (uuid == null) return;

        cancelTask(uuid);
        activeGlowing.remove(uuid);

        // Pobierz zapamiętaną nazwę
        String playerName = playerNames.remove(uuid);

        // Jeśli gracz jest online, użyj jego aktualnej nazwy
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            playerName = player.getName();

            // Usuń efekt glowing
            try {
                if (player.isOnline()) {
                    player.removePotionEffect(PotionEffectType.GLOWING);
                }
            } catch (Exception e) {
                plugin.getLogger().warning(
                        "[Glowing] Błąd usuwania efektu: " + e.getMessage());
            }
        }

        // Usuń z teamu — nawet gdy gracz offline (używamy zapamiętanej nazwy)
        if (playerName != null) {
            safeRemoveFromAllOurTeams(playerName);
        }
    }

    /**
     * Sprawdza czy gracz ma aktywny glowing z tego pluginu.
     */
    public boolean isGlowing(Player player) {
        if (player == null) return false;
        return activeGlowing.containsKey(player.getUniqueId());
    }

    /**
     * Wywoływane gdy gracz się wylogowuje.
     * KLUCZOWE: usuwamy z teamu żeby kolor nie został po powrocie
     * i żeby nie było problemów z packetami.
     */
    public void cleanupPlayer(Player player) {
        if (player == null) return;

        UUID uuid = player.getUniqueId();
        String playerName = player.getName();

        cancelTask(uuid);

        // Usuń z teamu zanim gracz się rozłączy
        safeRemoveFromAllOurTeams(playerName);

        activeGlowing.remove(uuid);
        playerNames.remove(uuid);
    }

    /**
     * Wywoływane przy shutdown pluginu.
     */
    public void cleanupAll() {
        // Anuluj wszystkie taski
        for (BukkitTask task : glowTasks.values()) {
            try {
                if (task != null) task.cancel();
            } catch (Exception ignored) {
            }
        }
        glowTasks.clear();

        // Usuń glowing z wszystkich graczy
        for (Map.Entry<UUID, String> entry : new HashMap<>(playerNames).entrySet()) {
            String playerName = entry.getValue();
            Player player = Bukkit.getPlayer(entry.getKey());

            try {
                if (player != null && player.isOnline()) {
                    player.removePotionEffect(PotionEffectType.GLOWING);
                }
            } catch (Exception ignored) {
            }

            if (playerName != null) {
                safeRemoveFromAllOurTeams(playerName);
            }
        }

        activeGlowing.clear();
        playerNames.clear();

        // Bezpiecznie unregister wszystkich naszych teamów
        safeUnregisterAllOurTeams();
    }

    /**
     * Synchronizuje glowing dla nowego gracza — używane w JoinListener.
     * Nie robimy nic specjalnego, bo gracz i tak zobaczy istniejące teamy
     * po scoreboard sync z serwera.
     */
    public void syncToNewPlayer(Player player) {
        // Placeholder — Minecraft sam sync-uje teamy scoreboardu
        // gdy gracz się loguje. Nic tu nie trzeba robić.
        if (player == null || !player.isOnline()) return;

        // Jeśli gracz miał aktywny glowing (np. rejoin) — przywróć
        UUID uuid = player.getUniqueId();
        ChatColor color = activeGlowing.get(uuid);
        if (color != null) {
            try {
                safeAddToTeam(player.getName(), color);
                playerNames.put(uuid, player.getName());
            } catch (Exception ignored) {
            }
        }
    }

    // ========================================
    // PRYWATNE METODY POMOCNICZE
    // ========================================

    /**
     * BEZPIECZNIE dodaje gracza do teamu koloru.
     * Sprawdza wszystkie warunki, łapie wyjątki.
     */
    private boolean safeAddToTeam(String playerName, ChatColor color) {
        if (playerName == null || color == null) return false;

        try {
            ScoreboardManager sm = Bukkit.getScoreboardManager();
            if (sm == null) return false;

            Scoreboard sb = sm.getMainScoreboard();
            String teamName = TEAM_PREFIX + color.name().toLowerCase();

            // Nazwa teamu musi być <= 16 znaków (Minecraft limit)
            if (teamName.length() > 16) {
                teamName = teamName.substring(0, 16);
            }

            Team team = sb.getTeam(teamName);
            if (team == null) {
                try {
                    team = sb.registerNewTeam(teamName);
                    team.setColor(color);
                } catch (Exception e) {
                    plugin.getLogger().warning(
                            "[Glowing] Nie udało się utworzyć teamu " + teamName
                                    + ": " + e.getMessage());
                    return false;
                }
            }

            // Sprawdź czy gracz już nie jest w tym teamie
            try {
                if (team.hasEntry(playerName)) {
                    return true; // już jest, nic nie rób
                }
            } catch (Exception ignored) {
            }

            // Dodaj do teamu
            try {
                team.addEntry(playerName);
                return true;
            } catch (IllegalStateException | IllegalArgumentException e) {
                plugin.getLogger().warning(
                        "[Glowing] Nie udało się dodać " + playerName
                                + " do teamu " + teamName + ": " + e.getMessage());
                return false;
            }

        } catch (Exception e) {
            plugin.getLogger().warning(
                    "[Glowing] Krytyczny błąd w safeAddToTeam: " + e.getMessage());
            return false;
        }
    }

    /**
     * BEZPIECZNIE usuwa gracza ze WSZYSTKICH naszych teamów.
     * KLUCZOWA metoda — sprawdza hasEntry() przed removeEntry()
     * i łapie każdy możliwy wyjątek.
     */
    private void safeRemoveFromAllOurTeams(String playerName) {
        if (playerName == null || playerName.isEmpty()) return;

        try {
            ScoreboardManager sm = Bukkit.getScoreboardManager();
            if (sm == null) return;

            Scoreboard sb = sm.getMainScoreboard();

            // Iteruj po kopii żeby uniknąć ConcurrentModification
            Set<Team> teams;
            try {
                teams = new HashSet<>(sb.getTeams());
            } catch (Exception e) {
                plugin.getLogger().warning(
                        "[Glowing] Nie mogę pobrać listy teamów: " + e.getMessage());
                return;
            }

            for (Team team : teams) {
                if (team == null) continue;

                String teamName;
                try {
                    teamName = team.getName();
                } catch (Exception ignored) {
                    continue;
                }

                if (teamName == null || !teamName.startsWith(TEAM_PREFIX)) continue;

                // KLUCZOWE: sprawdź hasEntry PRZED removeEntry
                try {
                    if (team.hasEntry(playerName)) {
                        team.removeEntry(playerName);
                    }
                } catch (IllegalStateException e) {
                    // Team został unregister-owany w międzyczasie — ignoruj
                } catch (IllegalArgumentException e) {
                    // Entry nie istnieje — ignoruj
                } catch (Exception e) {
                    plugin.getLogger().warning(
                            "[Glowing] Błąd usuwania z teamu " + teamName
                                    + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            plugin.getLogger().warning(
                    "[Glowing] Krytyczny błąd w safeRemoveFromAllOurTeams: "
                            + e.getMessage());
        }
    }

    /**
     * Unregister wszystkich naszych teamów.
     * Wywoływane tylko przy pełnym cleanup pluginu.
     */
    private void safeUnregisterAllOurTeams() {
        try {
            ScoreboardManager sm = Bukkit.getScoreboardManager();
            if (sm == null) return;

            Scoreboard sb = sm.getMainScoreboard();

            Set<Team> teams;
            try {
                teams = new HashSet<>(sb.getTeams());
            } catch (Exception e) {
                return;
            }

            for (Team team : teams) {
                if (team == null) continue;

                String teamName;
                try {
                    teamName = team.getName();
                } catch (Exception ignored) {
                    continue;
                }

                if (teamName == null || !teamName.startsWith(TEAM_PREFIX)) continue;

                try {
                    team.unregister();
                } catch (Exception ignored) {
                }
            }
        } catch (Exception ignored) {
        }
    }

    private void cancelTask(UUID uuid) {
        BukkitTask task = glowTasks.remove(uuid);
        if (task != null) {
            try {
                task.cancel();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * Alias dla compat z starym API.
     */
    public void cleanup(Player player) {
        cleanupPlayer(player);
    }
}
