package com.xandone.primalcalendar.events.models;

import com.xandone.primalcalendar.calendar.models.CalendarDate;

import java.util.List;

public class CalendarEvent {

    private final String name;
    private final int day;
    private final String month;
    private final Integer year; // null means any year
    private final boolean recurring;
    private final List<String> commands;
    private final String message;

    public CalendarEvent(String name, int day, String month, Integer year, boolean recurring, List<String> commands, String message) {
        this.name = name;
        this.day = day;
        this.month = month;
        this.year = year;
        this.recurring = recurring;
        this.commands = commands;
        this.message = message;
    }

    public boolean matches(CalendarDate date) {
        boolean dayMonthMatch = date.getDay() == day && date.getMonth().getName().equalsIgnoreCase(month);
        
        if (year == null) {
            return dayMonthMatch; // Matches any year
        }
        
        return dayMonthMatch && date.getYear() == year;
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

    public Integer getYear() {
        return year;
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
