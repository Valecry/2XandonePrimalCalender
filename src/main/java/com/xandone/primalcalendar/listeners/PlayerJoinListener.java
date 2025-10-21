package com.xandone.primalcalendar.listeners;

import com.xandone.primalcalendar.XandonePrimalCalendar;
import com.xandone.primalcalendar.calendar.models.CalendarDate;
import com.xandone.primalcalendar.calendar.models.Month;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.Random;

public class PlayerJoinListener implements Listener {

    private final XandonePrimalCalendar plugin;
    private final Random random;

    public PlayerJoinListener(XandonePrimalCalendar plugin) {
        this.plugin = plugin;
        this.random = new Random();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Check if auto-set birthday on join is enabled
        if (!plugin.getConfigManager().shouldAutoSetBirthdayOnJoin()) {
            return;
        }

        // Check if player already has a birthday set
        if (plugin.getDataManager().getBirthday(event.getPlayer().getUniqueId()) != null) {
            return;
        }

        // Generate a random birthday
        Month randomMonth = plugin.getCalendarManager().getMonths()
                .get(random.nextInt(plugin.getCalendarManager().getMonths().size()));
        int randomDay = random.nextInt(randomMonth.getDays()) + 1;

        // Set the birthday (this will lock it automatically)
        plugin.getDataManager().setBirthday(event.getPlayer().getUniqueId(), randomDay, randomMonth.getName());

        // Notify the player
        event.getPlayer().sendMessage(plugin.getLanguageManager().getMessage("birthdays.auto-set",
                "%date%", "Day " + randomDay + " of " + randomMonth.getName()));
    }
}
