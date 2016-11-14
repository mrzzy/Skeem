package sstinc.prevoir;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

/*
Make voidtimeblocks
evenSort(start, sto)
Schedule repeated tasks by even sort
Schedule the rest of the tasks

def evenSort(tasks, timeblocks, scheduled_start):
    for task in tasks:
        available_timeblocks = []
        for timeblock in timeblocks:
            if timeblock is before task.deadline:
                available_timeblocks.append(timeblock)

        period_needed = task.period_needed
        time_per_block = period_needed/len(available_timeblocks)
        while True:
            for timeblock in available_timeblocks:
                if timeblock.duration less than time_per_block:
                    period_needed -= timblock.duration
                    time_per_block = period_needed/len(available_timeblocks)-1
                    available_timeblocks.remove(timeblock)

                    schedule fill timeblock with task
                else:
                    period_needed -= time_per_block
                    schdule timeblock with task

            if period_needed < 1min:
                break

Sorting:
    Get voidtimeblocks
    Get all the tasks that repeat
    for each repeated task:
        for each expanded repeated task:
            scheduled the expanded repeated task into the latest timeblock in the day
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
public class ScheduleFragment extends ListFragment {
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;

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
        DbAdapter dbAdapter = new DbAdapter(getActivity());
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
                        period_needed = period_needed.minus(timeblock.getPeriod());
                        // Remove the Integer object i from the list (not index)
                        available_timeblocks.remove(Integer.valueOf(i));
                        // Calculate new time per block
                        time_per_block = period_needed.toStandardDuration().getMillis()/
                                ((long) available_timeblocks.size());

                        // Create task to schedule
                        Task timeblock_task = new Task(task);
                        // Set scheduled start and stop
                        Datetime task_scheduled_start = new Datetime(
                                timeblock.getScheduledStart().add(timeblock.getPeriodUsed()));
                        Datetime task_scheduled_stop = new Datetime(timeblock.getScheduledStop());
                        // Set period needed
                        timeblock_task.setPeriodNeeded(new Period(timeblock.getPeriodLeft()));
                        // Schedule timeblock with task
                        ((Timeblock) voidtimeblocks.get(i)).addTask(timeblock_task);
                    } else {
                        // timeblock has sufficient space for task
                        period_needed = period_needed.minus(timeblock.getPeriodLeft());

                        // Create task to schedule
                        Task timeblock_task = new Task(task);
                        // Set scheduled start and stop
                        Datetime task_scheduled_start = new Datetime(
                                timeblock.getScheduledStart().add(timeblock.getPeriodUsed()));
                        Datetime task_scheduled_stop = new Datetime(
                                timeblock.getScheduledStart().add(timeblock.getPeriodUsed()).add(
                                        new Period(time_per_block)));
                        // Set period needed
                        timeblock_task.setPeriodNeeded(new Period(timeblock.getPeriodLeft()));
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
    private ArrayList<Schedulable> schedule() {
        // schedule with evenSort
        // Get tasks
        DbAdapter dbAdapter = new DbAdapter(getActivity());
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
                            task_to_schedule.setPeriodNeeded(timeblock.getPeriodLeft());
                            ((Timeblock) voidtimeblocks.get(expanded_task_timeblock_index)).addTask(
                                    task_to_schedule);
                        } else {
                            // Enough time for the task
                            Task task_to_schedule = new Task(expanded_task);
                            task_to_schedule.setPeriodNeeded(expanded_task_period_needed);
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
        for (Task task: tasks) {
            if (task.getPeriodMinimum() != new Period()) {
                // If the task has a minimum time period
                Period excess = new Period();

                ArrayList<Integer> available_timeblocks = new ArrayList<>();
                // Get available timeblocks
                for (int i : timeblock_indices) {
                    Timeblock timeblock = (Timeblock) voidtimeblocks.get(i);
                    if (timeblock.getScheduledStart().getMillis() )

                }
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Reset menu
        //menu_shuffle = true; //TODO: implement this
        getActivity().invalidateOptionsMenu();

        // Set Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SelectCreateActivity.class);
                startActivity(intent);
            }
        });
        fab.setVisibility(View.VISIBLE);

        ArrayList<Schedulable> schedule = evenSort();
        ArrayList<Schedulable> filtered_schedule = new ArrayList<>();

        Calendar cal = Calendar.getInstance();
        int current_year = cal.get(Calendar.YEAR);
        int current_month = cal.get(Calendar.MONTH);
        int current_day = cal.get(Calendar.DAY_OF_MONTH);
        for (Schedulable item : schedule) {
            if (item instanceof Task) {
                Task task = (Task) item;
                if ((task.getScheduledStart().getDay() == current_day &&
                        task.getScheduledStart().getMonth() == current_month &&
                        task.getScheduledStart().getYear() == current_year) ||
                        (task.getScheduledStop().getDay() == current_day &&
                         task.getScheduledStop().getMonth() == current_month &&
                         task.getScheduledStop().getYear() == current_year)) {
                    filtered_schedule.add(task);
                }
            } else {
                Voidblock voidblock = (Voidblock) item;
                if ((voidblock.getScheduledStart().getDay() == current_day &&
                        voidblock.getScheduledStart().getMonth() == current_month &&
                        voidblock.getScheduledStart().getYear() == current_year) ||
                        (voidblock.getScheduledStop().getDay() == current_day &&
                                voidblock.getScheduledStop().getMonth() == current_month &&
                                voidblock.getScheduledStop().getYear() == current_year)) {
                    filtered_schedule.add(voidblock);
                }
            }
        }

        setListAdapter(new ScheduleArrayAdapter(getActivity(), filtered_schedule));
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.nav_shuffle).setVisible(menu_shuffle);
        menu.findItem(R.id.nav_continue).setVisible(menu_continue);
        menu.findItem(R.id.nav_done).setVisible(menu_finish);
        menu.findItem(R.id.nav_copy).setVisible(menu_duplicate);
        menu.findItem(R.id.nav_delete).setVisible(menu_delete);
    }
}
