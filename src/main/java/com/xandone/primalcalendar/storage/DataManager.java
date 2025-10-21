package com.xandone.primalcalendar.storage;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.calendar.models.CalendarDate;
import com.xandone.primalcalendar.calendar.models.Month;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class DataManager {

    private final XandonePrimalCalendar plugin;
    private File dataFile;
    private FileConfiguration data;
    private File birthdaysFile;
    private FileConfiguration birthdays;
    private File historyFile;
    private FileConfiguration history;

    public DataManager(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
    }

    public void loadData() {
        // Load main data file
        dataFile = new File(plugin.getDataFolder(), "data.yml");
        if (!dataFile.exists()) {
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data.yml: " + e.getMessage());
            }
        }
        data = YamlConfiguration.loadConfiguration(dataFile);

        // Load birthdays file
        birthdaysFile = new File(plugin.getDataFolder(), "birthdays.yml");
        if (!birthdaysFile.exists()) {
            try {
                birthdaysFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create birthdays.yml: " + e.getMessage());
            }
        }
        birthdays = YamlConfiguration.loadConfiguration(birthdaysFile);

        // Load history file
        historyFile = new File(plugin.getDataFolder(), "history.yml");
        if (!historyFile.exists()) {
            try {
                historyFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create history.yml: " + e.getMessage());
            }
        }
        history = YamlConfiguration.loadConfiguration(historyFile);
    }

    public void saveData() {
        try {
            data.save(dataFile);
            birthdays.save(birthdaysFile);
            history.save(historyFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data files: " + e.getMessage());
        }
    }

    public CalendarDate getSavedDate() {
        if (!data.contains("current-date")) {
            return null;
        }

        int day = data.getInt("current-date.day");
        String monthName = data.getString("current-date.month");
        int year = data.getInt("current-date.year");
        String era = data.getString("current-date.era");
        String eraAbbreviation = data.getString("current-date.era-abbreviation", "");

        Month month = plugin.getCalendarManager().getMonthByName(monthName);
        if (month == null) {
            return null;
        }

        return new CalendarDate(day, month, year, era, eraAbbreviation);
    }

    public void saveCurrentDate(CalendarDate date) {
        data.set("current-date.day", date.getDay());
        data.set("current-date.month", date.getMonth().getName());
        data.set("current-date.year", date.getYear());
        data.set("current-date.era", date.getEra());
        data.set("current-date.era-abbreviation", date.getEraAbbreviation());
        saveData();
    }

    // Birthday management
    public void setBirthday(UUID playerId, int day, String month) {
        birthdays.set(playerId.toString() + ".day", day);
        birthdays.set(playerId.toString() + ".month", month);
        birthdays.set(playerId.toString() + ".locked", true);
        try {
            birthdays.save(birthdaysFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save birthdays: " + e.getMessage());
        }
    }

    public boolean isBirthdayLocked(UUID playerId) {
        return birthdays.getBoolean(playerId.toString() + ".locked", false);
    }

    public void unlockBirthday(UUID playerId) {
        birthdays.set(playerId.toString() + ".locked", false);
        try {
            birthdays.save(birthdaysFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save birthdays: " + e.getMessage());
        }
    }

    public Map<String, Integer> getBirthday(UUID playerId) {
        if (!birthdays.contains(playerId.toString())) {
            return null;
        }
        Map<String, Integer> birthday = new HashMap<>();
        birthday.put("day", birthdays.getInt(playerId.toString() + ".day"));
        return birthday;
    }

    public String getBirthdayMonth(UUID playerId) {
        return birthdays.getString(playerId.toString() + ".month");
    }

    public List<UUID> getPlayersWithBirthday(int day, String month) {
        List<UUID> players = new ArrayList<>();
        for (String key : birthdays.getKeys(false)) {
            int bDay = birthdays.getInt(key + ".day");
            String bMonth = birthdays.getString(key + ".month");
            if (bDay == day && bMonth.equalsIgnoreCase(month)) {
                players.add(UUID.fromString(key));
            }
        }
        return players;
    }

    // Historical records
    public void addHistoricalRecord(String title, String description, CalendarDate date) {
        String key = "records." + System.currentTimeMillis();
        history.set(key + ".title", title);
        history.set(key + ".description", description);
        history.set(key + ".date.day", date.getDay());
        history.set(key + ".date.month", date.getMonth().getName());
        history.set(key + ".date.year", date.getYear());
        history.set(key + ".date.era", date.getEra());
        try {
            history.save(historyFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save history: " + e.getMessage());
        }
    }

    public List<Map<String, Object>> getHistoricalRecords() {
        List<Map<String, Object>> records = new ArrayList<>();
        if (!history.contains("records")) {
            return records;
        }

        for (String key : history.getConfigurationSection("records").getKeys(false)) {
            Map<String, Object> record = new HashMap<>();
            record.put("title", history.getString("records." + key + ".title"));
            record.put("description", history.getString("records." + key + ".description"));
            record.put("day", history.getInt("records." + key + ".date.day"));
            record.put("month", history.getString("records." + key + ".date.month"));
            record.put("year", history.getInt("records." + key + ".date.year"));
            record.put("era", history.getString("records." + key + ".date.era"));
            records.add(record);
        }
        return records;
    }

    // Holiday coins tracking
    public int getHolidayCoins(UUID playerId) {
        return data.getInt("holiday-coins." + playerId.toString(), 0);
    }

    public void setHolidayCoins(UUID playerId, int amount) {
        data.set("holiday-coins." + playerId.toString(), amount);
        saveData();
    }

    public void addHolidayCoins(UUID playerId, int amount) {
        int current = getHolidayCoins(playerId);
        setHolidayCoins(playerId, current + amount);
    }

    public void removeHolidayCoins(UUID playerId, int amount) {
        int current = getHolidayCoins(playerId);
        setHolidayCoins(playerId, Math.max(0, current - amount));
    }
}
