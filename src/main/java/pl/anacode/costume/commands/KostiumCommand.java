package pl.anacode.costume.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import pl.anacode.costume.CostumePlugin;
import pl.anacode.costume.gui.ListaKostiumowGUI;
import pl.anacode.costume.gui.SzafkaGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class KostiumCommand implements CommandExecutor, TabCompleter {

    private final CostumePlugin plugin;

    public KostiumCommand(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Ta komenda moze byc uzyta tylko przez gracza!");
                return true;
            }
            SzafkaGUI.open(player, plugin);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("lista") || sub.equals("list")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Ta komenda moze byc uzyta tylko przez gracza!");
                return true;
            }
            ListaKostiumowGUI.open(player, plugin);
            return true;
        }

        if (sub.equals("reload")) {
            if (!sender.hasPermission("costume.admin")) {
                sender.sendMessage(ChatColor.RED + "Nie masz uprawnien do tej komendy!");
                return true;
            }
            plugin.reloadPlugin();
            sender.sendMessage(ChatColor.GREEN + "Konfiguracja CostumePlugin zostala przeladowana!");
            return true;
        }

        if (sub.equals("daj") || sub.equals("give")) {
            if (!sender.hasPermission("costume.admin")) {
                sender.sendMessage(ChatColor.RED + "Nie masz uprawnien do tej komendy!");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Uzycie: /kostium daj <gracz> <id_kostiumu>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza: " + args[1]);
                return true;
            }

            String costumeId = args[2].toLowerCase();
            ItemStack item = plugin.getCostumeManager().createCostumeItem(costumeId);
            if (item == null) {
                sender.sendMessage(ChatColor.RED + "Nie znaleziono kostiumu o ID: " + costumeId);
                return true;
            }

            target.getInventory().addItem(item);
            sender.sendMessage(ChatColor.GREEN + "Nadano kostium " + costumeId + " graczowi " + target.getName());
            target.sendMessage(ChatColor.GREEN + "Otrzymales kostium: " + costumeId);
            return true;
        }

        if (sub.equals("zdejmij") || sub.equals("remove")) {
            if (args.length >= 2 && sender.hasPermission("costume.admin")) {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + "Nie znaleziono gracza: " + args[1]);
                    return true;
                }
                plugin.getCostumeManager().unequipCostume(target);
                sender.sendMessage(ChatColor.GREEN + "Zdjeto kostium graczowi " + target.getName());
                return true;
            }

            if (sender instanceof Player player) {
                plugin.getCostumeManager().unequipCostume(player);
                player.sendMessage(ChatColor.GREEN + "Zdjeto aktywny kostium!");
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Nieznany argument. Uzyj: /kostium [lista|daj|zdejmij|reload]");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(Arrays.asList("lista", "szafka", "zdejmij"));
            if (sender.hasPermission("costume.admin")) {
                completions.add("reload");
                completions.add("daj");
            }
            return completions.stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("daj") && sender.hasPermission("costume.admin")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("daj") && sender.hasPermission("costume.admin")) {
            return plugin.getCostumeManager().getAllCostumeIds().stream()
                    .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}
