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
import pl.anacode.costume.gui.PetyGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ParrotCommand implements CommandExecutor, TabCompleter {

    private final CostumePlugin plugin;

    public ParrotCommand(CostumePlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage(ChatColor.RED + "Ta komenda moze byc uzyta tylko przez gracza!");
                return true;
            }
            PetyGUI.open(player, plugin);
            return true;
        }

        String sub = args[0].toLowerCase();

        if (sub.equals("daj") || sub.equals("give")) {
            if (!sender.hasPermission("costume.admin")) {
                sender.sendMessage(ChatColor.RED + "Nie masz uprawnien!");
                return true;
            }

            if (args.length < 3) {
                sender.sendMessage(ChatColor.RED + "Uzycie: /papuga daj <gracz> <typ/id>");
                return true;
            }

            Player target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Gracz jest offline!");
                return true;
            }

            String type = args[2];
            ItemStack item = plugin.getParrotManager().createParrotItem(type);
            if (item == null) {
                sender.sendMessage(ChatColor.RED + "Nieprawidlowy typ papugi!");
                return true;
            }

            target.getInventory().addItem(item);
            sender.sendMessage(ChatColor.GREEN + "Nadano papuge (" + type + ") graczowi " + target.getName());
            return true;
        }

        if (sub.equals("reroll")) {
            if (!sender.hasPermission("costume.admin")) {
                sender.sendMessage(ChatColor.RED + "Nie masz uprawnien!");
                return true;
            }

            Player target = (args.length >= 2) ? Bukkit.getPlayer(args[1]) : (sender instanceof Player ? (Player) sender : null);
            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Podaj gracza!");
                return true;
            }

            plugin.getParrotManager().rerollParrotStats(target);
            sender.sendMessage(ChatColor.GREEN + "Wylosowano nowe statystyki papugi dla " + target.getName());
            return true;
        }

        if (sub.equals("menu") || sub.equals("gui")) {
            if (sender instanceof Player player) {
                PetyGUI.open(player, plugin);
                return true;
            }
        }

        sender.sendMessage(ChatColor.RED + "Uzycie: /papuga [menu|daj|reroll]");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (args.length == 1) {
            List<String> list = new ArrayList<>(List.of("menu"));
            if (sender.hasPermission("costume.admin")) {
                list.addAll(Arrays.asList("daj", "reroll"));
            }
            return list.stream().filter(s -> s.startsWith(args[0].toLowerCase())).collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("daj") && sender.hasPermission("costume.admin")) {
            return Bukkit.getOnlinePlayers().stream().map(Player::getName)
                    .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase())).collect(Collectors.toList());
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("daj") && sender.hasPermission("costume.admin")) {
            return Arrays.asList("red", "blue", "green", "cyan", "gray", "golden");
        }

        return List.of();
    }
}
