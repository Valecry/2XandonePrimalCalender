package com.xandone.primalcalendar.config;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ConfigManager {

    private final XandonePrimalCalendar plugin;
    private FileConfiguration config;
    private FileConfiguration holidaysConfig;
    private FileConfiguration eventsConfig;

    public ConfigManager(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
    }

    public void loadConfig() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        
        // Create holidays config
        File holidaysFile = new File(plugin.getDataFolder(), "holidays.yml");
        if (!holidaysFile.exists()) {
            plugin.saveResource("holidays.yml", false);
        }
        holidaysConfig = YamlConfiguration.loadConfiguration(holidaysFile);
        
        // Create events config
        File eventsFile = new File(plugin.getDataFolder(), "events.yml");
        if (!eventsFile.exists()) {
            plugin.saveResource("events.yml", false);
        }
        eventsConfig = YamlConfiguration.loadConfiguration(eventsFile);
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        
        File holidaysFile = new File(plugin.getDataFolder(), "holidays.yml");
        holidaysConfig = YamlConfiguration.loadConfiguration(holidaysFile);
        
        File eventsFile = new File(plugin.getDataFolder(), "events.yml");
        eventsConfig = YamlConfiguration.loadConfiguration(eventsFile);
    }

    public void saveHolidaysConfig() {
        try {
            holidaysConfig.save(new File(plugin.getDataFolder(), "holidays.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save holidays.yml: " + e.getMessage());
        }
    }

    public void saveEventsConfig() {
        try {
            eventsConfig.save(new File(plugin.getDataFolder(), "events.yml"));
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save events.yml: " + e.getMessage());
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getHolidaysConfig() {
        return holidaysConfig;
    }

    public FileConfiguration getEventsConfig() {
        return eventsConfig;
    }

    // Calendar settings
    public boolean isRealTimeSync() {
        return config.getBoolean("calendar.real-time-sync", false);
    }

    public boolean isServerTimeCalendar() {
        return config.getBoolean("calendar.server-time-calendar", true);
    }

    public int getTicksPerDay() {
        return config.getInt("calendar.ticks-per-day", 24000);
    }

    public String getDateFormat() {
        return config.getString("calendar.date-format", "Day %day% of %month%, Year %year%");
    }

    public List<String> getMonthNames() {
        return config.getStringList("calendar.months");
    }

    public int getDaysPerMonth() {
        return config.getInt("calendar.days-per-month", 30);
    }

    public String getEraName() {
        return config.getString("calendar.era-name", "First Era");
    }

    public String getEraAbbreviation() {
        return config.getString("calendar.era-abbreviation", "F.E");
    }

    public int getYearsPerEra() {
        return config.getInt("calendar.years-per-era", 0);
    }

    public String getEraNameByNumber(int eraNumber) {
        return config.getString("calendar.eras." + eraNumber + ".name", "Unknown Era");
    }

    public String getEraAbbreviationByNumber(int eraNumber) {
        return config.getString("calendar.eras." + eraNumber + ".abbreviation", "U.E.");
    }

    // Discord settings
    public boolean isDiscordEnabled() {
        return config.getBoolean("discord.enabled", false);
    }

    public String getDiscordWebhookUrl() {
        return config.getString("discord.webhook-url", "");
    }

    public boolean shouldAnnounceHolidays() {
        return config.getBoolean("discord.announce-holidays", true);
    }

    public boolean shouldAnnounceEvents() {
        return config.getBoolean("discord.announce-events", true);
    }

    public boolean shouldAnnounceMonthChange() {
        return config.getBoolean("discord.announce-month-change", true);
    }

    public boolean shouldAnnounceDayChange() {
        return config.getBoolean("discord.announce-day-change", true);
    }

    public boolean shouldAnnounceYearChange() {
        return config.getBoolean("discord.announce-year-change", true);
    }

    public boolean shouldAnnounceEraChange() {
        return config.getBoolean("discord.announce-era-change", true);
    }

    public boolean shouldAnnounceBirthdays() {
        return config.getBoolean("discord.announce-birthdays", true);
    }

    // Birthday settings
    public boolean areBirthdaysEnabled() {
        return config.getBoolean("birthdays.enabled", true);
    }

    public List<String> getBirthdayRewards() {
        return config.getStringList("birthdays.rewards");
    }

    public boolean shouldAutoSetBirthdayOnJoin() {
        return config.getBoolean("birthdays.auto-set-on-join", true);
    }
}
