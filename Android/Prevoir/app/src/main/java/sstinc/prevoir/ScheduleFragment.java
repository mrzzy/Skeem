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
        // Get voidblocks
        DbAdapter dbAdapter = new DbAdapter(getActivity());
        dbAdapter.open();
        ArrayList<Voidblock> voidblocks = dbAdapter.getVoidblocks();
        dbAdapter.close();

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
     * This function returns the timeblocks which the given task can use,
     * that is, timeblocks before it's deadline. The task's deadline may
     * be cutting into the last timeblock but the whole timeblock will be
     * returned.
     *
     * @param task the task to get available timeblocks for
     * @return timeblocks before the task's deadline
     */
    private ArrayList<Timeblock> getTaskAvailableTimeblocks(Task task) {
        // Array list to store results
        ArrayList<Timeblock> timeblocksAvailable = new ArrayList<>();
        // Task deadline in milliseconds
        long taskDeadlineMillis = task.getDeadline().getMillis();

        // Get the voidtimeblocks
        ArrayList<Schedulable> voidTimeblocks = getVoidTimeblocks();
        for (Schedulable schedulable : voidTimeblocks) {
            if (schedulable instanceof Timeblock) {
                Timeblock timeblock = (Timeblock) schedulable;
                // If the timeblock is before the deadline
                if (timeblock.getScheduledStart().getMillis() < taskDeadlineMillis) {
                    timeblocksAvailable.add(timeblock);
                }
            }
        }
        return timeblocksAvailable;
    }

    /**
     * Checks if there is enough time in timeblocks for all the tasks.
     * @return true if there is enough time. false if there is not enough
     * time
     */
    private boolean validateTaskInTimeblocks() {
        // Get voidTimeblocks and tasks
        DbAdapter dbAdapter = new DbAdapter(getActivity());
        dbAdapter.open();
        ArrayList<Task> tasks = dbAdapter.getTasks();
        dbAdapter.close();

        ArrayList<Schedulable> voidTimeblocks = getVoidTimeblocks();

        Period time_needed = new Period();
        for (Schedulable schedulable : tasks) {
            Task task = (Task) schedulable;
            time_needed.plus(task.getPeriodNeeded());
        }

        Period total_time = new Period();
        for (Schedulable schedulable : getVoidTimeblocks()) {
            if (schedulable instanceof Timeblock) {
                Timeblock timeblock = (Timeblock) schedulable;
                total_time.plus(timeblock.getPeriodLeft());
            }
        }

        return PeriodFormat.getDefault().print(total_time).compareTo(
                PeriodFormat.getDefault().print(time_needed)) != -1;
    }


    /**
     * Sorts and scehdules the tasks evenely into each timeblock. Returns an
     * empty list if there is insufficient time.
     * @return array list of voidblocks and timeblocks.
     */
    private ArrayList<Schedulable> evenSort() {
        if (!validateTaskInTimeblocks()) {
            return new ArrayList<>();
        }

        DbAdapter dbAdapter = new DbAdapter(getActivity());
        dbAdapter.open();
        ArrayList<Task> tasks = dbAdapter.getTasks();
        int number_of_timeblocks = dbAdapter.getVoidblocks().size()-1;
        dbAdapter.close();

        ArrayList<Schedulable> voidTimeblocks = getVoidTimeblocks();

        for (Task task : tasks) {
            Period time_per_block = new Period(
                    task.getPeriodNeeded().getMillis()/number_of_timeblocks);
            Period task_duration_left = task.getPeriodNeeded();

            while (task_duration_left.getMinutes() != 0) {
                for (Schedulable schedulable : voidTimeblocks) {
                    if (schedulable instanceof Timeblock) {
                        Timeblock timeblock = (Timeblock) schedulable;
                        if (timeblock.getPeriod().getMinutes() == 0) {
                            continue;
                        }

                        if (PeriodFormat.getDefault().print(time_per_block).compareTo(
                                PeriodFormat.getDefault().print(timeblock.getPeriod())) == -1) {
                            task_duration_left = task_duration_left.minus(timeblock.getPeriod());

                            Task timeblock_task = new Task(task);
                            timeblock_task.setScheduledStart(
                                    new Datetime(timeblock.getScheduledStart()));
                            timeblock_task.setScheduledStop(
                                    new Datetime(timeblock.getScheduledStop()));
                            timeblock_task.setPeriodNeeded(new Period(timeblock.getPeriod()));
                            timeblock.addTask(timeblock_task);
                        } else {
                            task_duration_left = task_duration_left.minus(time_per_block);

                            Task timeblock_task = new Task(task);
                            timeblock_task.setScheduledStart(new Datetime(
                                    timeblock.getScheduledStart().add(timeblock.getPeriodUsed())));
                            timeblock_task.setScheduledStop(new Datetime(
                                    timeblock.getScheduledStart()
                                            .add(timeblock.getPeriodUsed())
                                            .add(time_per_block)));
                            timeblock.addTask(timeblock_task);
                        }
                    }
                }
            }
        }

        // Change all timeblocks to tasks
        ArrayList<Schedulable> schedule = new ArrayList<>();
        for (Schedulable schedulable : voidTimeblocks) {
            if (schedulable instanceof Timeblock) {
                Timeblock timeblock = (Timeblock) schedulable;
                schedule.addAll(timeblock.getTasksScheduled());
            } else {
                schedule.add(schedulable);
            }
        }

        return schedule;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Reset menu
        menu_shuffle = true;
        getActivity().invalidateOptionsMenu();

        // Set Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to SelectCreateActivity
//                Intent intent = new Intent(this, SelectCreateActivity.class);

            }
        });

        // Show shuffle button
        MainActivity.menu_shuffle = true;
        getActivity().invalidateOptionsMenu();

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
