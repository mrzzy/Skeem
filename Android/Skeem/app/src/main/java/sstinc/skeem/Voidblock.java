package sstinc.skeem;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class handles the user's voidblocks. Each voidblock has a name and a
 * set of repeated weekdays. This class extends {@link Schedulable} and
 * implements {@link Parcelable}.
 *
 * @see WeekDays
 * @see Schedulable
 * @see Parcelable
 */
class Voidblock extends Schedulable implements Parcelable {
    boolean checked = false;
    private String name;
    private WeekDays weekDays;

    /**
     * Default constructor. Sets the name to an empty string and instantiates
     * weekdays.
     */
    Voidblock() {
        super();
        this.name = "";
        this.weekDays = new WeekDays();
    }

    // Copy constructor
    Voidblock(Voidblock voidblock) {
        super(voidblock);
        this.name = voidblock.getName();
        this.weekDays = new WeekDays(voidblock.getWeekDays());
    }

    // Getters and Setters

    /**
     * Gets the voidblock's name.
     * @return voidblock's name
     */
    String getName() {
        return this.name;
    }
    /**
     * Gets the voidblock's repeated weekdays.
     * @return voidblock's repeated weekdays
     */
    WeekDays getWeekDays() {
        return this.weekDays;
    }

    /**
     * {@link #getName()}
     * @param name voidblock's name
     */
    void setName(String name) {
        this.name = name;
    }
    /**
     * {@link #getWeekDays()}
     * @param weekDays voidblock's weekDays instance
     */
    void setWeekDays(WeekDays weekDays) {
        this.weekDays = weekDays;
    }

    /**
     * Checks if the current voidblock is repeated.
     * @return true if the task instance is not repeated. False otherwise.
     */
    boolean isRepeated() {
        return !this.weekDays.getWeekDays_list().isEmpty();
    }

    //TODO: Documentation
    Voidblock[] getSeparatedRepeatedVoidblocks(Datetime end_datetime) {
        // List of voidblocks
        ArrayList<Voidblock> voidblocks = new ArrayList<>();
        // Convert repeated days to integer
        ArrayList<Integer> repeated_days = new ArrayList<>();
        ArrayList<WeekDays.WeekDay> weekdays_index =
                new ArrayList<>(Arrays.asList(WeekDays.WeekDay.values()));
        for (WeekDays.WeekDay weekDay : this.weekDays.getWeekDays_list()) {
            repeated_days.add(weekdays_index.indexOf(weekDay) + 1);
        }

        DateTime dateTime = new DateTime();
        while (dateTime.isBefore(end_datetime.getMillis())) {
            for (int day_value : repeated_days) {
                int difference;
                // Calculate number of days to the next repeated weekday
                if (day_value < dateTime.getDayOfWeek()) {
                    difference = day_value + dateTime.getDayOfWeek() -7;
                } else {
                    difference = dateTime.getDayOfWeek() - day_value;
                }
                if (difference < 0) {
                    // if difference is < 0, the next day is before today, move to
                    // the next repeated day
                    // 1, 2, 3, 4, 5, 6, 7
                    dateTime = dateTime.plusDays(-difference);
                } else if (difference == 0) {
                    // today is a repeated day, add it to the list of tasks
                    Voidblock new_voidblock = new Voidblock(this);
                    // Set scheduled start and stop
                    new_voidblock.getScheduledStart().setYear(dateTime.getYear());
                    new_voidblock.getScheduledStart().setMonth(dateTime.getMonthOfYear());
                    new_voidblock.getScheduledStart().setDay(dateTime.getDayOfMonth());

                    new_voidblock.getScheduledStop().setYear(dateTime.getYear());
                    new_voidblock.getScheduledStop().setMonth(dateTime.getMonthOfYear());
                    new_voidblock.getScheduledStop().setDay(dateTime.getDayOfMonth());

                    voidblocks.add(new_voidblock);
                } else if (difference > 0){
                    // the repeated day is a few days later, move to the day
                    dateTime = dateTime.plus(difference);
                }
            }
        }

        return voidblocks.toArray(new Voidblock[voidblocks.size()]);
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
        // Write superclass
        writeSchedulableToParcel(out, flags);
        // Write name as string
        out.writeString(this.name);
        // Write weekdays as string array
        out.writeStringArray(this.weekDays.toStringArray());
        // Write the current id of the task
        out.writeLong(this.id);
    }

    // Creator constant for parcel
    public static final Parcelable.Creator<Voidblock> CREATOR =
            new Parcelable.Creator<Voidblock>() {
        public Voidblock createFromParcel(Parcel in) {
            return new Voidblock(in);
        }

        public Voidblock[] newArray(int size) {
            return new Voidblock[size];
        }
    };

    /**
     * Constructor to create instance from data from parcel.
     *
     * @param in parcel to read from
     */
    private Voidblock(Parcel in) {
        // Read superclass
        super(readSchedulableFromParcel(in));
        // Read name as string
        this.name = in.readString();
        // Read weekdays as string array
        this.weekDays = new WeekDays(in.createStringArray());
        // Read the id of the task
        this.id = in.readLong();
    }
}
