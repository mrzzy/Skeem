package sstinc.prevoir;

import android.content.Context;

import org.joda.time.DateTime;
import org.joda.time.Period;

import java.util.ArrayList;
import java.util.Collections;

/**
 * <p><i>This class implements {@link Scheduler}, see the documentation there
 * for context.</i></p>
 * Even schedule schedules the tasks as evenly as possible, that is, the task
 * will be completed over as many timeblocks as possible, each timeblock would
 * have the time scheduled as equal as possible.
 *
 * @see Scheduler
 * @see Schedulable
 * @see Task
 * @see Timeblock
 */
class EvenSchedule extends Scheduler {
    EvenSchedule(Context ctx) {
        super(ctx, "even");
    }

    /**
     * This function returns a list of voidblocks. Between each voidblock, a
     * timeblock is inserted to fill the gap.
     *
     * @see Voidblock
     * @see Timeblock
     * @return list of void and time blocks
     */
    private ArrayList<Schedulable> getVoidTimeblocks() {
        ArrayList<Voidblock> voidblocks = new ArrayList<>();
        // Get voidblocks and tasks
        DbAdapter dbAdapter = new DbAdapter(this.context);
        dbAdapter.open();
        ArrayList<Voidblock> raw_voidblocks = dbAdapter.getVoidblocks();
        ArrayList<Task> tasks = dbAdapter.getTasks();
        dbAdapter.close();

        Datetime latest_deadline = tasks.get(tasks.size()-1).getDeadline();

        // Expand all repeated voidblocks
        for (Voidblock voidblock : raw_voidblocks) {
            if (!voidblock.getWeekDays().getWeekDays_list().isEmpty()) {
                // There are repeated days; expand and add to voidblocks
                Voidblock[] expanded_voidblocks = voidblock.getSeparatedRepeatedVoidblocks(
                        latest_deadline);
                Collections.addAll(voidblocks, expanded_voidblocks);
            } else {
                voidblocks.add(voidblock);
            }
        }
        // Sort the voidblocks
        VoidblockComparator voidblockComparator = new VoidblockComparator();
        voidblockComparator.setSortBy(VoidblockComparator.Order.SCHEDULED_START, true);
        Collections.sort(voidblocks, voidblockComparator);

        ArrayList<Schedulable> voidTimeblocks = new ArrayList<>();
        for (int i=0; i<voidblocks.size(); i++) {
            if (i == 0) {
                voidTimeblocks.add(voidblocks.get(i));
            } else {
                Voidblock prev_voidblock = voidblocks.get(i-1);
                Voidblock curr_voidblock = voidblocks.get(i);
                voidTimeblocks.add(new Timeblock(prev_voidblock.getScheduledStop(),
                        curr_voidblock.getScheduledStart()));
                voidTimeblocks.add(curr_voidblock);
            }
        }
        return voidTimeblocks;
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
    private ArrayList<Integer> getTaskAvailableTimeblocks(Task task,
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
     * Schedules the tasks as evenly as possible into each timeblock. This
     * function assumes that the tasks can always be scheduled into the
     * timeblocks.
     *
     * @param tasks array list of tasks to schedule
     * @param voidtimeblocks array list of voidtimeblock scope
     * @param timeblock_indices array list of voidtimeblock's indices to each
     *                          timeblock
     * @param scheduled_start datetime to schedule from
     * @return array list of schedulables with timeblocks scheduled with the
     * tasks
     */
    private ArrayList<Schedulable> evenSort(ArrayList<Task> tasks,
                                            ArrayList<Schedulable> voidtimeblocks,
                                            ArrayList<Integer> timeblock_indices,
                                            Datetime scheduled_start) {
        if (scheduled_start == null) {
            scheduled_start = new Datetime(DateTime.now());
        }

        for (Task task : tasks) {
            ArrayList<Integer> available_timeblocks = new ArrayList<>();
            // Get available timeblocks
            for (int i : timeblock_indices) {
                Timeblock timeblock = (Timeblock) voidtimeblocks.get(i);
                if (timeblock.getScheduledStart().getMillis() >= scheduled_start.getMillis() &&
                        timeblock.getScheduledStop().getMillis() > task.getDeadline().getMillis()) {
                    available_timeblocks.add(i);
                }
            }

            Period period_needed = new Period(task.getPeriodNeeded());
            Long time_per_block = period_needed.toStandardDuration().getMillis()/
                    ((long) available_timeblocks.size());
            while (true) {
                for (int i : available_timeblocks) {
                    Timeblock timeblock = (Timeblock) voidtimeblocks.get(i);
                    if (timeblock.getPeriodLeft().toStandardDuration().getMillis() <
                            time_per_block) {
                        // timeblock is too small for task's time_per_block
                        period_needed = period_needed.minus(timeblock.getScheduledPeriod());
                        // Remove the Integer object i from the list (not index)
                        available_timeblocks.remove(Integer.valueOf(i));
                        // Calculate new time per block
                        time_per_block = period_needed.toStandardDuration().getMillis()/
                                ((long) available_timeblocks.size());

                        // Create task to schedule
                        Task timeblock_task = new Task(task);
                        // Schedule period needed
                        timeblock_task.setScheduledPeriod(new Period(timeblock.getPeriodLeft()));
                        // Schedule timeblock with task
                        ((Timeblock) voidtimeblocks.get(i)).addTask(timeblock_task);
                    } else {
                        // timeblock has sufficient space for task
                        period_needed = period_needed.minus(timeblock.getPeriodLeft());

                        // Create task to schedule
                        Task timeblock_task = new Task(task);
                        // Schedule period needed
                        timeblock_task.setScheduledPeriod(new Period(timeblock.getPeriodLeft()));
                        // Schedule timeblock with task
                        ((Timeblock) voidtimeblocks.get(i)).addTask(timeblock_task);
                    }
                }

                if (period_needed.getHours() == 0 && period_needed.getMinutes() == 0) {
                    break;
                }
            }
        }
        return voidtimeblocks;
    }

    /*
    Sorting:
    Get voidtimeblocks
    Get all the tasks that repeat
    for each repeated task:
        for each expanded repeated task:
            scheduled the expanded repeated task into the latest timeblock in the day

     */
    public ArrayList<Schedulable> schedule() {
        // schedule with evenSort
        // Get tasks
        DbAdapter dbAdapter = new DbAdapter(this.context);
        dbAdapter.open();
        ArrayList<Task> tasks = dbAdapter.getTasks();
        dbAdapter.close();
        // Get voidtimeblocks
        ArrayList<Schedulable> voidtimeblocks = getVoidTimeblocks();

        // Get timeblock indices
        ArrayList<Integer> timeblock_indices = new ArrayList<>();
        for (int i=0; i<voidtimeblocks.size(); i++) {
            if (voidtimeblocks.get(i) instanceof Timeblock) {
                timeblock_indices.add(i);
            }
        }

        // Schedule repeated tasks
        for (int i=0; i<tasks.size(); i++) {
            Task task = tasks.get(i);
            if (!task.getWeekDays().getWeekDays_list().isEmpty()) {
                // If it is a repeated task
                for (Task expanded_task : task.getSeparatedRepeatedTasks()) {
                    ArrayList<Integer> expanded_task_timeblocks_indices = new ArrayList<>();

                    // Get timeblocks available to the expanded task
                    for (int timeblock_index : timeblock_indices) {
                        Timeblock timeblock = (Timeblock) voidtimeblocks.get(timeblock_index);
                        if (timeblock.getScheduledStart().getMillis() >=
                                expanded_task.getScheduledStart().getMillis() &&
                                timeblock.getScheduledStop().getMillis() <=
                                        expanded_task.getScheduledStop().getMillis()) {
                            // If the timeblock is within the range of the expanded task
                            expanded_task_timeblocks_indices.add(timeblock_index);
                        }
                    }

                    // Reverse the expanded task's timeblock's indices to iterate backwards
                    Collections.reverse(expanded_task_timeblocks_indices);
                    Period expanded_task_period_needed = new Period(
                            expanded_task.getPeriodNeeded());
                    for (int expanded_task_timeblock_index : expanded_task_timeblocks_indices) {
                        // Try and schedule it into the timeblock
                        Timeblock timeblock = (Timeblock) voidtimeblocks.get(
                                expanded_task_timeblock_index);

                        if (timeblock.getPeriodLeft().toStandardDuration().getMillis() <
                                expanded_task_period_needed.toStandardDuration().getMillis()) {
                            // Not enough time, fill the timeblock
                            expanded_task_period_needed = expanded_task_period_needed.minus(
                                    timeblock.getPeriodLeft());
                            // Set the period needed
                            Task task_to_schedule = new Task(expanded_task);
                            task_to_schedule.setScheduledPeriod(timeblock.getPeriodLeft());
                            ((Timeblock) voidtimeblocks.get(expanded_task_timeblock_index)).addTask(
                                    task_to_schedule);
                        } else {
                            // Enough time for the task
                            Task task_to_schedule = new Task(expanded_task);
                            task_to_schedule.setScheduledPeriod(expanded_task_period_needed);
                            ((Timeblock) voidtimeblocks.get(expanded_task_timeblock_index)).addTask(
                                    task_to_schedule);
                            break;
                        }
                    }
                }
            }
        }

        // Try to schedule min_time_period
        /*
        for each task with min_time_period:
        excess = 0 ms
        for each available timeblock:
            if the timeblock's duration is shorter than min_time_period:
                fill timeblock with task
                add min_time_period - timeblock to excess
            else:
                fill timeblock with task
        if excess != 0:
            for each available timeblock:
                evenSort task into timeblock
         */
        ArrayList<Task> tasks_with_excess = new ArrayList<>();
        for (Task task: tasks) {
            if (task.getPeriodMinimum().toStandardDuration().getMillis() != 0) {
                // If the task has a minimum time period
                Period excess = new Period();

                ArrayList<Integer> available_timeblocks = new ArrayList<>();
                // Get available timeblocks
                for (int i : timeblock_indices) {
                    Timeblock timeblock = (Timeblock) voidtimeblocks.get(i);
                    if (timeblock.getScheduledStart().getMillis() <
                            task.getDeadline().getMillis()) {
                        available_timeblocks.add(i);
                    }

                }

                for (int i : available_timeblocks) {
                    Timeblock timeblock = (Timeblock) voidtimeblocks.get(i);
                    if (timeblock.getPeriodLeft().toStandardDuration().getMillis() <
                            task.getPeriodMinimum().toStandardDuration().getMillis()) {
                        // Calculate excess
                        excess.plus(task.getPeriodMinimum().minus(timeblock.getPeriodLeft()));

                        // Schedule task to timeblock
                        Task timeblock_task = new Task(task);
                        timeblock_task.setScheduledPeriod(timeblock.getPeriodLeft());
                        ((Timeblock) voidtimeblocks.get(i)).addTask(timeblock_task);
                    } else {
                        // Schedule task to timeblock
                        Task timeblock_task = new Task(task);
                        timeblock_task.setScheduledPeriod(task.getPeriodMinimum());
                        ((Timeblock) voidtimeblocks.get(i)).addTask(timeblock_task);
                    }
                }

                if (excess.toStandardDuration().getMillis() != 0) {
                    tasks_with_excess.add(task);
                }
                tasks.remove(task);
            }
        }
        tasks.addAll(tasks_with_excess);
        return evenSort(tasks, voidtimeblocks, timeblock_indices, null);
    }
}
