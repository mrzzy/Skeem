package sstinc.skeem.fragments;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;
import java.util.Calendar;

import sstinc.skeem.schedule.DirectSchedule;
import sstinc.skeem.R;
import sstinc.skeem.schedule.EDFScheduler;
import sstinc.skeem.schedule.Scheduler;
import sstinc.skeem.activities.SelectCreateActivity;
import sstinc.skeem.adapters.ScheduleArrayAdapter;
import sstinc.skeem.models.Schedulable;
import sstinc.skeem.models.Task;
import sstinc.skeem.models.Voidblock;

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

        Scheduler scheduler = new EDFScheduler(getActivity());
        ArrayList<Schedulable> schedule = Scheduler.filterSchedule(scheduler.schedule());

        setListAdapter(new ScheduleArrayAdapter(getActivity(), schedule));
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
