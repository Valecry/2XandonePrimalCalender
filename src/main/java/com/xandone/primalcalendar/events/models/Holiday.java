package com.xandone.primalcalendar.events.models;

import com.xandone.primalcalendar.calendar.models.CalendarDate;

import java.util.List;

public class Holiday {

    private final String name;
    private final int day;
    private final String month;
    private final boolean recurring;
    private final List<String> commands;
    private final String message;

    public Holiday(String name, int day, String month, boolean recurring, List<String> commands, String message) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.recurring = recurring;
        this.commands = commands;
        this.message = message;
    }

    public boolean matches(CalendarDate date) {
        return date.getDay() == day && date.getMonth().getName().equalsIgnoreCase(month);
    }

    public String getName() {
        return name;
    }

    public int getDay() {
        return day;
    }

    public String getMonth() {
        return month;
    }

    public boolean isRecurring() {
        return recurring;
    }

    public List<String> getCommands() {
        return commands;
    }

    public String getMessage() {
        return message;
    }
}
