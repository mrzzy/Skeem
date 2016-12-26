package sstinc.skeem.fragments;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import java.util.ArrayList;

import sstinc.skeem.schedule.DirectSchedule;
import sstinc.skeem.R;
import sstinc.skeem.schedule.EDFScheduler;
import sstinc.skeem.schedule.Scheduler;
import sstinc.skeem.activities.SelectCreateActivity;
import sstinc.skeem.adapters.ScheduleArrayAdapter;
import sstinc.skeem.models.Schedulable;

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
