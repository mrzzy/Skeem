package sstinc.skeem;

import android.os.Parcel;
import android.os.Parcelable;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import java.util.ArrayList;
import java.util.Arrays;

//TODO: Check dependencies on deadline per day

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
    boolean checked = false;
    private String name;
    private String description;
    private String subject;

    private WeekDays weekDays;
    private Datetime deadline_per_day;

    private Period period_needed;
    private Period period_minimum;

    private Datetime deadline;

    /**
     * Default constructor. Sets the name, description and subject to empty
     * strings. Instantiates the other variables with their default empty
     * constructors.
     */
    Task() {
        super();
        this.name = "";
        this.description = "";
        this.subject = "";

        this.weekDays = new WeekDays();
        this.deadline_per_day = new Datetime();

        this.period_needed = new Period();
        this.period_minimum = new Period();

        this.deadline = new Datetime();
    }

    // Copy constructor
    Task(Task task) {
        super(task);
        this.name = task.getName();
        this.description = task.getDescription();
        this.subject = task.getSubject();

        this.weekDays = new WeekDays(task.getWeekDays());
        this.deadline_per_day = new Datetime(task.getDeadlinePerDay());

        this.period_needed = new Period(task.getPeriodNeeded());
        this.period_minimum = new Period(task.getPeriodMinimum());

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

        this.weekDays = new WeekDays();
        this.deadline_per_day = new Datetime();

        this.period_needed = new Period();
        this.period_minimum = new Period();

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
     * Gets the task's deadline each weekday.
     * @return task's deadline each weekday
     */
    Datetime getDeadlinePerDay() {
        return this.deadline_per_day;
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
     * {@link #getDeadlinePerDay()}
     * @param deadline_per_day task's deadline per day
     */
    void setDeadlinePerDay(Datetime deadline_per_day) {
        this.deadline_per_day = deadline_per_day;
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

    /**
     * Checks if the current task is repeated.
     * @return true if the task instance is not repeated. False otherwise.
     */
    boolean isRepeated() {
        return !this.weekDays.getWeekDays_list().isEmpty();
    }

    //TODO: Documentation
    Task[] getSeparatedRepeatedTasks() {
        // List of tasks
        ArrayList<Task> tasks = new ArrayList<>();
        // Convert repeated days to integer
        ArrayList<Integer> repeated_days = new ArrayList<>();
        ArrayList<WeekDays.WeekDay> weekdays_index =
                new ArrayList<>(Arrays.asList(WeekDays.WeekDay.values()));
        for (WeekDays.WeekDay weekDay : this.weekDays.getWeekDays_list()) {
            repeated_days.add(weekdays_index.indexOf(weekDay) + 1);
        }

        DateTime dateTime = new DateTime();
        while (dateTime.isBefore(this.deadline.getMillis())) {
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
                    Task new_task = new Task(this);
                    Datetime datetime = new Datetime(dateTime);
                    datetime.setHasTime(true);
                    new_task.setScheduledStart(datetime);
                    tasks.add(new_task);
                } else if (difference > 0){
                    // the repeated day is a few days later, move to the day
                    dateTime = dateTime.plus(difference);
                }
            }
        }

        return tasks.toArray(new Task[tasks.size()]);
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
        // Write name, description and subject as strings
        out.writeString(this.name);
        out.writeString(this.description);
        out.writeString(this.subject);
        // Write the weekDays as a string array
        out.writeStringArray(this.weekDays.toStringArray());
        // Write the deadline per day as a parcelable
        out.writeParcelable(this.deadline_per_day, flags);
        // Use the default period format and write that string
        out.writeString(PeriodFormat.getDefault().print(this.period_needed));
        out.writeString(PeriodFormat.getDefault().print(this.period_minimum));
        // Convert datetime values to string and write them
        out.writeParcelable(this.deadline, flags);
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
        // Copy superclass
        super(readSchedulableFromParcel(in));
        // Read name, description and subject as strings
        this.name = in.readString();
        this.description = in.readString();
        this.subject = in.readString();
        // Create new WeekDays instance from string array
        this.weekDays = new WeekDays(in.createStringArray());
        // Create datetime objects from strings
        this.deadline_per_day = in.readParcelable(Datetime.class.getClassLoader());
        // Parse the Periods with the default period format
        this.period_needed = PeriodFormat.getDefault().parsePeriod(in.readString());
        this.period_minimum = PeriodFormat.getDefault().parsePeriod(in.readString());
        // Create datetime objects from datetime strings
        this.deadline = in.readParcelable(Datetime.class.getClassLoader());
        // Read the id of the task
        this.id = in.readLong();
    }
}
