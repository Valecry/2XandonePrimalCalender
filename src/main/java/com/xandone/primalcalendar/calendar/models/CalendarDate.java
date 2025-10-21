package com.xandone.primalcalendar.calendar.models;

public class CalendarDate {

    private int day;
    private Month month;
    private int year;
    private String era;
    private String eraAbbreviation;

    public CalendarDate(int day, Month month, int year, String era) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.era = era;
        this.eraAbbreviation = "";
    }

    public CalendarDate(int day, Month month, int year, String era, String eraAbbreviation) {
        this.day = day;
        this.month = month;
        this.year = year;
        this.era = era;
        this.eraAbbreviation = eraAbbreviation;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public Month getMonth() {
        return month;
    }

    public void setMonth(Month month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getEra() {
        return era;
    }

    public void setEra(String era) {
        this.era = era;
    }

    public String getEraAbbreviation() {
        return eraAbbreviation;
    }

    public void setEraAbbreviation(String eraAbbreviation) {
        this.eraAbbreviation = eraAbbreviation;
    }

    public boolean matches(int day, String monthName) {
        return this.day == day && this.month.getName().equalsIgnoreCase(monthName);
    }

    public boolean matches(int day, String monthName, int year) {
        return this.day == day && this.month.getName().equalsIgnoreCase(monthName) && this.year == year;
    }

    public String getFormattedYear() {
        if (eraAbbreviation != null && !eraAbbreviation.isEmpty()) {
            return year + " " + eraAbbreviation;
        }
        return String.valueOf(year);
    }

    @Override
    public String toString() {
        return "Day " + day + " of " + month.getName() + ", Year " + year + " (" + era + ")";
    }

    public CalendarDate clone() {
        return new CalendarDate(day, month, year, era, eraAbbreviation);
    }
}
