package com.xandone.primalcalendar.hooks;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.calendar.models.CalendarDate;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class PlaceholderAPIHook extends PlaceholderExpansion {

    private final XandonePrimalCalendar plugin;

    public PlaceholderAPIHook(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "primalcalendar";
    }

    @Override
    public @NotNull String getAuthor() {
        return "Xandone";
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
    public String onRequest(OfflinePlayer player, @NotNull String params) {
        CalendarDate currentDate = plugin.getCalendarManager().getCurrentDate();

        // Basic date placeholders
        if (params.equalsIgnoreCase("day")) {
            return String.valueOf(currentDate.getDay());
        }

        if (params.equalsIgnoreCase("month")) {
            return currentDate.getMonth().getName();
        }

        if (params.equalsIgnoreCase("month_number")) {
            return String.valueOf(currentDate.getMonth().getNumber());
        }

        if (params.equalsIgnoreCase("year")) {
            return currentDate.getFormattedYear();
        }

        if (params.equalsIgnoreCase("era")) {
            return currentDate.getEra();
        }

        if (params.equalsIgnoreCase("date")) {
            return plugin.getCalendarManager().formatDate(currentDate);
        }

        if (params.equalsIgnoreCase("date_short")) {
            return currentDate.getDay() + "/" + currentDate.getMonth().getNumber() + "/" + currentDate.getYear();
        }

        // Month information
        if (params.equalsIgnoreCase("month_days")) {
            return String.valueOf(currentDate.getMonth().getDays());
        }

        if (params.equalsIgnoreCase("days_in_year")) {
            return String.valueOf(plugin.getCalendarManager().getTotalDaysInYear());
        }

        if (params.equalsIgnoreCase("total_months")) {
            return String.valueOf(plugin.getCalendarManager().getMonths().size());
        }

        // Player-specific placeholders
        if (player != null) {
            if (params.equalsIgnoreCase("birthday")) {
                String month = plugin.getDataManager().getBirthdayMonth(player.getUniqueId());
                if (month == null) {
                    return "Not Set";
                }
                int day = plugin.getDataManager().getBirthday(player.getUniqueId()).get("day");
                return "Day " + day + " of " + month;
            }

            if (params.equalsIgnoreCase("birthday_day")) {
                if (plugin.getDataManager().getBirthday(player.getUniqueId()) == null) {
                    return "0";
                }
                return String.valueOf(plugin.getDataManager().getBirthday(player.getUniqueId()).get("day"));
            }

            if (params.equalsIgnoreCase("birthday_month")) {
                String month = plugin.getDataManager().getBirthdayMonth(player.getUniqueId());
                return month != null ? month : "None";
            }

            if (params.equalsIgnoreCase("holiday_coins")) {
                return String.valueOf(plugin.getDataManager().getHolidayCoins(player.getUniqueId()));
            }
        }

        // Holiday/Event counts
        if (params.equalsIgnoreCase("holidays_count")) {
            return String.valueOf(plugin.getEventManager().getHolidays().size());
        }

        if (params.equalsIgnoreCase("events_count")) {
            return String.valueOf(plugin.getEventManager().getEvents().size());
        }

        // Calendar mode
        if (params.equalsIgnoreCase("mode")) {
            if (plugin.getConfigManager().isRealTimeSync()) {
                return "Real-Time";
            } else if (plugin.getConfigManager().isServerTimeCalendar()) {
                return "Server-Time";
            }
            return "Unknown";
        }

        return null;
    }
}
