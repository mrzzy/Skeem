package sstinc.prevoir;


import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class TaskFragment extends ListFragment implements AdapterView.OnItemLongClickListener {
    public static final String EXTRA_TASK = "sstinc.prevoir.EXTRA_TASK";

    static final int createTaskRequestCode = 100;
    static final int updateTaskRequestCode = 200;

    ArrayList<Task> tasks;

    boolean menu_multi = false;

    AdapterView.OnItemClickListener editItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            // Add edit view
            Task task = (Task) getListAdapter().getItem(pos);
            Intent intent = new Intent(getActivity(), TaskCreateActivity.class);
            intent.putExtra(TaskFragment.EXTRA_TASK, task);
            startActivityForResult(intent, updateTaskRequestCode);
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        // Hide shuffle button
        MainActivity.menu_shuffle = false;
        getActivity().invalidateOptionsMenu();
        DbAdapter dbAdapter = new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        tasks = dbAdapter.getTasks();
        dbAdapter.close();

        TaskArrayAdapter taskArrayAdapter = new TaskArrayAdapter(getActivity(), tasks);
        setListAdapter(taskArrayAdapter);

        // Set listener
        getListView().setOnItemLongClickListener(this);
        getListView().setOnItemClickListener(editItemClickListener);

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

    private void refreshListAdapter() {
        // TODO: Reuse code below
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
                tasks = dbAdapter.getTasks();
                dbAdapter.close();
                setListAdapter(new TaskArrayAdapter(getActivity(), tasks));
            }
        } else if (requestCode == updateTaskRequestCode) {
            if (resultCode == RESULT_OK) {
                // TODO: Reuse code here
                // Update to database and view again
                Task task = data.getParcelableExtra(EXTRA_TASK);
                DbAdapter dbAdapter = new DbAdapter(getActivity().getApplicationContext());
                dbAdapter.open();
                dbAdapter.updateTask(task.getId(), task);
                tasks = dbAdapter.getTasks();
                Log.w(this.getClass().getName(), "Id of task: " + task.getId());
                dbAdapter.close();
                setListAdapter(new TaskArrayAdapter(getActivity(), tasks));
            }
        }
    }

    private int getCheckedCheckBoxes() {
        int count = 0;
        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
            View view = getViewByPosition(i, getListView());
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_checkBox);
            if (checkBox.isChecked()) {
                count++;
            }
        }
        return count;
    }

    private void hideCheckBoxes() {
        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
            View view = getViewByPosition(i, getListView());
            // Make it invisible and uncheck
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_checkBox);
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // Set the checkboxes visible, change each of the views to toggle checkbox
        // When all the checkboxes are unchecked, make checkboxes invisible, invalidate menu
        // Set the views back to edit mode.
        for (int i = getListAdapter().getCount()-1; i>=0; i--) {
            View v = getViewByPosition(i, getListView());
            // Set checkBox to be visible
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.list_item_checkBox);
            checkBox.setVisibility(View.VISIBLE);

            // Set onClickListener for checkBoxes
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getCheckedCheckBoxes() == 0) {
                        hideCheckBoxes();
                        menu_multi = false;
                        getActivity().invalidateOptionsMenu();
                    }
                }
            });

            // Set new onClickListener
            getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View inner_view,
                                        int position, long id) {
                    // Change it to check when clicked
                    // Get checkbox
                    CheckBox inner_view_checkBox = (CheckBox) inner_view.findViewById(
                            R.id.list_item_checkBox);
                    if (inner_view_checkBox.isChecked()) {
                        inner_view_checkBox.setChecked(false);
                        if (getCheckedCheckBoxes() == 0) {
                            hideCheckBoxes();
                            menu_multi = false;
                            getActivity().invalidateOptionsMenu();
                            // Return to normal OnItemClickListener
                            getListView().setOnItemClickListener(editItemClickListener);
                        }
                    } else {
                        inner_view_checkBox.setChecked(true);
                    }
                }
            });
        }
        // Reset Menu
        menu_multi = true;
        getActivity().invalidateOptionsMenu();

        // Check the currently selected item
        ((CheckBox) view.findViewById(R.id.list_item_checkBox)).setChecked(true);
        (view.findViewById(R.id.list_item_checkBox)).setVisibility(View.VISIBLE);
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.nav_copy).setVisible(menu_multi);
        menu.findItem(R.id.nav_delete).setVisible(menu_multi);

        // Set onClickListeners
        menu.findItem(R.id.nav_copy)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Disable menu
                menu_multi = false;
                getActivity().invalidateOptionsMenu();

                // Duplicate all the selected tasks
                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();
                for (int i=getListAdapter().getCount()-1; i>=0; i--) {
                    // Get CheckBox
                    View view = getViewByPosition(i, getListView());
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_checkBox);

                    if (checkBox.isChecked()) {
                        dbAdapter.insertTask((Task) getListAdapter().getItem(i));
                    }
                }

                tasks = dbAdapter.getTasks();
                dbAdapter.close();
                setListAdapter(new TaskArrayAdapter(getActivity(), tasks));

                // Return to normal OnItemClickListener
                getListView().setOnItemClickListener(editItemClickListener);
                return false;
            }
        });

        menu.findItem(R.id.nav_delete)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Disable menu
                menu_multi = false;
                getActivity().invalidateOptionsMenu();

                // Duplicate all the selected tasks
                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();
                for (int i=getListAdapter().getCount()-1; i>=0; i--) {
                    // Get CheckBox
                    View view = getViewByPosition(i, getListView());
                    CheckBox checkBox = (CheckBox) view.findViewById(
                            R.id.list_item_checkBox);

                    if (checkBox.isChecked()) {
                        dbAdapter.deleteTask(((Task) getListAdapter().getItem(i)).getId());
                    }
                }

                tasks = dbAdapter.getTasks();
                dbAdapter.close();
                setListAdapter(new TaskArrayAdapter(getActivity(), tasks));

                // Return to normal OnItemClickListener
                getListView().setOnItemClickListener(editItemClickListener);
                return false;
            }
        });
    }
}
