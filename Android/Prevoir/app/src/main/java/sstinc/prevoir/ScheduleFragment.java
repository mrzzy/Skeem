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

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleFragment extends ListFragment {

    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;

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
                voidTimeblocks.add(new Timeblock(prev_voidblock.to, curr_voidblock.from));
                voidTimeblocks.add(curr_voidblock);
            }
        }
        return voidTimeblocks;
    }

    private boolean validateTaskInTimeblocks() {
        // Get voidTimeblocks and tasks
        DbAdapter dbAdapter = new DbAdapter(getActivity());
        dbAdapter.open();
        ArrayList<Task> tasks = dbAdapter.getTasks();
        dbAdapter.close();

        Duration time_needed = new Duration();
        for (Schedulable schedulable : tasks) {
            Task task = (Task) schedulable;
            time_needed.add(task.duration);
        }

        Duration total_time = new Duration();
        for (Schedulable schedulable : getVoidTimeblocks()) {
            if (schedulable instanceof Timeblock) {
                Timeblock timeblock = (Timeblock) schedulable;
                total_time.add(timeblock.duration);
            }
        }

        return total_time.toFullString().compareTo(time_needed.toFullString()) != -1;
    }

    // Get and sort values according to even sort
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
            Duration time_per_block = task.duration.divide(number_of_timeblocks);
            Duration task_duration_left = task.duration;

            while (task_duration_left.toMinutes() != 0) {
                for (Schedulable schedulable : voidTimeblocks) {
                    if (schedulable instanceof Timeblock) {
                        Timeblock timeblock = (Timeblock) schedulable;
                        if (timeblock.duration.toMinutes() == 0) {
                            continue;
                        }

                        if (time_per_block.toFullString().compareTo(
                                timeblock.duration.toFullString()) == -1) {
                            task_duration_left = task_duration_left.subtract(timeblock.duration);

                            Task timeblock_task = new Task(task);
                            timeblock_task.scheduled_start = timeblock.from;
                            timeblock_task.scheduled_end = timeblock.to;
                            timeblock_task.duration = timeblock.duration;
                            timeblock.addTask(timeblock_task);
                        } else {
                            task_duration_left = task_duration_left.subtract(time_per_block);

                            Task timeblock_task = new Task(task);
                            Log.w(this.getClass().getName(), "Time per block: " + time_per_block.toFullString());
                            Log.w(this.getClass().getName(), "Timeblock from: " + timeblock.from.add(timeblock.duration_used).toString());
                            Log.w(this.getClass().getName(), "Task duration: " + task.duration.toFullString());
                            Log.w(this.getClass().getName(), "Duration used: " + timeblock.duration_used.toFullString());
                            timeblock_task.scheduled_start = timeblock.from.add(
                                    timeblock.duration_used);
                            timeblock_task.scheduled_end = timeblock.to.add(
                                    timeblock.duration_used).add(time_per_block);
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
                schedule.addAll(timeblock.tasks_scheduled);
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
                if ((task.scheduled_start.getDay() == current_day &&
                        task.scheduled_start.getMonth() == current_month &&
                        task.scheduled_start.getYear() == current_year) ||
                        (task.scheduled_end.getDay() == current_day &&
                         task.scheduled_end.getMonth() == current_month &&
                         task.scheduled_end.getYear() == current_year)) {
                    filtered_schedule.add(task);
                }
            } else {
                Voidblock voidblock = (Voidblock) item;
                if ((voidblock.from.getDay() == current_day &&
                        voidblock.from.getMonth() == current_month &&
                        voidblock.from.getYear() == current_year) ||
                        (voidblock.to.getDay() == current_day &&
                                voidblock.to.getMonth() == current_month &&
                                voidblock.to.getYear() == current_year)) {
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
