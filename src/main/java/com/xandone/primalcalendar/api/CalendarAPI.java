package com.xandone.primalcalendar.api;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.calendar.models.CalendarDate;
import com.xandone.primalcalendar.calendar.models.Month;
import com.xandone.primalcalendar.events.models.CalendarEvent;
import com.xandone.primalcalendar.events.models.Holiday;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Main API class for XandonePrimalCalendar
 * Allows other plugins to interact with the calendar system
 */
public class CalendarAPI {

    private final XandonePrimalCalendar plugin;

    public CalendarAPI(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
    }

    /**
     * Get the current calendar date
     * @return Current CalendarDate object
     */
    public CalendarDate getCurrentDate() {
        return plugin.getCalendarManager().getCurrentDate();
    }

    /**
     * Get the current day
     * @return Current day number
     */
    public int getCurrentDay() {
        return plugin.getCalendarManager().getCurrentDate().getDay();
    }

    /**
     * Get the current month
     * @return Current Month object
     */
    public Month getCurrentMonth() {
        return plugin.getCalendarManager().getCurrentDate().getMonth();
    }

    /**
     * Get the current year
     * @return Current year number
     */
    public int getCurrentYear() {
        return plugin.getCalendarManager().getCurrentDate().getYear();
    }

    /**
     * Get the current era name
     * @return Current era name
     */
    public String getCurrentEra() {
        return plugin.getCalendarManager().getCurrentDate().getEra();
    }

    /**
     * Format a date according to the configured format
     * @param date CalendarDate to format
     * @return Formatted date string
     */
    public String formatDate(CalendarDate date) {
        return plugin.getCalendarManager().formatDate(date);
    }

    /**
     * Set the calendar date
     * @param day Day of the month
     * @param monthName Name of the month
     * @param year Year number
     * @throws IllegalArgumentException if date is invalid
     */
    public void setDate(int day, String monthName, int year) {
        plugin.getCalendarManager().setDate(day, monthName, year);
    }

    /**
     * Advance the calendar by a number of days
     * @param days Number of days to advance
     */
    public void advanceDays(int days) {
        plugin.getCalendarManager().advanceDays(days);
    }

    /**
     * Get all configured months
     * @return List of Month objects
     */
    public List<Month> getMonths() {
        return plugin.getCalendarManager().getMonths();
    }

    /**
     * Get a month by its name
     * @param name Month name
     * @return Month object or null if not found
     */
    public Month getMonthByName(String name) {
        return plugin.getCalendarManager().getMonthByName(name);
    }

    /**
     * Get a month by its number
     * @param number Month number (1-based)
     * @return Month object or null if not found
     */
    public Month getMonthByNumber(int number) {
        return plugin.getCalendarManager().getMonthByNumber(number);
    }

    /**
     * Get total days in the year
     * @return Total number of days
     */
    public int getTotalDaysInYear() {
        return plugin.getCalendarManager().getTotalDaysInYear();
    }

    /**
     * Add a new holiday
     * @param name Holiday name
     * @param day Day of the month
     * @param month Month name
     * @param recurring Whether the holiday repeats yearly
     * @param commands Commands to execute on the holiday
     * @param message Message to broadcast
     */
    public void addHoliday(String name, int day, String month, boolean recurring, List<String> commands, String message) {
        plugin.getEventManager().addHoliday(name, day, month, recurring, commands, message);
    }

    /**
     * Remove a holiday
     * @param name Holiday name
     * @return true if removed, false if not found
     */
    public boolean removeHoliday(String name) {
        return plugin.getEventManager().removeHoliday(name);
    }

    /**
     * Get all holidays
     * @return Map of holiday names to Holiday objects
     */
    public Map<String, Holiday> getHolidays() {
        return plugin.getEventManager().getHolidays();
    }

    /**
     * Get a specific holiday
     * @param name Holiday name
     * @return Holiday object or null if not found
     */
    public Holiday getHoliday(String name) {
        return plugin.getEventManager().getHoliday(name);
    }

    /**
     * Add a new event
     * @param name Event name
     * @param day Day of the month
     * @param month Month name
     * @param year Specific year (null for any year)
     * @param recurring Whether the event repeats
     * @param commands Commands to execute on the event
     * @param message Message to broadcast
     */
    public void addEvent(String name, int day, String month, Integer year, boolean recurring, List<String> commands, String message) {
        plugin.getEventManager().addEvent(name, day, month, year, recurring, commands, message);
    }

    /**
     * Remove an event
     * @param name Event name
     * @return true if removed, false if not found
     */
    public boolean removeEvent(String name) {
        return plugin.getEventManager().removeEvent(name);
    }

    /**
     * Get all events
     * @return Map of event names to CalendarEvent objects
     */
    public Map<String, CalendarEvent> getEvents() {
        return plugin.getEventManager().getEvents();
    }

    /**
     * Get a specific event
     * @param name Event name
     * @return CalendarEvent object or null if not found
     */
    public CalendarEvent getEvent(String name) {
        return plugin.getEventManager().getEvent(name);
    }

    /**
     * Set a player's birthday
     * @param playerId Player UUID
     * @param day Day of the month
     * @param month Month name
     */
    public void setBirthday(UUID playerId, int day, String month) {
        plugin.getDataManager().setBirthday(playerId, day, month);
    }

    /**
     * Get a player's birthday
     * @param playerId Player UUID
     * @return Map with "day" key, or null if not set
     */
    public Map<String, Integer> getBirthday(UUID playerId) {
        return plugin.getDataManager().getBirthday(playerId);
    }

    /**
     * Get a player's birthday month
     * @param playerId Player UUID
     * @return Month name or null if not set
     */
    public String getBirthdayMonth(UUID playerId) {
        return plugin.getDataManager().getBirthdayMonth(playerId);
    }

    /**
     * Get all players with a birthday on a specific date
     * @param day Day of the month
     * @param month Month name
     * @return List of player UUIDs
     */
    public List<UUID> getPlayersWithBirthday(int day, String month) {
        return plugin.getDataManager().getPlayersWithBirthday(day, month);
    }

    /**
     * Add a historical record
     * @param title Record title
     * @param description Record description
     * @param date Date of the record
     */
    public void addHistoricalRecord(String title, String description, CalendarDate date) {
        plugin.getDataManager().addHistoricalRecord(title, description, date);
    }

    /**
     * Get all historical records
     * @return List of record maps
     */
    public List<Map<String, Object>> getHistoricalRecords() {
        return plugin.getDataManager().getHistoricalRecords();
    }

    /**
     * Get a player's holiday coin count
     * @param playerId Player UUID
     * @return Number of holiday coins
     */
    public int getHolidayCoins(UUID playerId) {
        return plugin.getDataManager().getHolidayCoins(playerId);
    }

    /**
     * Set a player's holiday coin count
     * @param playerId Player UUID
     * @param amount Number of coins
     */
    public void setHolidayCoins(UUID playerId, int amount) {
        plugin.getDataManager().setHolidayCoins(playerId, amount);
    }

    /**
     * Add holiday coins to a player
     * @param playerId Player UUID
     * @param amount Number of coins to add
     */
    public void addHolidayCoins(UUID playerId, int amount) {
        plugin.getDataManager().addHolidayCoins(playerId, amount);
    }

    /**
     * Remove holiday coins from a player
     * @param playerId Player UUID
     * @param amount Number of coins to remove
     */
    public void removeHolidayCoins(UUID playerId, int amount) {
        plugin.getDataManager().removeHolidayCoins(playerId, amount);
    }

    /**
     * Check if real-time sync is enabled
     * @return true if using real-world time
     */
    public boolean isRealTimeSync() {
        return plugin.getConfigManager().isRealTimeSync();
    }

    /**
     * Check if server-time calendar is enabled
     * @return true if using in-game time
     */
    public boolean isServerTimeCalendar() {
        return plugin.getConfigManager().isServerTimeCalendar();
    }

    /**
     * Send a custom Discord webhook message
     * @param message Message to send
     */
    public void sendDiscordMessage(String message) {
        plugin.getDiscordWebhook().sendCustomMessage(message);
    }
}
