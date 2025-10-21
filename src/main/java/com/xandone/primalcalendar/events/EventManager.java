package com.xandone.primalcalendar.events;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.api.events.CalendarEventTriggerEvent;
import com.xandone.primalcalendar.api.events.HolidayTriggerEvent;
import com.xandone.primalcalendar.api.events.PlayerBirthdayEvent;
import com.xandone.primalcalendar.calendar.models.CalendarDate;
import com.xandone.primalcalendar.events.models.Holiday;
import com.xandone.primalcalendar.events.models.CalendarEvent;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.*;

public class EventManager {

    private final XandonePrimalCalendar plugin;
    private final Map<String, Holiday> holidays;
    private final Map<String, CalendarEvent> events;
    private final Set<String> triggeredToday;

    public EventManager(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
        this.holidays = new HashMap<>();
        this.events = new HashMap<>();
        this.triggeredToday = new HashSet<>();
        loadHolidays();
        loadEvents();
    }

    public void loadHolidays() {
        holidays.clear();
        if (plugin.getConfigManager().getHolidaysConfig() == null) {
            plugin.getLogger().warning("Holidays config is not loaded yet!");
            return;
        }
        
        ConfigurationSection holidaysSection = plugin.getConfigManager().getHolidaysConfig().getConfigurationSection("holidays");
        
        if (holidaysSection == null) {
            return;
        }

        for (String key : holidaysSection.getKeys(false)) {
            String path = "holidays." + key;
            int day = plugin.getConfigManager().getHolidaysConfig().getInt(path + ".day");
            String month = plugin.getConfigManager().getHolidaysConfig().getString(path + ".month");
            boolean recurring = plugin.getConfigManager().getHolidaysConfig().getBoolean(path + ".recurring", true);
            List<String> commands = plugin.getConfigManager().getHolidaysConfig().getStringList(path + ".commands");
            String message = plugin.getConfigManager().getHolidaysConfig().getString(path + ".message", "");

            Holiday holiday = new Holiday(key, day, month, recurring, commands, message);
            holidays.put(key, holiday);
        }

        plugin.getLogger().info("Loaded " + holidays.size() + " holidays");
    }

    public void loadEvents() {
        events.clear();
        if (plugin.getConfigManager().getEventsConfig() == null) {
            plugin.getLogger().warning("Events config is not loaded yet!");
            return;
        }
        
        ConfigurationSection eventsSection = plugin.getConfigManager().getEventsConfig().getConfigurationSection("events");
        
        if (eventsSection == null) {
            return;
        }

        for (String key : eventsSection.getKeys(false)) {
            String path = "events." + key;
            int day = plugin.getConfigManager().getEventsConfig().getInt(path + ".day");
            String month = plugin.getConfigManager().getEventsConfig().getString(path + ".month");
            Integer year = plugin.getConfigManager().getEventsConfig().contains(path + ".year") 
                    ? plugin.getConfigManager().getEventsConfig().getInt(path + ".year") 
                    : null;
            boolean recurring = plugin.getConfigManager().getEventsConfig().getBoolean(path + ".recurring", false);
            List<String> commands = plugin.getConfigManager().getEventsConfig().getStringList(path + ".commands");
            String message = plugin.getConfigManager().getEventsConfig().getString(path + ".message", "");

            CalendarEvent event = new CalendarEvent(key, day, month, year, recurring, commands, message);
            events.put(key, event);
        }

        plugin.getLogger().info("Loaded " + events.size() + " events");
    }

    public void checkDailyEvents(CalendarDate date) {
        String dateKey = date.getDay() + "-" + date.getMonth().getName() + "-" + date.getYear();
        
        // Reset triggered events for new day
        triggeredToday.clear();

        // Check holidays
        for (Holiday holiday : holidays.values()) {
            if (holiday.matches(date) && !triggeredToday.contains(holiday.getName())) {
                triggerHoliday(holiday, date);
                triggeredToday.add(holiday.getName());
            }
        }

        // Check events
        for (CalendarEvent event : events.values()) {
            if (event.matches(date) && !triggeredToday.contains(event.getName())) {
                triggerEvent(event, date);
                triggeredToday.add(event.getName());
                
                // Remove one-time events after triggering
                if (!event.isRecurring()) {
                    removeEvent(event.getName());
                }
            }
        }

        // Check birthdays
        checkBirthdays(date);
    }

    private void triggerHoliday(Holiday holiday, CalendarDate date) {
        HolidayTriggerEvent event = new HolidayTriggerEvent(holiday, date);
        Bukkit.getPluginManager().callEvent(event);
        
        if (event.isCancelled()) {
            return;
        }
        
        // Broadcast message
        if (!holiday.getMessage().isEmpty()) {
            String message = plugin.getLanguageManager().getMessage("holidays.announced", 
                    "%name%", holiday.getName());
            Bukkit.broadcastMessage(message);
        }

        // Execute commands
        for (String command : holiday.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        // Discord notification
        if (plugin.getConfigManager().isDiscordEnabled() && plugin.getConfigManager().shouldAnnounceHolidays()) {
            plugin.getDiscordWebhook().sendHolidayAnnouncement(holiday, date);
        }

        plugin.getLogger().info("Triggered holiday: " + holiday.getName());
    }

    private void triggerEvent(CalendarEvent event, CalendarDate date) {
        CalendarEventTriggerEvent triggerEvent = new CalendarEventTriggerEvent(event, date);
        Bukkit.getPluginManager().callEvent(triggerEvent);
        
        if (triggerEvent.isCancelled()) {
            return;
        }
        
        // Broadcast message
        if (!event.getMessage().isEmpty()) {
            Bukkit.broadcastMessage(event.getMessage());
        }

        // Execute commands
        for (String command : event.getCommands()) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

        // Discord notification
        if (plugin.getConfigManager().isDiscordEnabled() && plugin.getConfigManager().shouldAnnounceEvents()) {
            plugin.getDiscordWebhook().sendEventAnnouncement(event, date);
        }

        plugin.getLogger().info("Triggered event: " + event.getName());
    }

    private void checkBirthdays(CalendarDate date) {
        if (!plugin.getConfigManager().areBirthdaysEnabled()) {
            return;
        }

        List<UUID> birthdayPlayers = plugin.getDataManager().getPlayersWithBirthday(
                date.getDay(), date.getMonth().getName());

        for (UUID playerId : birthdayPlayers) {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerId);
            String playerName = offlinePlayer.getName() != null ? offlinePlayer.getName() : "Unknown";

            if (offlinePlayer.isOnline()) {
                Player player = offlinePlayer.getPlayer();
                PlayerBirthdayEvent birthdayEvent = new PlayerBirthdayEvent(player, date);
                Bukkit.getPluginManager().callEvent(birthdayEvent);
                
                if (birthdayEvent.isCancelled()) {
                    continue;
                }
            }

            // Broadcast birthday message
            String message = plugin.getLanguageManager().getMessage("birthdays.celebration",
                    "%player%", playerName);
            Bukkit.broadcastMessage(message);

            // Execute birthday rewards
            for (String command : plugin.getConfigManager().getBirthdayRewards()) {
                String processedCommand = command.replace("%player%", playerName);
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), processedCommand);
            }

            // Discord notification
            if (plugin.getConfigManager().isDiscordEnabled()) {
                plugin.getDiscordWebhook().sendBirthdayAnnouncement(playerName, date);
            }
        }
    }

    public void addHoliday(String name, int day, String month, boolean recurring, List<String> commands, String message) {
        Holiday holiday = new Holiday(name, day, month, recurring, commands, message);
        holidays.put(name, holiday);

        // Save to config
        String path = "holidays." + name;
        plugin.getConfigManager().getHolidaysConfig().set(path + ".day", day);
        plugin.getConfigManager().getHolidaysConfig().set(path + ".month", month);
        plugin.getConfigManager().getHolidaysConfig().set(path + ".recurring", recurring);
        plugin.getConfigManager().getHolidaysConfig().set(path + ".commands", commands);
        plugin.getConfigManager().getHolidaysConfig().set(path + ".message", message);
        plugin.getConfigManager().saveHolidaysConfig();
    }

    public void addEvent(String name, int day, String month, Integer year, boolean recurring, List<String> commands, String message) {
        CalendarEvent event = new CalendarEvent(name, day, month, year, recurring, commands, message);
        events.put(name, event);

        // Save to config
        String path = "events." + name;
        plugin.getConfigManager().getEventsConfig().set(path + ".day", day);
        plugin.getConfigManager().getEventsConfig().set(path + ".month", month);
        if (year != null) {
            plugin.getConfigManager().getEventsConfig().set(path + ".year", year);
        }
        plugin.getConfigManager().getEventsConfig().set(path + ".recurring", recurring);
        plugin.getConfigManager().getEventsConfig().set(path + ".commands", commands);
        plugin.getConfigManager().getEventsConfig().set(path + ".message", message);
        plugin.getConfigManager().saveEventsConfig();
    }

    public boolean removeHoliday(String name) {
        if (holidays.remove(name) != null) {
            plugin.getConfigManager().getHolidaysConfig().set("holidays." + name, null);
            plugin.getConfigManager().saveHolidaysConfig();
            return true;
        }
        return false;
    }

    public boolean removeEvent(String name) {
        if (events.remove(name) != null) {
            plugin.getConfigManager().getEventsConfig().set("events." + name, null);
            plugin.getConfigManager().saveEventsConfig();
            return true;
        }
        return false;
    }

    public Map<String, Holiday> getHolidays() {
        return holidays;
    }

    public Map<String, CalendarEvent> getEvents() {
        return events;
    }

    public Holiday getHoliday(String name) {
        return holidays.get(name);
    }

    public CalendarEvent getEvent(String name) {
        return events.get(name);
    }
}
