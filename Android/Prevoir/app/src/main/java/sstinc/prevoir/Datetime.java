package sstinc.prevoir;

import org.joda.time.Period;

import java.util.Calendar;
import java.util.Locale;

//TODO: Locale datetime string format

/**
 * This class handles information relating to an instance in time using
 * Calendar. Minutes is the smallest denomination this class handles.
 *
 * @see Calendar
 */
class Datetime {
    private Calendar calendar;
    private boolean hasDate;
    private boolean hasTime;

    /**
     * Default constructor. Sets the calendar to the minimum date possible
     * for calendar to hold. {@link #hasDate} and {@link #hasTime} are false
     * to indicate absence of date and time.
     */
    Datetime() {
        this.hasDate = false;
        this.hasTime = false;

        this.calendar = Calendar.getInstance();
        this.calendar.set(0, 0, 0, 0, 0);
    }

    // Copy constructor
    Datetime(Datetime datetime) {
        this.calendar = Calendar.getInstance();
        this.calendar.setTimeInMillis(datetime.getMillis());

        this.hasDate = datetime.getHasDate();
        this.hasTime = datetime.getHasTime();
    }

    /**
     * String constructor. Creates the object by parsing the string passed.
     * Uses default empty constructor {@link #Datetime()} if string is empty.
     *
     * @param datetime The string of the datetime. Can be obtained by the
     *                 {@link #toString} method.
     */
    Datetime(String datetime) {
        if (datetime.isEmpty()) {
            // Set to default constructor
            this.hasDate = false;
            this.hasTime = false;

            this.calendar = Calendar.getInstance();
            this.calendar.set(0, 0, 0, 0, 0);
        } else {
            // Parse data from string
            String[] datetime_list = datetime.split(" ");
            String[] date_list = datetime_list[0].split("/");
            String[] time_list = datetime_list[1].split(":");

            // Set values
            this.calendar = Calendar.getInstance();
            // Set date values
            this.calendar.set(Calendar.YEAR, Integer.parseInt(date_list[0]));
            this.calendar.set(Calendar.MONTH, Integer.parseInt(date_list[1]));
            this.calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date_list[2]));

            // Set time values
            this.calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time_list[0]));
            this.calendar.set(Calendar.MINUTE, Integer.parseInt(time_list[1]));

            // Set hasDate
            this.hasDate = this.getDay() == 0;
            // Set hasTime
            this.hasTime = this.getHour() == 0;
        }
    }

    // Getters and Setters
    /**
     * Gets the datetime's year.
     * @return datetime's year
     */
    int getYear() {
        return this.calendar.get(Calendar.YEAR);
    }
    /**
     * Gets the datetime's month.
     * @return datetime's month
     */
    int getMonth() {
        return this.calendar.get(Calendar.MONTH);
    }
    /**
     * Gets the datetime's day of the month.
     * @return datetime's day of month
     */
    int getDay() {
        return this.calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Gets the datetime's 24 hour value.
     * @return datetime's 24 hour value
     */
    int getHour() {
        return this.calendar.get(Calendar.HOUR_OF_DAY);
    }
    /**
     * Gets the datetime's minute
     * @return datetime's minute
     */
    int getMinute() {
        return this.calendar.get(Calendar.MINUTE);
    }

    /**
     * Gets the value of {@link #hasDate}, which is set to true once date is
     * set by {@link #setYear(int)}, {@link #setMonth(int)},
     * {@link #setDay(int)} or during construction by
     * {@link #Datetime(Datetime)} or {@link #Datetime(String)}.
     * @return value of {@link #hasDate}
     */
    boolean getHasDate() {
        return this.hasDate;
    }
    /**
     * Gets the value of {@link #hasTime}, which is set to true once date is
     * set by {@link #setHour(int)}, {@link #setMinute(int)}, or during
     * construction by {@link #Datetime(Datetime)} or
     * {@link #Datetime(String)}.
     * @return value of {@link #hasDate}
     */
    boolean getHasTime() {
        return this.hasTime;
    }

    /**
     * Gets the datetime's calendar time in milliseconds.
     * @return datetime calendar time in milliseconds
     */
    long getMillis() {
        return this.calendar.getTimeInMillis();
    }

    /**
     * {@link #getYear()}
     * @param year datetime's year
     */
    void setYear(int year) {
        hasDate = true;
        this.calendar.set(Calendar.YEAR, year);
    }
    /**
     * {@link #getMonth()}
     * @param month datetime's month
     */
    void setMonth(int month) {
        hasDate = true;
        this.calendar.set(Calendar.MONTH, month);
    }
    /**
     * {@link #getDay()}
     * @param day datetime's day
     */
    void setDay(int day) {
        hasDate = true;
        this.calendar.set(Calendar.DAY_OF_MONTH, day);
    }

    /**
     * {@link #getHour()}
     * @param hour datetime's hour
     */
    void setHour(int hour) {
        this.hasTime = true;
        this.calendar.set(Calendar.HOUR_OF_DAY, hour);
    }
    /**
     * {@link #getMinute()}
     * @param minute datetime's minute
     */
    void setMinute(int minute) {
        this.hasTime = true;
        this.calendar.set(Calendar.MINUTE, minute);
    }

    /**
     * {@link #getMillis()}
     * @param millis datetime's time in milliseconds
     */
    void setMillis(long millis) {
        this.calendar.setTimeInMillis(millis);
    }

    /**
     * Returns the a new datetime instance with the period added.
     *
     * @see Period
     * @param period the period to add
     * @return new datetime instance with period added
     */
    Datetime add(Period period) {
        // Clone current calendar instance to calculate new datetime
        Calendar calendar = (Calendar) this.calendar.clone();
        // Add the period to the calendar
        calendar.add(Calendar.YEAR, period.getYears());
        calendar.add(Calendar.MONTH, period.getMonths());
        calendar.add(Calendar.DAY_OF_MONTH, period.getDays());

        calendar.add(Calendar.HOUR_OF_DAY, period.getHours());
        calendar.add(Calendar.MINUTE, period.getMinutes());
        // New datetime instance
        Datetime datetime = new Datetime();
        // Set calendar values
        datetime.setYear(calendar.get(Calendar.YEAR));
        datetime.setMonth(calendar.get(Calendar.MONTH));
        datetime.setDay(calendar.get(Calendar.DAY_OF_MONTH));

        datetime.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        datetime.setMinute(calendar.get(Calendar.MINUTE));

        return datetime;
    }

    /**
     * Returns the a new datetime instance with the period subtracted.
     *
     * @see Period
     * @param period the period to subtract
     * @return new datetime instance with period subtracted
     */
    Datetime subtract(Period period) {
        // Clone current calendar instance to calculate new datetime
        Calendar calendar = (Calendar) this.calendar.clone();
        // Subtract the period to the calendar
        calendar.add(Calendar.YEAR, -period.getYears());
        calendar.add(Calendar.MONTH, -period.getMonths());
        calendar.add(Calendar.DAY_OF_MONTH, -period.getDays());

        calendar.add(Calendar.HOUR_OF_DAY, -period.getHours());
        calendar.add(Calendar.MINUTE, -period.getMinutes());
        // New datetime instance
        Datetime datetime = new Datetime();
        // Set calendar values
        datetime.setYear(calendar.get(Calendar.YEAR));
        datetime.setMonth(calendar.get(Calendar.MONTH));
        datetime.setDay(calendar.get(Calendar.DAY_OF_MONTH));

        datetime.setHour(calendar.get(Calendar.HOUR_OF_DAY));
        datetime.setMinute(calendar.get(Calendar.MINUTE));

        return datetime;
    }

    /**
     * Returns the datetime's calendar year, month, day, hour and minute
     * values in the format "Y/M/D H:M" where the values do not have a fixed
     * width (they are not padded with zeroes).
     *
     * @return datetime's string equivalent
     */
    @Override
    public String toString() {
        return this.getYear() + "/" + this.getMonth() + "/" + this.getDay() + " " +
                this.getHour() + ":" + this.getMinute();
    }

    /**
     * Returns the datetime's calendar year, month, day, hour and minute
     * depending on whether they exist. The values are formatted in the format
     * "DD/MM/YY HH:MM" when all values are present, "DD/MM/YY" when only the
     * date is present and "HH:MM" when only the time is present.
     *
     * @return datetime formatted string
     */
    String toFormattedString() {
        String formattedString = "";
        // Add date if it is present
        if (this.getHasDate()) {
            formattedString += String.format(Locale.getDefault(),
                    "%1$ty/%1$tm/%1$td", this.calendar);
        }
        // Add time if it is present
        if (this.getHasTime()) {
            formattedString += String.format(Locale.getDefault(),
                    "%1$tH:%1$tM", this.calendar);
        }

        return formattedString;
    }
}
