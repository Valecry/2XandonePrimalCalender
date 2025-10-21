package com.xandone.primalcalendar.api.events;

import com.xandone.primalcalendar.calendar.models.CalendarDate;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Called when it's a player's birthday
 * Can be cancelled to prevent birthday rewards
 */
public class PlayerBirthdayEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();
    private final Player player;
    private final CalendarDate date;
    private boolean cancelled = false;

    public PlayerBirthdayEvent(Player player, CalendarDate date) {
        this.player = player;
        this.date = date;
    }

    /**
     * Get the player whose birthday it is
     * @return Player object
     */
    public Player getPlayer() {
        return player;
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
