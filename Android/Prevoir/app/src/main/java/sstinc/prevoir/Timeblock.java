package sstinc.prevoir;

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
class Timeblock extends Schedulable {
    private ArrayList<Task> tasks_scheduled;
    private Period period;
    private Period period_used;
    private Period period_left;

    /**
     * Default constructor. Instantiates all variables to empty values.
     */
    Timeblock() {
        this.tasks_scheduled = new ArrayList<>();
        this.period = new Period();
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
    Timeblock(Datetime scheduled_start, Datetime scheduled_stop) {
        this.scheduled_start = scheduled_start;
        this.scheduled_stop = scheduled_stop;
        this.tasks_scheduled = new ArrayList<>();

        // Calculate duration
        this.period = new Period(this.scheduled_stop.getMillis() -
                this.scheduled_start.getMillis());
        this.period_used = new Period();
        this.period_left = new Period(this.period);
    }

    // Getters and Setters
    /**
     * Gets the list of scheduled tasks within the timeblock.
     * @return array list of tasks scheduled
     */
    ArrayList<Task> getTasksScheduled() {
        return this.tasks_scheduled;
    }

    /**
     * Gets the period of the timeblock.
     * @return timeblock's period
     */
    Period getPeriod() {
        return this.period;
    }
    /**
     * Gets the period used by the tasks in the timeblock
     * @return timeblock's used period
     */
    Period getPeriodUsed() {
        return this.period_used;
    }
    /**
     * Gets the unused period of the timeblock by the tasks.
     * @return timeblock's available period
     */
    Period getPeriodLeft() {
        return this.period_left;
    }

    /**
     * Adds a new task to the timeblock. Recalculates the time used and time
     * left in the timeblock.
     *
     * @param task task to add
     * @return true if addition successful. false if there is not enough time
     * left in the timeblcok for the task to be added.
     */
    boolean addTask(Task task) {
        // Check if there is enough time left for the task
        if (this.period_left.getMillis() < task.getPeriodNeeded().getMillis()) {
            return false;
        }
        // Add to tasks_scheduled
        this.tasks_scheduled.add(task);

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
    void removeTask(Task task) {
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
