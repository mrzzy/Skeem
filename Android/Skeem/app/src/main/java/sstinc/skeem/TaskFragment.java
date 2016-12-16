package sstinc.skeem;


import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

//TODO: Time picker in landscape

public class TaskFragment extends ListFragment implements AdapterView.OnItemLongClickListener {
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Request codes
    static final int createTaskRequestCode = 110;
    static final int updateTaskRequestCode = 120;
    // Intent extras
    public static final String EXTRA_TASK = "sstinc.skeem.EXTRA_TASK";

    AdapterView.OnItemClickListener editItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            // Add edit view
            Task task = (Task) getListAdapter().getItem(pos);
            Intent intent = new Intent(getActivity(), TaskCreateActivity.class);
            intent.putExtra(EXTRA_TASK, task);
            startActivityForResult(intent, updateTaskRequestCode);
        }
    };

    private View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition ) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);

        // Reset menu
        getActivity().invalidateOptionsMenu();

        //TODO: Add comments for what this does
        getView().setFocusableInTouchMode(true);

        // When back button is pressed, if in multi-selection mode, disable it.
        // Disable multi-selection mode when back button is pressed by
        // unchecking all the checkboxes which will trigger
        // onCheckedChangeListener exit multi-selection.
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    // If back button pressed
                    if (menu_duplicate && menu_delete) {
                        // If in multi-selection mode
                        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
                            // Get the checkbox of each element
                            View view = getViewByPosition(i, getListView());
                            CheckBox checkBox = (CheckBox) view.findViewById(
                                    R.id.list_item_task_checkBox);

                            // Uncheck it
                            checkBox.setChecked(false);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        // Add bottom padding so that floating action button will not
        // interfere with scrolling.
        float fab_margin = getResources().getDimension(R.dimen.fab_margin);
        getListView().setClipToPadding(false);
        getListView().setPadding(0, 0, 0, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 56 + (fab_margin*2/3),
                getActivity().getResources().getDisplayMetrics()));

        // Get the tasks from database
        DbAdapter dbAdapter = new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        ArrayList<Task> tasks = dbAdapter.getTasks();
        dbAdapter.close();

        // Set Long Click Listener
        getListView().setOnItemClickListener(editItemClickListener);
        getListView().setOnItemLongClickListener(this);

        // Display the tasks from the database
        setListAdapter(new TaskArrayAdapter(getActivity(), tasks));

        // Floating Action Button for adding new tasks
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity().getApplicationContext(),
                        TaskCreateActivity.class);
                startActivityForResult(intent, createTaskRequestCode);
            }
        });
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == createTaskRequestCode) {
            // Request code received after task creation
            if (resultCode == RESULT_OK) {
                // Get the new task from the activity
                Task task = data.getParcelableExtra(EXTRA_TASK);

                // Add task to database
                DbAdapter dbAdapter = new DbAdapter(getActivity().getApplicationContext());
                dbAdapter.open();
                dbAdapter.insertTask(task);
                ArrayList<Task> tasks = dbAdapter.getTasks();
                dbAdapter.close();

                // Reset the list adapter to show the new task
                setListAdapter(new TaskArrayAdapter(getActivity(), tasks));
            }
        } else if (requestCode == updateTaskRequestCode) {
            // Request code received after editing an existing task
            if (resultCode == RESULT_OK) {
                // Update to database and view again
                // Get the edited task from the activity
                Task task = data.getParcelableExtra(EXTRA_TASK);

                // Update the task in the database
                DbAdapter dbAdapter = new DbAdapter(getActivity().getApplicationContext());
                dbAdapter.open();
                dbAdapter.updateTask(task);
                ArrayList<Task> tasks = dbAdapter.getTasks();
                dbAdapter.close();

                // Reset the list adapter to show the updated task
                setListAdapter(new TaskArrayAdapter(getActivity(), tasks));
            }
        }
    }

    private int getCheckedCheckBoxes() {
        int count = 0;
        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
            View view = getViewByPosition(i, getListView());
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_task_checkBox);
            if (checkBox.isChecked()) {
                count++;
            }
        }
        return count;
    }

    /**
     * This function is called when a long press on a task occurs. When it
     * occurs, all the task's checkboxes are made visible and the item long
     * clicked will have it's checkbox selected.
     *
     * @param parent parent view
     * @param view view of task
     * @param position position of task in array adapter
     * @param id task's id in array adapter
     * @return true if something happened
     */
    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // Set each item's onClickListener to toggle the checkbox instead of
        // updating the selected task.
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Toggle checkbox on click
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_task_checkBox);
                checkBox.toggle();
            }
        });

        // Iterate through each view
        for (int i = getListAdapter().getCount()-1; i>=0; i--) {
            View v = getViewByPosition(i, getListView());

            // Make checkbox visible
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.list_item_task_checkBox);
            checkBox.setVisibility(View.VISIBLE);

            // Set onCheckedChangeListener for checkBoxes
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (getCheckedCheckBoxes() == 0) {
                        // Hide all the checkboxes
                        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
                            View view = getViewByPosition(i, getListView());
                            // Make checkbox invisible and uncheck
                            CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_task_checkBox);
                            checkBox.setVisibility(View.GONE);
                            checkBox.setChecked(false);
                        }

                        // Reset menu
                        menu_duplicate = false;
                        menu_delete = false;
                        getActivity().invalidateOptionsMenu();

                        // Return to normal OnItemClickListener
                        getListView().setOnItemClickListener(editItemClickListener);
                    }
                }
            });
        }
        // Make long pressed checkbox checked
        ((CheckBox) view.findViewById(R.id.list_item_task_checkBox)).setChecked(true);

        // Reset Menu
        menu_duplicate = true;
        menu_delete = true;
        getActivity().invalidateOptionsMenu();

        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        menu.findItem(R.id.nav_shuffle).setVisible(menu_shuffle);
        menu.findItem(R.id.nav_continue).setVisible(menu_continue);
        menu.findItem(R.id.nav_done).setVisible(menu_finish);
        menu.findItem(R.id.nav_copy).setVisible(menu_duplicate);
        menu.findItem(R.id.nav_delete).setVisible(menu_delete);

        // Set onClickListener for duplicating selected tasks
        menu.findItem(R.id.nav_copy).setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Disable menu
                menu_duplicate = false;
                menu_delete = false;
                getActivity().invalidateOptionsMenu();

                // Duplicate all the selected tasks
                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();
                for (int i=getListAdapter().getCount()-1; i>=0; i--) {
                    // Get CheckBox
                    View view = getViewByPosition(i, getListView());
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_task_checkBox);

                    if (checkBox.isChecked()) {
                        dbAdapter.insertTask((Task) getListAdapter().getItem(i));
                    }
                }

                ArrayList<Task> tasks = dbAdapter.getTasks();
                dbAdapter.close();
                setListAdapter(new TaskArrayAdapter(getActivity(), tasks));

                // Return to normal OnItemClickListener
                getListView().setOnItemClickListener(editItemClickListener);
                return true;
            }
        });

        // Set onClickListener for multi-selection deletion
        menu.findItem(R.id.nav_delete).setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Delete immediately if there is only one task selected
                if (getCheckedCheckBoxes() == 1) {
                    // Disable menu
                    menu_duplicate = false;
                    menu_delete = false;
                    getActivity().invalidateOptionsMenu();

                    // Delete immediately
                    DbAdapter dbAdapter = new DbAdapter(getActivity());
                    for (int i=getListAdapter().getCount()-1; i>=0; i--) {
                        View view = getViewByPosition(i, getListView());
                        CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_task_checkBox);

                        if (checkBox.isChecked()) {
                            dbAdapter.open();
                            dbAdapter.deleteTask(((Task) getListAdapter().getItem(i)).getId());
                            break;
                        }
                    }
                    ArrayList<Task> tasks = dbAdapter.getTasks();
                    dbAdapter.close();
                    setListAdapter(new TaskArrayAdapter(getActivity(), tasks));

                    // Return to normal OnItemClickListener
                    getListView().setOnItemClickListener(editItemClickListener);
                    return false;
                }

                // Ask for conformation to delete if there is more than one
                // task.
                // Create alert dialog
                AlertDialog.Builder confirm_delete_tasks = new AlertDialog.Builder(getActivity());
                confirm_delete_tasks.setTitle(R.string.dialog_confirm_delete_title);
                confirm_delete_tasks.setMessage(R.string.dialog_confirm_delete_message);

                // Set onClickListener for positive button
                confirm_delete_tasks.setPositiveButton(R.string.dialog_confirm_delete_positive,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Disable menu
                                menu_duplicate = false;
                                menu_delete = false;
                                getActivity().invalidateOptionsMenu();

                                // Duplicate all the selected tasks
                                DbAdapter dbAdapter = new DbAdapter(getActivity());
                                dbAdapter.open();
                                for (int index=getListAdapter().getCount()-1; index>=0; index--) {
                                    // Get CheckBox
                                    View view = getViewByPosition(index, getListView());
                                    CheckBox checkBox = (CheckBox) view.findViewById(
                                            R.id.list_item_task_checkBox);

                                    // Delete task if it is checked
                                    if (checkBox.isChecked()) {
                                        dbAdapter.deleteTask(
                                                ((Task) getListAdapter().getItem(index)).getId());
                                    }
                                }

                                ArrayList<Task> tasks = dbAdapter.getTasks();
                                dbAdapter.close();
                                setListAdapter(new TaskArrayAdapter(getActivity(), tasks));

                                // Return to normal OnItemClickListener
                                getListView().setOnItemClickListener(editItemClickListener);
                            }
                        });

                // Set empty onClickListener for negative button
                confirm_delete_tasks.setNegativeButton(R.string.dialog_confirm_delete_negative,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {}});

                // Show dialog
                confirm_delete_tasks.show();

                return true;
            }
        });
    }
}
