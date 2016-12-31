package sstinc.skeem.schedule;

import android.content.Context;

import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Collections;

import sstinc.skeem.adapters.DbAdapter;
import sstinc.skeem.models.Datetime;
import sstinc.skeem.models.Schedulable;
import sstinc.skeem.models.Task;
import sstinc.skeem.models.Timeblock;
import sstinc.skeem.models.Voidblock;
import sstinc.skeem.utils.TaskComparator;
import sstinc.skeem.utils.VoidblockComparator;

/**
 * This class superclasses all schedule sorting algorithms and handles their
 * respective function calls. It also provides a layer of abstraction.
 */
public abstract class Scheduler {
    private String name;
    private ArrayList<Task> tasks;
    private ArrayList<Task> tasksWithExpandedRepeats;
    private ArrayList<Task> repeatedTasksExpanded;
    private ArrayList<Voidblock> voidblocks;
    private ArrayList<Voidblock> expandedVoidblocks;
    private ArrayList<Timeblock> timeblocks;
    private ArrayList<Schedulable> emptySchedule;


    /**
     * Default constructor.
     * @param ctx activity context used to access the database
     * @param name name of the schedule that will be displayed to user
     */
    Scheduler(Context ctx, String name) {
        // Create the private variables
        this.name = name;

        // Access database to get tasks and voidblocks
        DbAdapter dbAdapter = new DbAdapter(ctx);
        dbAdapter.open();
        this.tasks = dbAdapter.getTasks();
        this.voidblocks = dbAdapter.getVoidblocks();
        dbAdapter.close();

        this.tasksWithExpandedRepeats = new ArrayList<>();
        this.repeatedTasksExpanded = new ArrayList<>();
        this.expandedVoidblocks = new ArrayList<>();
        this.timeblocks = new ArrayList<>();
        this.emptySchedule = new ArrayList<>();

        // Expand voidblocks
        if (this.tasks.size() > 0) {
            Datetime latestDeadline = this.tasks.get(this.tasks.size()-1).getDeadline();
            for (Voidblock voidblock : this.voidblocks) {
                if (voidblock.isRepeated()) {
                    Collections.addAll(this.expandedVoidblocks,
                            voidblock.getSeparatedRepeatedVoidblocks(latestDeadline));
                } else {
                    this.expandedVoidblocks.add(voidblock);
                }
            }
            // Sort the expanded voidblocks, most recent first
            VoidblockComparator voidBlockComparator = new VoidblockComparator();
            voidBlockComparator.setSortBy(VoidblockComparator.Order.SCHEDULED_START, true);
            Collections.sort(this.expandedVoidblocks, voidBlockComparator);
        } else {
            this.expandedVoidblocks = this.voidblocks;
        }

        // Expand tasks
        for (Task task : this.tasks) {
            if (task.isRepeated()) {
                Collections.addAll(this.repeatedTasksExpanded, task.getSeparatedRepeatedTasks());
            }
        }

        // Sort the repeated tasks expanded
        TaskComparator repeatedTaskComparator = new TaskComparator();
        repeatedTaskComparator.setSortBy(TaskComparator.Order.SCHEDULED_START, true);
        Collections.sort(this.repeatedTasksExpanded, repeatedTaskComparator);

        // Create emptySchedule and timeblocks
        for (int i=0;i<this.expandedVoidblocks.size();i++) {

            if (i == 0) {
                if (Datetime.getCurrentDatetime().getMillis() <
                        this.expandedVoidblocks.get(i).getScheduledStart().getMillis()) {
                    Timeblock timeblockToAdd = new Timeblock();
                    timeblockToAdd.setScheduledStart(Datetime.getCurrentDatetime());
                    timeblockToAdd.setScheduledStop(
                            this.expandedVoidblocks.get(i).getScheduledStart());

                    this.emptySchedule.addAll(timeblockToAdd.getSingleDayTimeblocks());
                }
                this.emptySchedule.add(this.expandedVoidblocks.get(i));
            } else {
                Voidblock prev_voidblock = this.expandedVoidblocks.get(i-1);
                Voidblock curr_voidblock = this.expandedVoidblocks.get(i);

                Timeblock timeblockToAdd = new Timeblock(prev_voidblock.getScheduledStop(),
                        curr_voidblock.getScheduledStart());

                if (!timeblockToAdd.getScheduledPeriod().equals(new Period())) {
                    this.emptySchedule.addAll(timeblockToAdd.getSingleDayTimeblocks());
                    this.emptySchedule.add(curr_voidblock);
                    this.timeblocks.addAll(timeblockToAdd.getSingleDayTimeblocks());
                } else {
                    this.emptySchedule.add(curr_voidblock);
                }
            }
        }

        // Add an ending timeblock for the day
        if (this.expandedVoidblocks.size() != 0) {
            Voidblock lastVoidblock = this.expandedVoidblocks.get(this.expandedVoidblocks.size()-1);
            if (lastVoidblock.getScheduledStop().getHour() != 23 ||
                    lastVoidblock.getScheduledStop().getMinute() != 59) {
                Datetime endDatetime = new Datetime(lastVoidblock.getScheduledStop());
                endDatetime.setHour(23);
                endDatetime.setMinute(59);

                Timeblock timeblock = new Timeblock();
                timeblock.setScheduledStart(lastVoidblock.getScheduledStop());
                timeblock.setScheduledStop(endDatetime);

                this.emptySchedule.add(timeblock);
            }
        } else if (this.tasks.size() != 0) {
            // Add a timeblock that can be filled with all the tasks if there
            // are no voidblocks
            Datetime lastDeadline = this.tasks.get(this.tasks.size()-1).getDeadline();
            Timeblock timeblock = new Timeblock(Datetime.getCurrentDatetime(), lastDeadline);
            this.emptySchedule.add(timeblock);
        }

        // Remove all voidblocks before now
        this.emptySchedule = removeBeforeNow(this.emptySchedule);
        if (this.emptySchedule.size() != 0 && this.emptySchedule.get(0) instanceof Timeblock) {
            this.emptySchedule.get(0).setScheduledStart(Datetime.getCurrentDatetime());
        }
    }

    /**
     * Removes the timeblocks that contain the tasks.
     * @param timeblockSchedule the schedule to expand timeblocks
     * @return schedule with all the tasks from the timeblocks moved out and
     * timeblocks removed
     */
    private static ArrayList<Schedulable> removeTimeblocks(ArrayList<Schedulable> timeblockSchedule) {
        ArrayList<Schedulable> schedule = new ArrayList<>();
        for (Schedulable schedulable : timeblockSchedule) {
            if (schedulable instanceof Timeblock) {
                Timeblock timeblock = (Timeblock) schedulable;
                schedule.addAll(timeblock.getTasksScheduled());
            } else {
                schedule.add(schedulable);
            }
        }
        return schedule;
    }

    /**
     * Removes any schedulable that ends before the current Datetime.
     * @param fullSchedule schedule to filter
     * @return schedule without any schedulable before now
     */
    private static ArrayList<Schedulable> removeBeforeNow(ArrayList<Schedulable> fullSchedule) {
        ArrayList<Schedulable> schedule = new ArrayList<>();
        for (Schedulable schedulable : fullSchedule) {
            if (schedulable.getScheduledStop().getMillis() >
                    Datetime.getCurrentDatetime().getMillis()) {
                schedule.add(schedulable);
            }
        }

        return schedule;
    }

    /**
     * Apply all the filters to the schedule before sending it to the adapter.
     * @param unfilteredSchedule schedule with all the tasks scheduled
     * @return filtered schedule depending on how to filter
     */
    public static ArrayList<Schedulable> filterSchedule(ArrayList<Schedulable> unfilteredSchedule) {
        ArrayList<Schedulable> schedule = unfilteredSchedule;
        // Apply filters
        schedule = removeTimeblocks(schedule);

        return schedule;
    }

    /**
     * This function returns the index of the timeblocks which the given task
     * can use, that is, timeblocks before it's deadline. The task's deadline
     * may be cutting into the last timeblock but the index of the timeblock
     * will still be returned.
     *
     * @param task the task to get available timeblocks for
     * @param timeblocks the array list of timeblocks
     * @return index of timeblocks before the task's deadline
     */
    ArrayList<Integer> getTaskAvailableTimeblocks(Task task,
                                                          ArrayList<Timeblock> timeblocks) {
        // Array list to store results
        ArrayList<Integer> timeblocksAvailable = new ArrayList<>();
        // Task deadline in milliseconds
        long taskDeadlineMillis = task.getDeadline().getMillis();

        for (int i=0; i<timeblocks.size(); i++) {
            Timeblock timeblock = timeblocks.get(i);
            // If the timeblock is before the deadline
            if (timeblock.getScheduledStart().getMillis() < taskDeadlineMillis) {
                timeblocksAvailable.add(i);
            }
        }

        return timeblocksAvailable;
    }

    /**
     * The name of the scheduler, for example, "direct" or "even".
     * @return name of the scheduler method
     */
    String getName() {
        return name;
    }

    /**
     * The tasks from the database when this class is initialized.
     * @return tasks from database when initialized
     */
    ArrayList<Task> getTasks() {
        return this.tasks;
    }

    /**
     * The non-repeated tasks from the database
     * @return non-repeated tasks
     */
    ArrayList<Task> getNonRepeatedTasks() {
        ArrayList<Task> nonRepeatedTasks = new ArrayList<>();
        for (Task task : this.tasks) {
            if (!task.isRepeated()) {
                nonRepeatedTasks.add(task);
            }
        }

        // Sort the expanded tasks, most recent first
        TaskComparator taskComparator = new TaskComparator();
        taskComparator.setSortBy(TaskComparator.Order.DEADLINE, true);
        Collections.sort(nonRepeatedTasks, taskComparator);

        return nonRepeatedTasks;
    }

    /**
     * The repeated tasks from the database
     * @return repeated tasks
     */
    ArrayList<Task> getRepeatedTasks() {
        ArrayList<Task> repeatedTasks = new ArrayList<>();
        for (Task task : this.tasks) {
            if (task.isRepeated()) {
                repeatedTasks.add(task);
            }
        }

        // Sort the repeated tasks
        TaskComparator taskComparator = new TaskComparator();
        taskComparator.setSortBy(TaskComparator.Order.SCHEDULED_START, true);
        Collections.sort(repeatedTasks, taskComparator);

        return repeatedTasks;
    }

    /**
     * The tasks from the database with all of it's repeated days expanded
     * into individual tasks.
     * @return tasks and expanded repeated tasks
     */
    ArrayList<Task> getTasksWithExpandedRepeats() {
        return this.tasksWithExpandedRepeats;
    }

    /**
     * The repeated tasks from the database expanded into individual tasks.
     * @return repeated tasks expanded
     */
    ArrayList<Task> getRepeatedTasksExpanded() {
        return this.repeatedTasksExpanded;
    }

    /**
     * The voidblocks from the database when this class is initialized.
     * @return voidhblocks from database when initialized
     */
    ArrayList<Voidblock> getVoidblocks() {
        return this.voidblocks;
    }

    /**
     * The voidblocks from the database with all of it's repeated days
     * expanded into individual voidblocks.
     * @return voidblocks and expanded repeated voidblocks
     */
    ArrayList<Voidblock> getExpandedVoidblocks() {
        return this.expandedVoidblocks;
    }

    /**
     * The timeblocks calculated from the tasks and voidblocks when this class
     * is initialized.
     * @return calculated timeblocks when initialized
     */
    ArrayList<Timeblock> getTimeblocks() {
        return this.timeblocks;
    }

    /**
     * The voidblocks and timeblocks without any tasks when this class is
     * initialized.
     * @return voidblocks and timeblocks when initialized
     */
    ArrayList<Schedulable> getEmptySchedule() {
        return this.emptySchedule;
    }

    /**
     * Checks if there is sufficient data to start scheduling.
     * @return true if tasks or voidblocks are empty, false otherwise
     */
    boolean isEmpty() {
        return this.getTasks().isEmpty() && this.getVoidblocks().isEmpty();
    }

    public abstract ArrayList<Schedulable> schedule();
}
