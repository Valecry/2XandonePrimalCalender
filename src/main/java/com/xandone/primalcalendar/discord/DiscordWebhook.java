package com.xandone.primalcalendar.discord;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.calendar.models.CalendarDate;
import com.xandone.primalcalendar.events.models.CalendarEvent;
import com.xandone.primalcalendar.events.models.Holiday;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {

    private final XandonePrimalCalendar plugin;

    public DiscordWebhook(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
    }

    public void sendHolidayAnnouncement(Holiday holiday, CalendarDate date) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            return;
        }

        String formattedDate = plugin.getCalendarManager().formatDate(date);
        String title = "🎉 Holiday: " + holiday.getName();
        String description = "Today is **" + holiday.getName() + "**!\n\n📅 Date: " + formattedDate;
        int color = 16766720; // Gold color

        sendEmbed(title, description, color);
    }

    public void sendEventAnnouncement(CalendarEvent event, CalendarDate date) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            return;
        }

        String formattedDate = plugin.getCalendarManager().formatDate(date);
        String title = "📢 Event: " + event.getName();
        String description = "An event is happening today!\n\n📅 Date: " + formattedDate;
        int color = 3447003; // Blue color

        sendEmbed(title, description, color);
    }

    public void sendBirthdayAnnouncement(String playerName, CalendarDate date) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            return;
        }

        String formattedDate = plugin.getCalendarManager().formatDate(date);
        String title = "🎂 Birthday Celebration!";
        String description = "Happy Birthday to **" + playerName + "**!\n\n📅 Date: " + formattedDate;
        int color = 15844367; // Pink color

        sendEmbed(title, description, color);
    }

    public void sendDateChange(CalendarDate date) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            return;
        }

        String formattedDate = plugin.getCalendarManager().formatDate(date);
        String title = "📅 Calendar Update";
        String description = "The calendar date has been updated to:\n**" + formattedDate + "**";
        int color = 5814783; // Purple color

        sendEmbed(title, description, color);
    }

    public void sendMonthChangeAnnouncement(CalendarDate date) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            return;
        }

        if (!plugin.getConfigManager().shouldAnnounceMonthChange()) {
            return;
        }

        String formattedDate = plugin.getCalendarManager().formatDate(date);
        String title = "🌙 New Month Begins!";
        String description = String.format(
                "A new month has begun in the calendar!\n\n" +
                "📅 **Current Date:** %s\n" +
                "🗓️ **Month:** %s\n" +
                "📆 **Day:** %d\n" +
                "📜 **Year:** %s",
                formattedDate,
                date.getMonth().getName(),
                date.getDay(),
                date.getFormattedYear()
        );
        int color = 3066993; // Green color

        sendEmbed(title, description, color);
    }

    public void sendDayChangeAnnouncement(CalendarDate date) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            return;
        }

        if (!plugin.getConfigManager().shouldAnnounceDayChange()) {
            return;
        }

        String formattedDate = plugin.getCalendarManager().formatDate(date);
        String title = "\ud83c\udf05 New Day Begins!";
        String description = String.format(
                "A new day has begun in the calendar!\n\n" +
                "\ud83d\udcc5 **Current Date:** %s\n" +
                "\ud83d\uddd3\ufe0f **Month:** %s\n" +
                "\ud83d\udcc6 **Day:** %d\n" +
                "\ud83d\udcdc **Year:** %s",
                formattedDate,
                date.getMonth().getName(),
                date.getDay(),
                date.getFormattedYear()
        );
        int color = 15158332; // Orange color

        sendEmbed(title, description, color);
    }

    public void sendYearChangeAnnouncement(CalendarDate date) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            return;
        }

        if (!plugin.getConfigManager().shouldAnnounceYearChange()) {
            return;
        }

        String formattedDate = plugin.getCalendarManager().formatDate(date);
        String title = "\ud83c\udf8a New Year Begins!";
        String description = String.format(
                "A new year has begun in the calendar!\n\n" +
                "\ud83d\udcc5 **Current Date:** %s\n" +
                "\ud83d\uddd3\ufe0f **Month:** %s\n" +
                "\ud83d\udcc6 **Day:** %d\n" +
                "\ud83d\udcdc **Year:** %s\n\n" +
                "\ud83c\udf89 May this year bring prosperity and adventure!",
                formattedDate,
                date.getMonth().getName(),
                date.getDay(),
                date.getFormattedYear()
        );
        int color = 10181038; // Teal color

        sendEmbed(title, description, color);
    }

    public void sendEraChangeAnnouncement(CalendarDate date, String oldEra, String newEra) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            return;
        }

        if (!plugin.getConfigManager().shouldAnnounceEraChange()) {
            return;
        }

        String formattedDate = plugin.getCalendarManager().formatDate(date);
        String title = "\u26a1 Era Transformed!";
        String description = String.format(
                "The great cycle has turned and a new era begins!\n\n" +
                "\ud83d\udcc5 **Current Date:** %s\n" +
                "\ud83d\uddd3\ufe0f **Month:** %s\n" +
                "\ud83d\udcc6 **Day:** %d\n" +
                "\ud83d\udcdc **Year:** %s\n\n" +
                "\u23f0 **Previous Era:** %s\n" +
                "\u2728 **New Era:** %s\n\n" +
                "\ud83c\udf1f Let the tales of this new age be written!",
                formattedDate,
                date.getMonth().getName(),
                date.getDay(),
                date.getFormattedYear(),
                oldEra,
                newEra
        );
        int color = 10117668; // Purple color

        sendEmbed(title, description, color);
    }

    private void sendEmbed(String title, String description, int color) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String webhookUrl = plugin.getConfigManager().getDiscordWebhookUrl();
                if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equals("YOUR_WEBHOOK_URL_HERE")) {
                    return;
                }

                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = buildEmbedJson(title, description, color);

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != 204 && responseCode != 200) {
                    plugin.getLogger().warning("Discord webhook returned response code: " + responseCode);
                }

                connection.disconnect();

            } catch (IOException e) {
                plugin.getLogger().warning("Failed to send Discord webhook: " + e.getMessage());
            }
        });
    }

    private String buildEmbedJson(String title, String description, int color) {
        return String.format(
                "{\"embeds\":[{\"title\":\"%s\",\"description\":\"%s\",\"color\":%d,\"timestamp\":\"%s\"}]}",
                escapeJson(title),
                escapeJson(description),
                color,
                java.time.Instant.now().toString()
        );
    }

    private String escapeJson(String text) {
        return text.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public void sendCustomMessage(String message) {
        if (!plugin.getConfigManager().isDiscordEnabled()) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                String webhookUrl = plugin.getConfigManager().getDiscordWebhookUrl();
                if (webhookUrl == null || webhookUrl.isEmpty() || webhookUrl.equals("YOUR_WEBHOOK_URL_HERE")) {
                    return;
                }

                URL url = new URL(webhookUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = String.format("{\"content\":\"%s\"}", escapeJson(message));

                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode != 204 && responseCode != 200) {
                    plugin.getLogger().warning("Discord webhook returned response code: " + responseCode);
                }

                connection.disconnect();

            } catch (IOException e) {
                plugin.getLogger().warning("Failed to send Discord webhook: " + e.getMessage());
            }
        });
    }
}
