package pl.anacode.costume.managers;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import pl.anacode.costume.CostumePlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParrotSpawnManager {

    private final CostumePlugin plugin;
    private final NamespacedKey parrotKey;
    private final NamespacedKey ownerKey;
    private final Map<UUID, Parrot> spawnedParrots = new HashMap<>();
    private BukkitTask followTask;

    public ParrotSpawnManager(CostumePlugin plugin) {
        this.plugin = plugin;
        this.parrotKey = new NamespacedKey(plugin, "costume_parrot");
        this.ownerKey = new NamespacedKey(plugin, "parrot_owner");
        startFollowTask();
    }

    public void spawnParrotForPlayer(Player player) {
        if (player == null || !player.isOnline()) return;

        despawnParrot(player.getUniqueId());

        Location loc = player.getLocation().add(0.5, 0.5, 0.5);
        Parrot parrot = player.getWorld().spawn(loc, Parrot.class, p -> {
            p.setAdult();
            p.setTamed(true);
            p.setOwner(player);
            p.setSitting(false);
            p.setInvulnerable(true);
            p.setCustomName(ChatColor.GOLD + "Papuga gracza " + player.getName());
            p.setCustomNameVisible(true);

            String type = plugin.getParrotManager().getParrotType(player.getUniqueId());
            p.setVariant(mapTypeToVariant(type));

            p.getPersistentDataContainer().set(parrotKey, PersistentDataType.BYTE, (byte) 1);
            p.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, player.getUniqueId().toString());
        });

        spawnedParrots.put(player.getUniqueId(), parrot);
    }

    public void despawnParrot(UUID uuid) {
        Parrot parrot = spawnedParrots.remove(uuid);
        if (parrot != null && parrot.isValid()) {
            parrot.remove();
        }
    }

    public void despawnAll() {
        if (followTask != null) {
            followTask.cancel();
        }
        for (Parrot parrot : spawnedParrots.values()) {
            if (parrot != null && parrot.isValid()) {
                parrot.remove();
            }
        }
        spawnedParrots.clear();
    }

    public void teleportParrotToPlayer(Player player) {
        Parrot parrot = spawnedParrots.get(player.getUniqueId());
        if (parrot != null && parrot.isValid()) {
            parrot.teleport(player.getLocation().add(0, 1, 0));
        }
    }

    public boolean isPluginParrot(Parrot parrot) {
        if (parrot == null) return false;
        return parrot.getPersistentDataContainer().has(parrotKey, PersistentDataType.BYTE);
    }

    public boolean isOwner(Parrot parrot, Player player) {
        if (parrot == null || player == null) return false;
        String ownerStr = parrot.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
        return ownerStr != null && ownerStr.equals(player.getUniqueId().toString());
    }

    private Parrot.Variant mapTypeToVariant(String type) {
        if (type == null) return Parrot.Variant.RED;
        return switch (type.toLowerCase()) {
            case "blue" -> Parrot.Variant.BLUE;
            case "green" -> Parrot.Variant.GREEN;
            case "cyan" -> Parrot.Variant.CYAN;
            case "gray" -> Parrot.Variant.GRAY;
            default -> Parrot.Variant.RED;
        };
    }

    private void startFollowTask() {
        this.followTask = new BukkitRunnable() {
            @Override
            public void run() {
                for (Map.Entry<UUID, Parrot> entry : spawnedParrots.entrySet()) {
                    Player player = plugin.getServer().getPlayer(entry.getKey());
                    Parrot parrot = entry.getValue();

                    if (player == null || !player.isOnline() || parrot == null || !parrot.isValid()) {
                        continue;
                    }

                    if (!parrot.getWorld().equals(player.getWorld())) {
                        parrot.teleport(player.getLocation().add(0, 1, 0));
                        continue;
                    }

                    double distanceSq = parrot.getLocation().distanceSquared(player.getLocation());
                    if (distanceSq > 400.0) {
                        parrot.teleport(player.getLocation().add(0, 1, 0));
                    } else if (distanceSq > 16.0) {
                        parrot.getPathfinder().moveTo(player.getLocation(), 1.4);
                    }
                }
            }
        }.runTaskTimer(plugin, 20L, 20L);
    }
}
