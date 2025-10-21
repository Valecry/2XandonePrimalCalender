package com.xandone.primalcalendar.api.events;

import com.xandone.primalcalendar.calendar.models.CalendarDate;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when the calendar date changes
 */
public class CalendarDateChangeEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CalendarDate oldDate;
    private final CalendarDate newDate;

    public CalendarDateChangeEvent(CalendarDate oldDate, CalendarDate newDate) {
        this.oldDate = oldDate;
        this.newDate = newDate;
    }

    /**
     * Get the previous date
     * @return Old CalendarDate
     */
    public CalendarDate getOldDate() {
        return oldDate;
    }

    /**
     * Get the new date
     * @return New CalendarDate
     */
    public CalendarDate getNewDate() {
        return newDate;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
