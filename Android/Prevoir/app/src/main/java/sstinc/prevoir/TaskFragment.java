package sstinc.prevoir;


import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
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
    // Extra constants for intents
    public static final String EXTRA_TASK = "sstinc.prevoir.EXTRA_TASK";
    // Request codes for receiving and sending data
    static final int createTaskRequestCode = 110;
    static final int updateTaskRequestCode = 120;
    // Boolean to show if menu shows duplicate and delete buttons
    static boolean menu_multi = false;

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getView().setFocusableInTouchMode(true);

        // When back button is pressed, if in multi-selection mode, disable it.
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (menu_multi) {
                        Log.w(this.getClass().getName(), "Undoing checks...");
                        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
                            Log.w(this.getClass().getName(), "i = " + i);
                            View view = getViewByPosition(i, getListView());
                            CheckBox checkBox = (CheckBox) view.findViewById(
                                    R.id.list_item_task_checkBox);
                            checkBox.setChecked(false);
                        }
                    }
                    return true;
                }
                return false;
            }
        });

        // Set options menu
        setHasOptionsMenu(true);

        // Reset menu
        menu_multi = false;
        getActivity().invalidateOptionsMenu();

        // Hide shuffle button
        MainActivity.menu_shuffle = false;
        getActivity().invalidateOptionsMenu();

        // Set bottom padding
        getListView().setClipToPadding(false);
        float fab_margin = getResources().getDimension(R.dimen.fab_margin);
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
                dbAdapter.updateTask(task.getId(), task);
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

    private void hideCheckBoxes() {
        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
            View view = getViewByPosition(i, getListView());
            // Make it invisible and uncheck
            CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_task_checkBox);
            checkBox.setVisibility(View.GONE);
            checkBox.setChecked(false);
        }
    }

    public View getViewByPosition(int pos, ListView listView) {
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
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // Set new onClickListener
        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_task_checkBox);
                // Change it to check when clicked
                checkBox.toggle();
            }
        });

        // Iterate through each view
        for (int i = getListAdapter().getCount()-1; i>=0; i--) {
            View v = getViewByPosition(i, getListView());
            // Set checkBox to be visible
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.list_item_task_checkBox);
            checkBox.setVisibility(View.VISIBLE);

            // Set onClickListener for checkBoxes
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (getCheckedCheckBoxes() == 0) {
                        hideCheckBoxes();
                        menu_multi = false;
                        getActivity().invalidateOptionsMenu();
                        // Return to normal OnItemClickListener
                        getListView().setOnItemClickListener(editItemClickListener);
                    }
                }
            });
        }
        // Reset Menu
        menu_multi = true;
        getActivity().invalidateOptionsMenu();

        ((CheckBox) view.findViewById(R.id.list_item_task_checkBox)).setChecked(true);
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
                return false;
            }
        });

        menu.findItem(R.id.nav_delete)
                .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (getCheckedCheckBoxes() == 1) {
                    // Disable menu
                    menu_multi = false;
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
                // Confirm delete
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.dialog_confirm_delete_title)
                        .setMessage(R.string.dialog_confirm_delete_message)
                        .setPositiveButton(R.string.dialog_confirm_delete_positive,
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
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
                                            R.id.list_item_task_checkBox);

                                    if (checkBox.isChecked()) {
                                        dbAdapter.deleteTask(
                                                ((Task) getListAdapter().getItem(i)).getId());
                                    }
                                }

                                ArrayList<Task> tasks = dbAdapter.getTasks();
                                dbAdapter.close();
                                setListAdapter(new TaskArrayAdapter(getActivity(), tasks));

                                // Return to normal OnItemClickListener
                                getListView().setOnItemClickListener(editItemClickListener);
                            }
                        })
                        .setNegativeButton(R.string.dialog_confirm_delete_negative,
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

                return false;
            }
        });
    }
}
