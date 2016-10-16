package sstinc.prevoir;


import android.app.ListFragment;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ListView;

import java.util.ArrayList;

public class TaskFragment extends ListFragment {

    ArrayList<Task> tasks;
    TaskArrayAdapter taskArrayAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        tasks = new ArrayList<>();
        // Test tasks
        tasks.add(new Task("Name 1", "subject 1",
                new ArrayList<Task.WeekDay>(), new Deadline(new Datetime()),
                "Description 1", new Duration()));
        tasks.add(new Task("Name 2", "subject 2",
                new ArrayList<Task.WeekDay>(), new Deadline(new Datetime()),
                "Description 2", new Duration()));
        tasks.add(new Task("Name 3", "subject 3",
                new ArrayList<Task.WeekDay>(), new Deadline(new Datetime()),
                "Description 3", new Duration()));
        tasks.add(new Task("Name 4", "subject 4",
                new ArrayList<Task.WeekDay>(), new Deadline(new Datetime()),
                "Description 4", new Duration()));
        tasks.add(new Task("Name 5", "subject 5",
                new ArrayList<Task.WeekDay>(), new Deadline(new Datetime()),
                "Description 5", new Duration()));
        tasks.add(new Task("Name 6", "subject 6",
                new ArrayList<Task.WeekDay>(), new Deadline(new Datetime()),
                "Description 6", new Duration()));
        tasks.add(new Task("Name 7", "subject 7",
                new ArrayList<Task.WeekDay>(), new Deadline(new Datetime()),
                "Description 7", new Duration()));
        tasks.add(new Task("Name 8", "subject 8",
                new ArrayList<Task.WeekDay>(), new Deadline(new Datetime()),
                "Description 8", new Duration()));
        tasks.add(new Task("Name 9", "subject 9",
                new ArrayList<Task.WeekDay>(), new Deadline(new Datetime()),
                "Description 9", new Duration()));

        taskArrayAdapter = new TaskArrayAdapter(getActivity(), tasks);
        setListAdapter(taskArrayAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int pos, long id) {
        super.onListItemClick(l, v, pos, id);
        Task task = (Task) getListAdapter().getItem(pos);
        Snackbar.make(v, "You clicked on " + task.name, Snackbar.LENGTH_LONG).show();
    }

}
