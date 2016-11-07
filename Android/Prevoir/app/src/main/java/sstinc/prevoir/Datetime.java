package sstinc.prevoir;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.Calendar;
import java.util.Locale;

//TODO: Locale datetime string format
//TODO: Check dependencies for datetime parcelable

/**
 * This class handles information relating to an instance in time using
 * Calendar. Minutes is the smallest denomination this class handles. The
 * class is parcelable.
 *
 * @see Calendar
 * @see Parcelable
 */
class Datetime implements Parcelable {
    private static final DateTimeFormatter default_format = DateTimeFormat.forPattern(
            "yyyy-MM-dd HH:mm:ss");

    private org.joda.time.DateTime datetime;
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

        this.datetime = new org.joda.time.DateTime(0);
    }

    // Copy constructor
    Datetime(Datetime datetime) {
        if (datetime == null) {
            this.hasDate = false;
            this.hasTime = false;

            this.datetime = new org.joda.time.DateTime(0);
        } else {
            this.datetime = new org.joda.time.DateTime(datetime.getMillis());

            this.hasDate = datetime.getHasDate();
            this.hasTime = datetime.getHasTime();
        }
    }

    Datetime(org.joda.time.DateTime datetime) {
        if (datetime == null) {
            this.hasDate = false;
            this.hasTime = false;

            this.datetime = new org.joda.time.DateTime(0);
        } else {
            this.hasDate = true;
            this.hasTime = true;

            this.datetime = datetime;
        }
    }

    /**
     * String constructor. Creates the object by parsing the string passed.
     * Uses default empty constructor {@link #Datetime()} if string is empty.
     *
     * @param datetime The string of the datetime. Can be obtained by the
     *                 {@link #toString} method.
     */
    Datetime(String datetime) {
        if (datetime == null || datetime.isEmpty()) {
            // Set to default constructor
            this.hasDate = false;
            this.hasTime = false;

            this.datetime = new org.joda.time.DateTime(0);
        } else {
            // Parse data from string
            String[] datetime_list = datetime.split(" ");
            String[] date_list = datetime_list[0].split("/");
            String[] time_list = datetime_list[1].split(":");

            // Set values
            this.datetime = new org.joda.time.DateTime(Integer.parseInt(date_list[0]), // Year
                                                       Integer.parseInt(date_list[1]), // Month
                                                       Integer.parseInt(date_list[2]), // Day
                                                       Integer.parseInt(time_list[0]), // Hour
                                                       Integer.parseInt(time_list[1]));// Minute
            // Set hasDate
            this.hasDate = this.getDay() != 0;
            // Set hasTime
            this.hasTime = this.getHour() != 0;
        }
    }

    // Getters and Setters
    /**
     * Gets the datetime's year.
     * @return datetime's year
     */
    int getYear() {
        return this.datetime.getYear();
    }
    /**
     * Gets the datetime's month.
     * @return datetime's month
     */
    int getMonth() {
        return this.datetime.getMonthOfYear();
    }
    /**
     * Gets the datetime's day of the month.
     * @return datetime's day of month
     */
    int getDay() {
        return this.datetime.getDayOfMonth();
    }

    /**
     * Gets the datetime's 24 hour value.
     * @return datetime's 24 hour value
     */
    int getHour() {
        return this.datetime.getHourOfDay();
    }
    /**
     * Gets the datetime's minute
     * @return datetime's minute
     */
    int getMinute() {
        return this.datetime.getMinuteOfHour();
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
        return this.datetime.getMillis();
    }

    /**
     * {@link #getYear()}
     * @param year datetime's year
     */
    void setYear(int year) {
        hasDate = true;
        this.datetime = this.datetime.withYear(year);
    }
    /**
     * {@link #getMonth()}
     * @param month datetime's month
     */
    void setMonth(int month) {
        hasDate = true;
        this.datetime = this.datetime.withMonthOfYear(month);
    }
    /**
     * {@link #getDay()}
     * @param day datetime's day
     */
    void setDay(int day) {
        hasDate = true;
        this.datetime = this.datetime.withDayOfMonth(day);
    }

    /**
     * {@link #getHour()}
     * @param hour datetime's hour
     */
    void setHour(int hour) {
        this.hasTime = true;
        this.datetime = this.datetime.withHourOfDay(hour);
    }
    /**
     * {@link #getMinute()}
     * @param minute datetime's minute
     */
    void setMinute(int minute) {
        this.hasTime = true;
        this.datetime = this.datetime.withMinuteOfHour(minute);
    }

    /**
     * {@link #getMillis()}
     * @param millis datetime's time in milliseconds
     */
    void setMillis(long millis) {
        this.hasDate = true;
        this.hasTime = true;
        this.datetime = new org.joda.time.DateTime(millis);
    }

    /**
     * Returns the a new datetime instance with the period added.
     *
     * @see Period
     * @param period the period to add
     * @return new datetime instance with period added
     */
    Datetime add(Period period) {
        return new Datetime(this.datetime.withPeriodAdded(period, 1));
    }

    /**
     * Returns the a new datetime instance with the period subtracted.
     *
     * @see Period
     * @param period the period to subtract
     * @return new datetime instance with period subtracted
     */
    Datetime subtract(Period period) {
        return new Datetime(this.datetime.withPeriodAdded(period, -1));
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
            this.datetime.toCalendar(Locale.getDefault());
            formattedString += String.format(Locale.getDefault(),
                    "%1$td/%1$tm/%1$ty", this.datetime.toCalendar(Locale.getDefault()));
        }

        // Add a spacing in between if both exist
        if (this.getHasDate() && this.getHasTime()) {
            formattedString += " ";
        }

        // Add time if it is present
        if (this.getHasTime()) {
            formattedString += String.format(Locale.getDefault(),
                    "%1$tH:%1$tM", this.datetime.toCalendar(Locale.getDefault()));
        }

        return formattedString;
    }

    // Empty describe contents function for parcel
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Function to write the data into a parcelable object.
     *
     * @param out parcel to write to
     * @param flags other flags
     */
    @Override
    public void writeToParcel(Parcel out, int flags) {
        // Write hasDate and hasTime
        boolean[] hasDateHasTime = {this.hasDate, this.hasTime};
        out.writeBooleanArray(hasDateHasTime);
        // Write datetime
        out.writeString(default_format.print(this.datetime));

    }

    // Creator constant for parcel
    public static final Parcelable.Creator<Datetime> CREATOR = new Parcelable.Creator<Datetime>() {
        public Datetime createFromParcel(Parcel in) {
            return new Datetime(in);
        }

        public Datetime[] newArray(int size) {
            return new Datetime[size];
        }
    };

    /**
     * Constructor to create instance from data from parcel.
     *
     * @param in parcel to read from
     */
    private Datetime(Parcel in) {
        // Read hasDate and hasTime
        boolean[] hasDateHasTime = in.createBooleanArray();
        this.hasDate = hasDateHasTime[0];
        this.hasTime = hasDateHasTime[1];
        // Parse datetime
        this.datetime = default_format.parseDateTime(in.readString());
    }
}
