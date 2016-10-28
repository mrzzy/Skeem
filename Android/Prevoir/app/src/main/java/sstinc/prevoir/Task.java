package sstinc.prevoir;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormatter;

import java.util.ArrayList;

//TODO: Remove Deadline Class

/**
 * This class handles information relating to each task. Each task has a
 * name, description, subject, deadline, period needed and minimum period
 * needed. Once the task is scheduled, it would have a scheduled time to
 * start and stop as well. The task extends {@link Schedulable} and
 * implements {@link Parcelable}.
 *
 * @see Schedulable
 * @see Parcelable
 * @see Period
 * @see Datetime
 */
class Task extends Schedulable implements Parcelable {
    private final PeriodFormatter string_format = new PeriodFormatter();

    private String name;
    private String description;
    private String subject;

    private WeekDays weekDays;

    private Period period_needed;
    private Period period_minimum;

    private Datetime scheduled_start;
    private Datetime scheduled_stop;
    private Datetime deadline;

    /**
     * Default constructor. Sets the name, description and subject to empty
     * strings. Instantiates the other variables with their default empty
     * constructors.
     */
    Task() {
        this.name = "";
        this.description = "";
        this.subject = "";

        this.weekDays = new WeekDays();

        this.period_needed = new Period();
        this.period_minimum = new Period();

        this.scheduled_start = new Datetime();
        this.scheduled_stop = new Datetime();
        this.deadline = new Datetime();
    }

    // Copy constructor
    Task(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.subject = task.getSubject();

        this.weekDays = new WeekDays(task.getWeekDays());

        this.period_needed = new Period(task.getPeriodNeeded());
        this.period_minimum = new Period(task.getPeriodMinimum());

        this.scheduled_start = new Datetime(task.getScheduledStart());
        this.scheduled_stop = new Datetime(task.getScheduledStop());
        this.deadline = new Datetime(task.getDeadline());
    }

    /**
     * Basic constructor. Allows you to set the name, description and
     * subject. Instantiates the other variables with their default empty
     * constructors.
     *
     * @param name the task's name
     * @param description the task's description
     * @param subject the task's subject
     */
    Task(String name, String description, String subject) {
        this.name = name;
        this.description = description;
        this.subject = subject;

        this.period_needed = new Period();
        this.period_minimum = new Period();

        this.scheduled_start = new Datetime();
        this.scheduled_stop = new Datetime();
        this.deadline = new Datetime();
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
        out.writeString(this.name);
        out.writeString(this.description);
        out.writeString(this.subject);

        out.writeStringArray(this.weekDays.toStringArray());

        //TODO

        out.writeString(name);
        out.writeString(subject);
        // Convert weekDays to string
        ArrayList<String> arrayList_string_weekDays = new ArrayList<>();
        for (WeekDay weekDay : weekDays) {
            arrayList_string_weekDays.add(weekDay.toString());
        }
        String[] string_weekDays = new String[arrayList_string_weekDays.size()];
        string_weekDays = arrayList_string_weekDays.toArray(string_weekDays);
        out.writeStringArray(string_weekDays);
        out.writeString(deadline.getDeadline().toString());
        out.writeString(description);
        out.writeString(duration.toString());
        out.writeString(min_time_period.toString());
        out.writeLong(this.getId());
    }

    // Creator constant for parcel
    public static final Parcelable.Creator<Task> CREATOR = new Parcelable.Creator<Task>() {
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    /**
     * Function to read data from the task's parcel.
     *
     * @param in parcel to read from
     */
    private Task(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.subject = in.readString();

        //TODO
        this.weekDays = in.readStringArray();;

        this.weekDays = new ArrayList<>();
        String[] string_weekDays = in.createStringArray();
        for (String string_weekday : string_weekDays) {
            this.weekDays.add(WeekDay.valueOf(string_weekday));
        }
        this.deadline = new Deadline(new Datetime(in.readString()));
        this.description = in.readString();
        this.duration = new Duration(in.readString());
        this.min_time_period = new Duration(in.readString());
        setId(in.readLong());
    }
}
