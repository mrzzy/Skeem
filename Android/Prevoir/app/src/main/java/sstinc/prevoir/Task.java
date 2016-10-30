package sstinc.prevoir;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

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

    // Getters and Setters
    /**
     * Gets the task's name.
     * @return task's name
     */
    String getName() {
        return this.name;
    }
    /**
     * Gets the task's description.
     * @return task's description
     */
    String getDescription() {
        return this.description;
    }
    /**
     * Gets the task's subject.
     * @return task's subject
     */
    String getSubject() {
        return this.subject;
    }

    /**
     * Gets the task's repeated weekdays.
     * @return task's repeated weekdays
     */
    WeekDays getWeekDays() {
        return this.weekDays;
    }

    /**
     * Gets the period needed for the task to be completed
     * @return period needed for task to be completed
     */
    Period getPeriodNeeded() {
        return this.period_needed;
    }
    /**
     * Gets the user's recommended time to be spent on this task for each
     * timeblock.
     * @return recommended time to be spent on task
     */
    Period getPeriodMinimum() {
        return this.period_minimum;
    }

    /**
     * Gets the task's deadline
     * @return task's deadline
     */
    Datetime getDeadline() {
        return this.deadline;
    }

    /**
     * {@link #getName()}
     * @param name task's name
     */
    void setName(String name) {
        this.name = name;
    }
    /**
     * {@link #getDescription()}
     * @param description task's description
     */
    void setDescription(String description) {
        this.description = description;
    }
    /**
     * {@link #getSubject()}
     * @param subject task's subject
     */
    void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * {@link #getWeekDays()}
     * @param weekDays task's weekDays instance
     */
    void setWeekDays(WeekDays weekDays) {
        this.weekDays = weekDays;
    }

    /**
     * {@link #getPeriodNeeded()}
     * @param period_needed period needed for task
     */
    void setPeriodNeeded(Period period_needed) {
        this.period_needed = period_needed;
    }
    /**
     * {@link #getPeriodMinimum()}
     * @param period_minimum recommended period for task
     */
    void setPeriodMinimum(Period period_minimum) {
        this.period_minimum = period_minimum;
    }

    /**
     * {@link #getDeadline()}
     * @param deadline task's deadline
     */
    void setDeadline(Datetime deadline) {
        this.deadline = deadline;
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
        // Write name, description and subject as strings
        out.writeString(this.name);
        out.writeString(this.description);
        out.writeString(this.subject);
        // Write the weekDays as a string array
        out.writeStringArray(this.weekDays.toStringArray());
        // Use the default period format and write that string
        out.writeString(PeriodFormat.getDefault().print(this.period_needed));
        out.writeString(PeriodFormat.getDefault().print(this.period_minimum));
        // Convert datetime values to string and write them
        out.writeString(this.scheduled_start.toString());
        out.writeString(this.scheduled_stop.toString());
        out.writeString(this.deadline.toString());
        // Write the current id of the task
        out.writeLong(this.id);
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
     * Constructor to create instance from data from parcel.
     *
     * @param in parcel to read from
     */
    private Task(Parcel in) {
        // Read name, description and subject as strings
        this.name = in.readString();
        this.description = in.readString();
        this.subject = in.readString();
        // Create new WeekDays instance from string array
        this.weekDays = new WeekDays(in.createStringArray());
        // Parse the Periods with the default period format
        this.period_needed = PeriodFormat.getDefault().parsePeriod(in.readString());
        this.period_minimum = PeriodFormat.getDefault().parsePeriod(in.readString());
        // Create datetime strings to datetime objects
        this.scheduled_start = new Datetime(in.readString());
        this.scheduled_stop = new Datetime(in.readString());
        this.deadline = new Datetime(in.readString());
        // Read the id of the task
        this.id = in.readLong();
    }
}
