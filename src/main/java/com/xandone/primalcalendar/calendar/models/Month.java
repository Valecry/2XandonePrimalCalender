package com.xandone.primalcalendar.calendar.models;

public class Month {

    private final int number;
    private final String name;
    private final int days;

    public Month(int number, String name, int days) {
        this.number = number;
        this.name = name;
        this.days = days;
    }

    public int getNumber() {
        return number;
    }

    public String getName() {
        return name;
    }

    public int getDays() {
        return days;
    }

    @Override
    public String toString() {
        return name + " (" + days + " days)";
    }
}
