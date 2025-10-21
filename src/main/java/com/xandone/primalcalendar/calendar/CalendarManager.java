package com.xandone.primalcalendar.calendar;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.api.events.CalendarDateChangeEvent;
import com.xandone.primalcalendar.calendar.models.CalendarDate;
import com.xandone.primalcalendar.calendar.models.Month;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarManager {

    private final XandonePrimalCalendar plugin;
    private CalendarDate currentDate;
    private List<Month> months;
    private BukkitTask tickerTask;
    private long lastTickTime;
    private int ticksPerDay;

    public CalendarManager(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
        this.months = new ArrayList<>();
    }

    public void initialize() {
        loadMonths();
        loadCurrentDate();
        this.ticksPerDay = plugin.getConfigManager().getTicksPerDay();
        this.lastTickTime = System.currentTimeMillis();
    }

    private void loadMonths() {
        months.clear();
        List<String> monthNames = plugin.getConfigManager().getMonthNames();
        int defaultDays = plugin.getConfigManager().getDaysPerMonth();
        
        Map<String, Integer> customLengths = new HashMap<>();
        if (plugin.getConfig().contains("calendar.custom-month-lengths") && 
            plugin.getConfig().getConfigurationSection("calendar.custom-month-lengths") != null) {
            for (String monthName : plugin.getConfig().getConfigurationSection("calendar.custom-month-lengths").getKeys(false)) {
                customLengths.put(monthName, plugin.getConfig().getInt("calendar.custom-month-lengths." + monthName));
            }
        }
        
        for (int i = 0; i < monthNames.size(); i++) {
            String name = monthNames.get(i);
            int days = customLengths.getOrDefault(name, defaultDays);
            months.add(new Month(i + 1, name, days));
        }
    }

    private void loadCurrentDate() {
        if (plugin.getConfigManager().isRealTimeSync()) {
            // Sync with real-world date
            LocalDate realDate = LocalDate.now();
            int monthIndex = (realDate.getMonthValue() - 1) % months.size();
            Month month = months.get(monthIndex);
            int day = Math.min(realDate.getDayOfMonth(), month.getDays());
            int year = realDate.getYear();
            currentDate = new CalendarDate(day, month, year, 
                    plugin.getConfigManager().getEraName(),
                    plugin.getConfigManager().getEraAbbreviation());
        } else {
            // Load from data or start at day 1
            CalendarDate savedDate = plugin.getDataManager().getSavedDate();
            if (savedDate != null) {
                currentDate = savedDate;
            } else {
                currentDate = new CalendarDate(1, months.get(0), 1, 
                        plugin.getConfigManager().getEraName(),
                        plugin.getConfigManager().getEraAbbreviation());
            }
        }
    }

    public void startTicker() {
        if (!plugin.getConfigManager().isServerTimeCalendar()) {
            return; // Don't tick if using real-time sync
        }

        tickerTask = Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            // Check if a full day has passed
            long currentTime = System.currentTimeMillis();
            long elapsedTicks = (currentTime - lastTickTime) / 50; // Convert ms to ticks
            
            if (elapsedTicks >= ticksPerDay) {
                advanceDay();
                lastTickTime = currentTime;
            }
        }, 20L, 20L); // Run every second
    }

    public void stopTicker() {
        if (tickerTask != null) {
            tickerTask.cancel();
        }
    }

    public void advanceDay() {
        advanceDays(1);
    }

    public void advanceDays(int days) {
        CalendarDate oldDate = currentDate.clone();
        
        for (int i = 0; i < days; i++) {
            currentDate.setDay(currentDate.getDay() + 1);
            
            // Send day change announcement
            if (plugin.getConfigManager().isDiscordEnabled() && 
                plugin.getConfigManager().shouldAnnounceDayChange()) {
                plugin.getDiscordWebhook().sendDayChangeAnnouncement(currentDate);
            }
            
            // Check if we need to advance to next month
            if (currentDate.getDay() > currentDate.getMonth().getDays()) {
                currentDate.setDay(1);
                int nextMonthIndex = (currentDate.getMonth().getNumber() % months.size());
                currentDate.setMonth(months.get(nextMonthIndex));
                
                if (plugin.getConfigManager().isDiscordEnabled() && 
                    plugin.getConfigManager().shouldAnnounceMonthChange()) {
                    plugin.getDiscordWebhook().sendMonthChangeAnnouncement(currentDate);
                }
                
                // Check if we've wrapped around to a new year
                if (nextMonthIndex == 0) {
                    String oldEra = currentDate.getEra();
                    currentDate.setYear(currentDate.getYear() + 1);
                    updateEraIfNeeded();
                    
                    // Send year change announcement
                    if (plugin.getConfigManager().isDiscordEnabled() && 
                        plugin.getConfigManager().shouldAnnounceYearChange()) {
                        plugin.getDiscordWebhook().sendYearChangeAnnouncement(currentDate);
                    }
                    
                    // Send era change announcement if era changed
                    if (plugin.getConfigManager().isDiscordEnabled() && 
                        plugin.getConfigManager().shouldAnnounceEraChange() &&
                        !oldEra.equals(currentDate.getEra())) {
                        plugin.getDiscordWebhook().sendEraChangeAnnouncement(currentDate, oldEra, currentDate.getEra());
                    }
                }
            }
            
            // Trigger daily events
            plugin.getEventManager().checkDailyEvents(currentDate);
        }
        
        CalendarDateChangeEvent event = new CalendarDateChangeEvent(oldDate, currentDate.clone());
        Bukkit.getPluginManager().callEvent(event);
        
        // Save the new date
        plugin.getDataManager().saveCurrentDate(currentDate);
    }

    private void updateEraIfNeeded() {
        int yearsPerEra = plugin.getConfigManager().getYearsPerEra();
        if (yearsPerEra > 0) {
            int eraNumber = ((currentDate.getYear() - 1) / yearsPerEra) + 1;
            
            String eraName = plugin.getConfigManager().getEraNameByNumber(eraNumber);
            String eraAbbreviation = plugin.getConfigManager().getEraAbbreviationByNumber(eraNumber);
            
            currentDate.setEra(eraName);
            currentDate.setEraAbbreviation(eraAbbreviation);
        } else {
            currentDate.setEra(plugin.getConfigManager().getEraName());
            currentDate.setEraAbbreviation(plugin.getConfigManager().getEraAbbreviation());
        }
    }

    public void setDate(int day, String monthName, int year) {
        CalendarDate oldDate = currentDate.clone();
        
        Month month = getMonthByName(monthName);
        if (month == null) {
            throw new IllegalArgumentException("Invalid month name: " + monthName);
        }
        
        if (day < 1 || day > month.getDays()) {
            throw new IllegalArgumentException("Invalid day for month " + monthName + ": " + day);
        }
        
        currentDate = new CalendarDate(day, month, year, 
                plugin.getConfigManager().getEraName(),
                plugin.getConfigManager().getEraAbbreviation());
        updateEraIfNeeded();
        
        CalendarDateChangeEvent event = new CalendarDateChangeEvent(oldDate, currentDate.clone());
        Bukkit.getPluginManager().callEvent(event);
        
        plugin.getDataManager().saveCurrentDate(currentDate);
    }

    public void syncWithMinecraftDay() {
        long worldTime = plugin.getServer().getWorlds().get(0).getFullTime();
        long daysPassed = worldTime / 24000L;
        
        // Reset to day 1 and advance by the number of days passed
        currentDate = new CalendarDate(1, months.get(0), 1, 
                plugin.getConfigManager().getEraName(),
                plugin.getConfigManager().getEraAbbreviation());
        
        if (daysPassed > 0) {
            advanceDays((int) daysPassed);
        }
        
        plugin.getDataManager().saveCurrentDate(currentDate);
    }

    public CalendarDate getCurrentDate() {
        return currentDate;
    }

    public List<Month> getMonths() {
        return months;
    }

    public Month getMonthByName(String name) {
        return months.stream()
                .filter(m -> m.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public Month getMonthByNumber(int number) {
        return months.stream()
                .filter(m -> m.getNumber() == number)
                .findFirst()
                .orElse(null);
    }

    public String formatDate(CalendarDate date) {
        String format = plugin.getConfigManager().getDateFormat();
        return format
                .replace("%day%", String.valueOf(date.getDay()))
                .replace("%month%", date.getMonth().getName())
                .replace("%year%", date.getFormattedYear())
                .replace("%era%", date.getEra());
    }

    public int getTotalDaysInYear() {
        return months.stream().mapToInt(Month::getDays).sum();
    }
}
