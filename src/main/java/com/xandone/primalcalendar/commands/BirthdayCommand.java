package com.xandone.primalcalendar.commands;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BirthdayCommand implements CommandExecutor, TabCompleter {

    private final XandonePrimalCalendar plugin;

    public BirthdayCommand(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("primalcalendar.birthday")) {
            player.sendMessage(plugin.getLanguageManager().getMessage("commands.no-permission"));
            return true;
        }

        if (args.length == 0) {
            // View birthday
            String month = plugin.getDataManager().getBirthdayMonth(player.getUniqueId());
            if (month == null) {
                player.sendMessage("§cYou haven't set your birthday yet! Use /birthday set <day> <month>");
                return true;
            }

            int day = plugin.getDataManager().getBirthday(player.getUniqueId()).get("day");
            player.sendMessage(plugin.getLanguageManager().getMessage("birthdays.view",
                    "%date%", "Day " + day + " of " + month));
            return true;
        }

        if (args[0].equalsIgnoreCase("set")) {
            if (plugin.getDataManager().isBirthdayLocked(player.getUniqueId())) {
                player.sendMessage(plugin.getLanguageManager().getMessage("birthdays.locked"));
                return true;
            }

            if (args.length < 3) {
                player.sendMessage("§cUsage: /birthday set <day> <month>");
                return true;
            }

            try {
                int day = Integer.parseInt(args[1]);
                String month = args[2];

                // Validate month
                if (plugin.getCalendarManager().getMonthByName(month) == null) {
                    player.sendMessage("§cInvalid month name!");
                    return true;
                }

                plugin.getDataManager().setBirthday(player.getUniqueId(), day, month);
                player.sendMessage(plugin.getLanguageManager().getMessage("birthdays.set",
                        "%date%", "Day " + day + " of " + month));

            } catch (NumberFormatException e) {
                player.sendMessage("§cInvalid day format!");
            }

            return true;
        }

        player.sendMessage("§cUsage: /birthday [set <day> <month>]");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("set");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            return plugin.getCalendarManager().getMonths().stream()
                    .map(month -> month.getName())
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
