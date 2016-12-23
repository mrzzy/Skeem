package sstinc.skeem;

import android.content.Context;

import java.util.ArrayList;
import java.util.Collections;

/**
 * This class superclasses all schedule sorting algorithms and handles their
 * respective function calls. It also provides a layer of abstraction.
 */
abstract class Scheduler {
    private String name;
    private ArrayList<Task> tasks;
    private ArrayList<Voidblock> voidblocks;
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

        this.timeblocks = new ArrayList<>();
        this.emptySchedule = new ArrayList<>();

        // Expand voidblocks to create timeblocks and emptySchedule
        ArrayList<Voidblock> expanded_voidblocks = new ArrayList<>();

        if (this.tasks.size() != 0) {
            Datetime latest_deadline = tasks.get(tasks.size()-1).getDeadline();
            // Expand all repeated voidblocks
            for (Voidblock voidblock : this.voidblocks) {
                if (voidblock.isRepeated()) {
                    // Add it to the expanded voidblock
                    Collections.addAll(expanded_voidblocks,
                            voidblock.getSeparatedRepeatedVoidblocks(latest_deadline));
                } else {
                    expanded_voidblocks.add(voidblock);
                }
            }

            // Sort the voidblocks
            VoidblockComparator voidblockComparator = new VoidblockComparator();
            voidblockComparator.setSortBy(VoidblockComparator.Order.SCHEDULED_START, true);
            Collections.sort(expanded_voidblocks, voidblockComparator);
        }

        // Create emptySchedule
        for (int i=0;i<expanded_voidblocks.size();i++) {
            if (i == 0) {
                this.emptySchedule.add(expanded_voidblocks.get(i));
            } else {
                Voidblock prev_voidblock = expanded_voidblocks.get(i-1);
                Voidblock curr_voidblock = expanded_voidblocks.get(i);

                Timeblock timeblockToAdd = new Timeblock(prev_voidblock.getScheduledStop(),
                        curr_voidblock.getScheduledStart());

                this.emptySchedule.add(timeblockToAdd);
                this.emptySchedule.add(curr_voidblock);
                this.timeblocks.add(timeblockToAdd);
            }
        }
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
     * The voidblocks from the database when this class is initialized.
     * @return voidhblocks from database when initialized
     */
    ArrayList<Voidblock> getVoidblocks() {
        return this.voidblocks;
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

    abstract ArrayList<Schedulable> schedule();
}
