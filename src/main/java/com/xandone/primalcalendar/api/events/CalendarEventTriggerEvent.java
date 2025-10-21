package com.xandone.primalcalendar.api.events;

import com.xandone.primalcalendar.calendar.models.CalendarDate;
import com.xandone.primalcalendar.events.models.CalendarEvent;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when a calendar event is about to be triggered
 * Can be cancelled to prevent the event from executing
 */
public class CalendarEventTriggerEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final CalendarEvent calendarEvent;
    private final CalendarDate date;
    private boolean cancelled = false;

    public CalendarEventTriggerEvent(CalendarEvent calendarEvent, CalendarDate date) {
        this.calendarEvent = calendarEvent;
        this.date = date;
    }

    /**
     * Get the event being triggered
     * @return CalendarEvent object
     */
    public CalendarEvent getCalendarEvent() {
        return calendarEvent;
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
