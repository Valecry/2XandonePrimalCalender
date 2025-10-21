package com.xandone.primalcalendar.commands;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.calendar.models.CalendarDate;
import com.xandone.primalcalendar.items.HolidayCoinItem;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CalendarAdminCommand implements CommandExecutor, TabCompleter {

    private final XandonePrimalCalendar plugin;
    private final HolidayCoinItem holidayCoinItem;

    public CalendarAdminCommand(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
        this.holidayCoinItem = new HolidayCoinItem(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("primalcalendar.admin")) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("commands.no-permission"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "setdate":
                return handleSetDate(sender, args);
            case "advance":
                return handleAdvance(sender, args);
            case "sync":
                return handleSync(sender);
            case "reload":
                return handleReload(sender);
            case "holtoken":
            case "holidaycoin":
                return handleHolidayCoin(sender, args);
            case "addholiday":
                return handleAddHoliday(sender, args);
            case "removeholiday":
                return handleRemoveHoliday(sender, args);
            case "addevent":
                return handleAddEvent(sender, args);
            case "removeevent":
                return handleRemoveEvent(sender, args);
            case "listholidays":
                return handleListHolidays(sender);
            case "listevents":
                return handleListEvents(sender);
            case "record":
                return handleRecord(sender, args);
            case "history":
                return handleHistory(sender);
            case "setbirthday":
                return handleSetBirthday(sender, args);
            case "testdiscord":
                return handleTestDiscord(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l=== Calendar Admin Commands ===");
        sender.sendMessage("§e/caladmin setdate <day> <month> <year> §7- Set the calendar date");
        sender.sendMessage("§e/caladmin advance <days> §7- Advance the calendar by X days");
        sender.sendMessage("§e/caladmin sync §7- Sync calendar with Minecraft day count");
        sender.sendMessage("§e/caladmin reload §7- Reload the plugin configuration");
        sender.sendMessage("§e/caladmin holtoken give <player> <amount> §7- Give holiday coins");
        sender.sendMessage("§e/caladmin addholiday <name> <day> <month> §7- Add a recurring holiday");
        sender.sendMessage("§e/caladmin removeholiday <name> §7- Remove a holiday");
        sender.sendMessage("§e/caladmin addevent <name> <day> <month> [year] §7- Add an event");
        sender.sendMessage("§e/caladmin removeevent <name> §7- Remove an event");
        sender.sendMessage("§e/caladmin listholidays §7- List all holidays");
        sender.sendMessage("§e/caladmin listevents §7- List all events");
        sender.sendMessage("§e/caladmin record <title> <description> §7- Add historical record");
        sender.sendMessage("§e/caladmin history §7- View historical records");
        sender.sendMessage("§e/caladmin setbirthday <player> <day> <month> §7- Set player birthday");
        sender.sendMessage("§e/caladmin testdiscord <type> §7- Test Discord webhooks");
    }

    private boolean handleSetDate(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /caladmin setdate <day> <month> <year>");
            return true;
        }

        try {
            int day = Integer.parseInt(args[1]);
            String month = args[2];
            int year = Integer.parseInt(args[3]);

            plugin.getCalendarManager().setDate(day, month, year);
            CalendarDate newDate = plugin.getCalendarManager().getCurrentDate();
            String formattedDate = plugin.getCalendarManager().formatDate(newDate);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("calendar.date-set", 
                    "%date%", formattedDate));
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number format!");
        } catch (IllegalArgumentException e) {
            sender.sendMessage("§c" + e.getMessage());
        }

        return true;
    }

    private boolean handleAdvance(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /caladmin advance <days>");
            return true;
        }

        try {
            int days = Integer.parseInt(args[1]);
            plugin.getCalendarManager().advanceDays(days);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("calendar.time-advanced", 
                    "%days%", String.valueOf(days)));
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number format!");
        }

        return true;
    }

    private boolean handleSync(CommandSender sender) {
        plugin.getCalendarManager().syncWithMinecraftDay();
        CalendarDate newDate = plugin.getCalendarManager().getCurrentDate();
        String formattedDate = plugin.getCalendarManager().formatDate(newDate);
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("calendar.synced", 
                "%date%", formattedDate));
        return true;
    }

    private boolean handleReload(CommandSender sender) {
        plugin.getConfigManager().reloadConfig();
        plugin.getLanguageManager().loadLanguages();
        plugin.getEventManager().loadHolidays();
        plugin.getEventManager().loadEvents();
        
        sender.sendMessage(plugin.getLanguageManager().getMessage("commands.reload-success"));
        return true;
    }

    private boolean handleHolidayCoin(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /caladmin holtoken give <player> <amount>");
            return true;
        }

        if (!args[1].equalsIgnoreCase("give")) {
            sender.sendMessage("§cUsage: /caladmin holtoken give <player> <amount>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        try {
            int amount = Integer.parseInt(args[3]);
            target.getInventory().addItem(holidayCoinItem.createHolidayCoin(amount));
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("admin.coin-given",
                    "%player%", target.getName(),
                    "%amount%", String.valueOf(amount)));
            target.sendMessage(plugin.getLanguageManager().getMessage("admin.coin-received",
                    "%amount%", String.valueOf(amount)));
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid amount!");
        }

        return true;
    }

    private boolean handleAddHoliday(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /caladmin addholiday <name> <day> <month>");
            return true;
        }

        String name = args[1];
        try {
            int day = Integer.parseInt(args[2]);
            String month = args[3];

            plugin.getEventManager().addHoliday(name, day, month, true, new ArrayList<>(), "§6Today is " + name + "!");
            sender.sendMessage("§aHoliday '" + name + "' added successfully!");
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid day format!");
        }

        return true;
    }

    private boolean handleRemoveHoliday(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /caladmin removeholiday <name>");
            return true;
        }

        String name = args[1];
        if (plugin.getEventManager().removeHoliday(name)) {
            sender.sendMessage("§aHoliday '" + name + "' removed successfully!");
        } else {
            sender.sendMessage("§cHoliday not found!");
        }

        return true;
    }

    private boolean handleAddEvent(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /caladmin addevent <name> <day> <month> [year]");
            return true;
        }

        String name = args[1];
        try {
            int day = Integer.parseInt(args[2]);
            String month = args[3];
            Integer year = args.length > 4 ? Integer.parseInt(args[4]) : null;

            plugin.getEventManager().addEvent(name, day, month, year, year == null, new ArrayList<>(), "§eEvent: " + name);
            sender.sendMessage(plugin.getLanguageManager().getMessage("admin.event-created"));
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid number format!");
        }

        return true;
    }

    private boolean handleRemoveEvent(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /caladmin removeevent <name>");
            return true;
        }

        String name = args[1];
        if (plugin.getEventManager().removeEvent(name)) {
            sender.sendMessage(plugin.getLanguageManager().getMessage("admin.event-removed"));
        } else {
            sender.sendMessage("§cEvent not found!");
        }

        return true;
    }

    private boolean handleListHolidays(CommandSender sender) {
        sender.sendMessage("§6§l=== Holidays ===");
        plugin.getEventManager().getHolidays().values().forEach(holiday -> {
            sender.sendMessage("§e" + holiday.getName() + " §7- Day " + holiday.getDay() + " of " + holiday.getMonth());
        });
        return true;
    }

    private boolean handleListEvents(CommandSender sender) {
        sender.sendMessage("§6§l=== Events ===");
        plugin.getEventManager().getEvents().values().forEach(event -> {
            String yearInfo = event.getYear() != null ? " (Year " + event.getYear() + ")" : "";
            sender.sendMessage("§e" + event.getName() + " §7- Day " + event.getDay() + " of " + event.getMonth() + yearInfo);
        });
        return true;
    }

    private boolean handleRecord(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("§cUsage: /caladmin record <title> <description>");
            return true;
        }

        String title = args[1];
        String description = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        
        plugin.getDataManager().addHistoricalRecord(title, description, plugin.getCalendarManager().getCurrentDate());
        sender.sendMessage("§aHistorical record added!");
        
        return true;
    }

    private boolean handleHistory(CommandSender sender) {
        sender.sendMessage("§6§l=== Historical Records ===");
        plugin.getDataManager().getHistoricalRecords().forEach(record -> {
            sender.sendMessage("§e" + record.get("title") + " §7- Day " + record.get("day") + " of " + 
                    record.get("month") + ", Year " + record.get("year"));
            sender.sendMessage("  §f" + record.get("description"));
        });
        return true;
    }

    private boolean handleSetBirthday(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage("§cUsage: /caladmin setbirthday <player> <day> <month>");
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("§cPlayer not found!");
            return true;
        }

        try {
            int day = Integer.parseInt(args[2]);
            String month = args[3];

            // Validate month
            if (plugin.getCalendarManager().getMonthByName(month) == null) {
                sender.sendMessage("§cInvalid month name!");
                return true;
            }

            // Unlock birthday first, then set it
            plugin.getDataManager().unlockBirthday(target.getUniqueId());
            plugin.getDataManager().setBirthday(target.getUniqueId(), day, month);
            
            sender.sendMessage(plugin.getLanguageManager().getMessage("admin.birthday-set",
                    "%player%", target.getName(),
                    "%date%", "Day " + day + " of " + month));
            target.sendMessage(plugin.getLanguageManager().getMessage("birthdays.admin-set",
                    "%date%", "Day " + day + " of " + month));
        } catch (NumberFormatException e) {
            sender.sendMessage("§cInvalid day format!");
        }

        return true;
    }

    private boolean handleTestDiscord(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage("§cUsage: /caladmin testdiscord <holiday|event|birthday|month>");
            return true;
        }

        if (!plugin.getConfigManager().isDiscordEnabled()) {
            sender.sendMessage("§cDiscord integration is not enabled in config.yml!");
            return true;
        }

        String webhookUrl = plugin.getConfigManager().getDiscordWebhookUrl();
        if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equals("YOUR_WEBHOOK_URL_HERE")) {
            sender.sendMessage("§cDiscord webhook URL is not configured in config.yml!");
            return true;
        }

        CalendarDate currentDate = plugin.getCalendarManager().getCurrentDate();
        String type = args[1].toLowerCase();

        switch (type) {
            case "holiday":
                // Create a test holiday
                plugin.getDiscordWebhook().sendHolidayAnnouncement(
                    new com.xandone.primalcalendar.events.models.Holiday(
                        "Test Holiday",
                        currentDate.getDay(),
                        currentDate.getMonth().getName(),
                        true,
                        new ArrayList<>(),
                        "This is a test holiday announcement!"
                    ),
                    currentDate
                );
                sender.sendMessage("§aTest holiday announcement sent to Discord!");
                break;

            case "event":
                // Create a test event
                plugin.getDiscordWebhook().sendEventAnnouncement(
                    new com.xandone.primalcalendar.events.models.CalendarEvent(
                        "Test Event",
                        currentDate.getDay(),
                        currentDate.getMonth().getName(),
                        currentDate.getYear(),
                        false,
                        new ArrayList<>(),
                        "This is a test event announcement!"
                    ),
                    currentDate
                );
                sender.sendMessage("§aTest event announcement sent to Discord!");
                break;

            case "birthday":
                // Test birthday announcement
                String playerName = sender instanceof Player ? sender.getName() : "TestPlayer";
                plugin.getDiscordWebhook().sendBirthdayAnnouncement(playerName, currentDate);
                sender.sendMessage("§aTest birthday announcement sent to Discord!");
                break;

            case "month":
                // Test month change announcement
                plugin.getDiscordWebhook().sendMonthChangeAnnouncement(currentDate);
                sender.sendMessage("§aTest month change announcement sent to Discord!");
                break;

            default:
                sender.sendMessage("§cInvalid type! Use: holiday, event, birthday, or month");
                return true;
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("setdate", "advance", "sync", "reload", "holtoken", "addholiday", "removeholiday", 
                    "addevent", "removeevent", "listholidays", "listevents", "record", "history", "setbirthday", "testdiscord")
                    .stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("holtoken")) {
            return List.of("give");
        }

        if (args.length == 3 && args[0].equalsIgnoreCase("holtoken") && args[1].equalsIgnoreCase("give")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("setbirthday")) {
            return Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("setbirthday")) {
            return plugin.getCalendarManager().getMonths().stream()
                    .map(month -> month.getName())
                    .filter(name -> name.toLowerCase().startsWith(args[3].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3 && (args[0].equalsIgnoreCase("setdate") || args[0].equalsIgnoreCase("addholiday") || args[0].equalsIgnoreCase("addevent"))) {
            return plugin.getCalendarManager().getMonths().stream()
                    .map(month -> month.getName())
                    .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("testdiscord")) {
            return Arrays.asList("holiday", "event", "birthday", "month")
                    .stream()
                    .filter(s -> s.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
