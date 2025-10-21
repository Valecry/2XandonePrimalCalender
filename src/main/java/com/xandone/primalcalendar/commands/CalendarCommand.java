package com.xandone.primalcalendar.commands;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.calendar.models.CalendarDate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CalendarCommand implements CommandExecutor, TabCompleter {

    private final XandonePrimalCalendar plugin;

    public CalendarCommand(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("primalcalendar.calendar.view")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.no-permission"));
            return true;
        }

        CalendarDate currentDate = plugin.getCalendarManager().getCurrentDate();
        String formattedDate = plugin.getCalendarManager().formatDate(currentDate);
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("calendar.current-date", 
                "%date%", formattedDate));
        
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
