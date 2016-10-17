package sstinc.prevoir;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class TaskFragment extends ListFragment {

    public final static String EXTRA_TASK = "sstinc.prevoir.EXTRA_TASK";

    static final int createTaskRequestCode = 100;

    ArrayList<Task> tasks;
    TaskArrayAdapter taskArrayAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Hide shuffle button
        MainActivity.menu_show = false;
        getActivity().invalidateOptionsMenu();
        DbAdapter dbAdapter = new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        tasks = dbAdapter.getTasks();
        dbAdapter.close();

        taskArrayAdapter = new TaskArrayAdapter(getActivity(), tasks);
        setListAdapter(taskArrayAdapter);

        // Set Floating Action Button
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        TaskCreateActivity.class);
                startActivityForResult(intent, createTaskRequestCode);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == createTaskRequestCode) {
            if (resultCode == RESULT_OK) {
                // Add to database and view again
                Task task = data.getParcelableExtra(EXTRA_TASK);
                DbAdapter dbAdapter = new DbAdapter(getActivity().getApplicationContext());
                dbAdapter.open();
                dbAdapter.insertTask(task);
                dbAdapter.close();
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        // TODO: Add edit view
        Task task = (Task) getListAdapter().getItem(pos);
        Snackbar.make(v, "You clicked on " + task.name, Snackbar.LENGTH_LONG).show();
    }

}
