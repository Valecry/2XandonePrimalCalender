# XandonePrimalCalendar API Documentation

## Getting Started

To use the XandonePrimalCalendar API in your plugin, add it as a dependency in your `plugin.yml`:

\`\`\`yaml
depend: [XandonePrimalCalendar]
\`\`\`

Or as a soft dependency:

\`\`\`yaml
softdepend: [XandonePrimalCalendar]
\`\`\`

## Accessing the API

\`\`\`java
XandonePrimalCalendar plugin = (XandonePrimalCalendar) Bukkit.getPluginManager().getPlugin("XandonePrimalCalendar");
CalendarAPI api = plugin.getCalendarAPI();
\`\`\`

## Calendar Operations

### Get Current Date
\`\`\`java
CalendarDate currentDate = api.getCurrentDate();
int day = api.getCurrentDay();
Month month = api.getCurrentMonth();
int year = api.getCurrentYear();
String era = api.getCurrentEra();
\`\`\`

### Set Date
\`\`\`java
api.setDate(15, "Frostmoon", 3);
\`\`\`

### Advance Time
\`\`\`java
api.advanceDays(7); // Advance by 7 days
\`\`\`

### Format Dates
\`\`\`java
String formatted = api.formatDate(currentDate);
\`\`\`

## Holiday & Event Management

### Add Holiday
\`\`\`java
List<String> commands = Arrays.asList("give @a diamond 5", "broadcast Happy Holiday!");
api.addHoliday("Winter Festival", 1, "Frostmoon", true, commands, "Today is Winter Festival!");
\`\`\`

### Add Event
\`\`\`java
List<String> commands = Arrays.asList("broadcast Special event!");
api.addEvent("One-Time Event", 10, "Sunmoon", 3, false, commands, "Special event happening!");
\`\`\`

### Get Holidays
\`\`\`java
Map<String, Holiday> holidays = api.getHolidays();
Holiday holiday = api.getHoliday("Winter Festival");
\`\`\`

## Birthday Management

### Set Birthday
\`\`\`java
UUID playerId = player.getUniqueId();
api.setBirthday(playerId, 15, "Bloomoon");
\`\`\`

### Get Birthday
\`\`\`java
Map<String, Integer> birthday = api.getBirthday(playerId);
String month = api.getBirthdayMonth(playerId);
\`\`\`

### Get Players with Birthday Today
\`\`\`java
List<UUID> players = api.getPlayersWithBirthday(15, "Bloomoon");
\`\`\`

## Historical Records

### Add Record
\`\`\`java
CalendarDate date = api.getCurrentDate();
api.addHistoricalRecord("Great Battle", "The kingdom was saved", date);
\`\`\`

### Get Records
\`\`\`java
List<Map<String, Object>> records = api.getHistoricalRecords();
\`\`\`

## Holiday Coins

### Manage Coins
\`\`\`java
int coins = api.getHolidayCoins(playerId);
api.setHolidayCoins(playerId, 10);
api.addHolidayCoins(playerId, 5);
api.removeHolidayCoins(playerId, 2);
\`\`\`

## Discord Integration

### Send Custom Message
\`\`\`java
api.sendDiscordMessage("Custom announcement from my plugin!");
\`\`\`

## Events

### Listen to Calendar Events

\`\`\`java
@EventHandler
public void onDateChange(CalendarDateChangeEvent event) {
    CalendarDate oldDate = event.getOldDate();
    CalendarDate newDate = event.getNewDate();
    // Handle date change
}

@EventHandler
public void onHoliday(HolidayTriggerEvent event) {
    Holiday holiday = event.getHoliday();
    CalendarDate date = event.getDate();
    
    // Cancel the holiday if needed
    if (someCondition) {
        event.setCancelled(true);
    }
}

@EventHandler
public void onEvent(CalendarEventTriggerEvent event) {
    CalendarEvent calendarEvent = event.getCalendarEvent();
    // Handle event trigger
}

@EventHandler
public void onBirthday(PlayerBirthdayEvent event) {
    Player player = event.getPlayer();
    // Give custom birthday rewards
}
\`\`\`

## Month Information

### Get Months
\`\`\`java
List<Month> months = api.getMonths();
Month month = api.getMonthByName("Frostmoon");
Month month2 = api.getMonthByNumber(1);
int totalDays = api.getTotalDaysInYear();
\`\`\`

## Configuration Checks

\`\`\`java
boolean realTimeSync = api.isRealTimeSync();
boolean serverTime = api.isServerTimeCalendar();
\`\`\`

## PlaceholderAPI Placeholders

- `%primalcalendar_day%` - Current day
- `%primalcalendar_month%` - Current month name
- `%primalcalendar_month_number%` - Current month number
- `%primalcalendar_year%` - Current year
- `%primalcalendar_era%` - Current era
- `%primalcalendar_date%` - Formatted date
- `%primalcalendar_date_short%` - Short date format (1/8/3)
- `%primalcalendar_birthday%` - Player's birthday
- `%primalcalendar_holiday_coins%` - Player's holiday coins
- `%primalcalendar_holidays_count%` - Total holidays
- `%primalcalendar_events_count%` - Total events
- `%primalcalendar_mode%` - Calendar mode (Real-Time/Server-Time)

## Example Plugin Integration

\`\`\`java
public class MyPlugin extends JavaPlugin {
    
    private CalendarAPI calendarAPI;
    
    @Override
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("XandonePrimalCalendar") != null) {
            XandonePrimalCalendar calendar = (XandonePrimalCalendar) Bukkit.getPluginManager()
                    .getPlugin("XandonePrimalCalendar");
            calendarAPI = calendar.getCalendarAPI();
            getLogger().info("Hooked into XandonePrimalCalendar!");
        }
    }
    
    public void doSomethingWithCalendar() {
        if (calendarAPI != null) {
            CalendarDate date = calendarAPI.getCurrentDate();
            getLogger().info("Current date: " + calendarAPI.formatDate(date));
        }
    }
}
