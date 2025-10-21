package com.xandone.primalcalendar.commands;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.items.HolidayCoinItem;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class HolidayCommand implements CommandExecutor, TabCompleter {

    private final XandonePrimalCalendar plugin;
    private final HolidayCoinItem holidayCoinItem;

    public HolidayCommand(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
        this.holidayCoinItem = new HolidayCoinItem(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.player-only"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("primalcalendar.holiday.create")) {
            player.sendMessage(plugin.getLanguageManager().getMessage("commands.no-permission"));
            return true;
        }

        if (args.length < 3) {
            player.sendMessage("§cUsage: /holiday <name> <day> <month>");
            return true;
        }

        // Check if player has a holiday coin
        boolean hasHolidayCoin = false;
        ItemStack coinToRemove = null;

        for (ItemStack item : player.getInventory().getContents()) {
            if (holidayCoinItem.isHolidayCoin(item)) {
                hasHolidayCoin = true;
                coinToRemove = item;
                break;
            }
        }

        if (!hasHolidayCoin) {
            player.sendMessage(plugin.getLanguageManager().getMessage("holidays.no-coin"));
            return true;
        }

        String name = args[0];
        try {
            int day = Integer.parseInt(args[1]);
            String month = args[2];

            // Check if holiday already exists on that date
            boolean dateOccupied = plugin.getEventManager().getHolidays().values().stream()
                    .anyMatch(h -> h.getDay() == day && h.getMonth().equalsIgnoreCase(month));

            if (dateOccupied) {
                player.sendMessage(plugin.getLanguageManager().getMessage("holidays.already-exists"));
                return true;
            }

            // Remove one holiday coin
            if (coinToRemove.getAmount() > 1) {
                coinToRemove.setAmount(coinToRemove.getAmount() - 1);
            } else {
                player.getInventory().remove(coinToRemove);
            }

            // Create the holiday (non-recurring, player-created)
            plugin.getEventManager().addHoliday(name, day, month, false, new ArrayList<>(), 
                    "§6Today is " + name + " (created by " + player.getName() + ")!");

            player.sendMessage(plugin.getLanguageManager().getMessage("holidays.created",
                    "%name%", name,
                    "%date%", "Day " + day + " of " + month));

        } catch (NumberFormatException e) {
            player.sendMessage("§cInvalid day format!");
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 3) {
            return plugin.getCalendarManager().getMonths().stream()
                    .map(month -> month.getName())
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}
