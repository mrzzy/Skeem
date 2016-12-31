package sstinc.skeem.models;

import org.joda.time.Period;

import java.util.ArrayList;

/**
 * This class handles the addition and removal of a task within a timeblock,
 * the time between two voidblocks. This class extends {@link Schedulable}.
 *
 * @see Task
 * @see Voidblock
 * @see Schedulable
 */
public class Timeblock extends Schedulable {
    private ArrayList<Task> tasks_scheduled;
    private Period period_used;
    private Period period_left;

    /**
     * Default constructor. Instantiates all variables to empty values.
     */
    public Timeblock() {
        super();
        this.tasks_scheduled = new ArrayList<>();
        this.period_used = new Period();
        this.period_left = new Period();
    }

    /**
     * Datetime constructor. Sets the start and stop time of the timeblock.
     * This start and stop time should be the stop time of the previous
     * voidblock and the start time of the next voidblock.
     *
     * @param scheduled_start timeblock's start time
     * @param scheduled_stop timeblock's stop time
     */
    public Timeblock(Datetime scheduled_start, Datetime scheduled_stop) {
        super();
        this.scheduled_start = scheduled_start;
        this.scheduled_stop = scheduled_stop;
        this.tasks_scheduled = new ArrayList<>();

        // Calculate duration
        this.period_used = new Period();
        this.period_left = new Period(this.getScheduledPeriod());
    }

    // Getters and Setters
    /**
     * Gets the list of scheduled tasks within the timeblock.
     * @return array list of tasks scheduled
     */
    public ArrayList<Task> getTasksScheduled() {
        return this.tasks_scheduled;
    }

    /**
     * Gets the period used by the tasks in the timeblock
     * @return timeblock's used period
     */
    public Period getPeriodUsed() {
        return this.period_used;
    }

    /**
     * Gets the unused period of the timeblock by the tasks.
     * @return timeblock's available period
     */
    public Period getPeriodLeft() {
        return this.period_left;
    }

    /**
     * Gets the unused period of the timeblock by the tasks before a certain
     * datetime.
     * @param before datetime before the timeblock ends
     * @return timeblock's available period before given datetime
     */
    public Period getPeriodLeft(Datetime before) {
        if (this.getScheduledStop().getMillis() <= before.getMillis()) {
            return this.period_left;
        } else if (this.getScheduledStart().getMillis() >= before.getMillis()) {
            return new Period();
        } else {
            Period periodLeftBeforeTime = new Period(this.period_left);
            return periodLeftBeforeTime.minus(before.getDifference(this.getScheduledStop()));
        }
    }

    /**
     * Get the timeblocks where each timeblock has a maximum duration of 1
     * day. Each timeblock does not cross over two days.
     * @return arraylist of timeblocks that do not cross over two days
     */
    public ArrayList<Timeblock> getSingleDayTimeblocks() {
        ArrayList<Timeblock> timeblocks = new ArrayList<>();
        if (this.getScheduledStart().compareDates(this.getScheduledStop()) == 0) {
            timeblocks.add(this);
        } else {
            Datetime startDatetime = new Datetime(this.getScheduledStart());
            while (startDatetime.compareDates(this.getScheduledStop()) != 0) {
                // End of the day for the start datetime
                Datetime endOfDay = new Datetime(startDatetime);
                endOfDay.setHour(23);
                endOfDay.setMinute(59);

                timeblocks.add(new Timeblock(startDatetime, endOfDay));

                // Move to next day
                Datetime nextStartOfDay = new Datetime(endOfDay);
                nextStartOfDay = nextStartOfDay.add(new Period().plusDays(1));
                nextStartOfDay.setHour(0);
                nextStartOfDay.setMinute(0);
                startDatetime = nextStartOfDay;
            }

            timeblocks.add(new Timeblock(startDatetime, this.getScheduledStop()));
        }

        return timeblocks;
    }

    /**
     * Sets the scheduled start datetime and recalculates the total period
     * and period left if there is a set scheduled stop.
     *
     * @param datetime new scheduled start datetime to set
     */
    @Override
    public void setScheduledStart(Datetime datetime) {
        this.scheduled_start = datetime;
        // Get the new periods
        // If the scheduled stop is set
        if (this.scheduled_stop.getMillis() != new Period().getMillis()) {
            // Recalculate the period and period left
            this.period_left = this.getScheduledPeriod().minus(this.period_used);
        }
    }

    /**
     * Sets the scheduled stop datetime and recalculates the total period and
     * period left if there is a set scheduled start.
     *
     * @param datetime new scheduled stop datetime to set
     */
    @Override
    public void setScheduledStop(Datetime datetime) {
        this.scheduled_stop = datetime;
        // Get the new periods
        // If the scheduled start is set
        if (this.scheduled_start.getMillis() != new Period().getMillis()) {
            // Recalculate the period and period left
            this.period_left = this.getScheduledPeriod().minus(this.period_used);
        }
    }

    /**
     * Adds a new task to the timeblock. Recalculates the time used and time
     * left in the timeblock and set the scheduled start and stop of the task.
     *
     * @param task task to add
     * @return true if addition successful. false if there is not enough time
     * left in the timeblcok for the task to be added.
     */
    public boolean addTask(Task task) {
        // Check if there is enough time left for the task
        if (this.period_left.getMillis() < task.getScheduledPeriod().getMillis()) {
            return false;
        }
        // Add to tasks_scheduled
        this.tasks_scheduled.add(task);

        // Set the new scheduled start and stop
        task.setScheduledStart(this.scheduled_start.add(this.period_used));
        task.setScheduledStop(this.scheduled_start.add(this.period_used).add(
                task.getPeriodNeeded()));

        // Recalculate time used and time left
        this.period_left = this.period_left.minus(task.getPeriodNeeded());
        this.period_used = this.period_used.plus(task.getPeriodNeeded());

        return true;
    }

    /**
     * Removes a task from the tasks scheduled. Recalculates the time used
     * and time left in the timeblock.
     *
     * @param task task to remove
     */
    public void removeTask(Task task) {
        // Returns if the task is not in the tasks scheduled
        if (!this.tasks_scheduled.contains(task)) {
            return;
        }

        // Remove task from tasks scheduled
        this.tasks_scheduled.remove(task);

        // Recalculate time used and time left
        this.period_left = this.period_left.plus(task.getPeriodNeeded());
        this.period_used = this.period_used.minus(task.getPeriodNeeded());
    }
}
