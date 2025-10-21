package com.xandone.primalcalendar.api.events;

import com.xandone.primalcalendar.calendar.models.CalendarDate;
import com.xandone.primalcalendar.events.models.Holiday;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a holiday is about to be triggered
 * Can be cancelled to prevent the holiday from executing
 */
public class HolidayTriggerEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Holiday holiday;
    private final CalendarDate date;
    private boolean cancelled = false;

    public HolidayTriggerEvent(Holiday holiday, CalendarDate date) {
        this.holiday = holiday;
        this.date = date;
    }

    /**
     * Get the holiday being triggered
     * @return Holiday object
     */
    public Holiday getHoliday() {
        return holiday;
    }

    /**
     * Get the current date
     * @return CalendarDate
     */
    public CalendarDate getDate() {
        return date;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
