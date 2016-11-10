package sstinc.prevoir;
//TODO: Check for overlaps
//TODO: long press

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
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

public class VoidblockFragment extends ListFragment implements AdapterView.OnItemClickListener,
        AdapterView.OnItemLongClickListener {
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Request codes
    public static final int createVoidblockRequestCode = 210;
    public static final int updateVoidblockRequestCode = 220;
    // Extra strings
    public static final String EXTRA_VOIDBLOCK = "sstinc.prevoir.EXTRA_VOIDBLOCK";

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

    private void editVoidblock(int position) {
        Voidblock voidblock = (Voidblock) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), VoidblockCreateActivity.class);
        intent.putExtra(EXTRA_VOIDBLOCK, voidblock);
        startActivityForResult(intent, updateVoidblockRequestCode);
    }

    private void createVoidblock() {
        Intent intent = new Intent(getActivity(), VoidblockCreateActivity.class);
        startActivityForResult(intent, createVoidblockRequestCode);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Set options menu
        setHasOptionsMenu(true);

        // Reset menu
        getActivity().invalidateOptionsMenu();

        // Hide shuffle button
        menu_shuffle = false;
        getActivity().invalidateOptionsMenu();

        // Set bottom padding
        getListView().setClipToPadding(false);
        float fab_margin = getResources().getDimension(R.dimen.fab_margin);
        getListView().setPadding(0, 0, 0, (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 56 + (fab_margin*2/3),
                getActivity().getResources().getDisplayMetrics()));


        // Get voidblocks from database
        DbAdapter dbAdapter = new DbAdapter(getActivity());
        dbAdapter.open();
        ArrayList<Voidblock> voidblocks = dbAdapter.getVoidblocks();
        dbAdapter.close();

        // Set onClickListeners
        getListView().setOnItemClickListener(this);
        getListView().setOnItemLongClickListener(this);

        // Set the data
        setListAdapter(new VoidblockArrayAdapter(getActivity(), voidblocks));

        // Floating Action Button for adding new tasks
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createVoidblock();
            }
        });
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.findItem(R.id.nav_shuffle).setVisible(menu_shuffle);
        menu.findItem(R.id.nav_continue).setVisible(menu_continue);
        menu.findItem(R.id.nav_done).setVisible(menu_finish);
        menu.findItem(R.id.nav_copy).setVisible(menu_duplicate);
        menu.findItem(R.id.nav_delete).setVisible(menu_delete);

        menu.findItem(R.id.nav_copy).setOnMenuItemClickListener(
                new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Reset menu
                menu_duplicate = false;
                menu_delete = false;
                getActivity().invalidateOptionsMenu();

                ArrayList<Voidblock> voidblocks_to_copy = new ArrayList<>();
                // Get voidblocks selected
                for (int i=getListAdapter().getCount()-1; i>=0; i--) {
                    View v = getViewByPosition(i, getListView());
                    CheckBox checkBox = (CheckBox) v.findViewById(
                            R.id.list_item_voidblock_checkbox);
                    if (checkBox.isChecked()) {
                        voidblocks_to_copy.add((Voidblock) getListAdapter().getItem(i));
                    }
                }
                // Copy
                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();
                for (Voidblock voidblock : voidblocks_to_copy) {
                    dbAdapter.insertVoidblock(voidblock);
                }
                ArrayList<Voidblock> voidblocks = dbAdapter.getVoidblocks();
                dbAdapter.close();

                setListAdapter(new VoidblockArrayAdapter(getActivity(), voidblocks));
                getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        editVoidblock(position);
                    }
                });
                return true;
            }
        });

        menu.findItem(R.id.nav_delete).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                // Reset menu
                menu_duplicate = false;
                menu_delete = false;
                getActivity().invalidateOptionsMenu();

                ArrayList<Voidblock> voidblocks_to_delete = new ArrayList<>();
                // Get voidblocks selected
                for (int i=getListAdapter().getCount()-1; i>=0; i--) {
                    View v = getViewByPosition(i, getListView());
                    CheckBox checkBox = (CheckBox) v.findViewById(
                            R.id.list_item_voidblock_checkbox);
                    if (checkBox.isChecked()) {
                        voidblocks_to_delete.add((Voidblock) getListAdapter().getItem(i));
                    }
                }
                // Delete
                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();
                for (Voidblock voidblock : voidblocks_to_delete) {
                    dbAdapter.deleteVoidblock(voidblock.getId());
                }
                ArrayList<Voidblock> voidblocks = dbAdapter.getVoidblocks();
                dbAdapter.close();

                setListAdapter(new VoidblockArrayAdapter(getActivity(), voidblocks));
                getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        editVoidblock(position);
                    }
                });
                return true;
            }
        });
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        editVoidblock(position);
    }

    private int getCheckedCheckBoxes() {
        int count = 0;
        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
            View v = getViewByPosition(i, getListView());
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.list_item_voidblock_checkbox);
            if (checkBox.isChecked()) {
                count += 1;
            }
        }
        return count;
    }

    private void setCheckBoxes(boolean show) {
        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
            View v = getViewByPosition(i, getListView());
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.list_item_voidblock_checkbox);
            if (show) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        // Reset menu
        menu_duplicate = true;
        menu_delete = true;
        getActivity().invalidateOptionsMenu();

        getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_voidblock_checkbox);
                checkBox.toggle();
            }
        });
        for (int i=getListAdapter().getCount()-1; i>=0; i--) {
            View v = getViewByPosition(i, getListView());
            CheckBox checkBox = (CheckBox) v.findViewById(R.id.list_item_voidblock_checkbox);

                    checkBox.setChecked(false);
            checkBox.setVisibility(View.VISIBLE);
            checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (!isChecked) {
                        if (getCheckedCheckBoxes() == 0) {
                            setCheckBoxes(false);
                            menu_duplicate = false;
                            menu_delete = false;
                            getActivity().invalidateOptionsMenu();
                            getListView().setOnItemClickListener(
                                    new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view,
                                                        int position, long id) {
                                    editVoidblock(position);
                                }
                            });
                        }
                    }
                }
            });
        }

        CheckBox checkBox = (CheckBox) view.findViewById(R.id.list_item_voidblock_checkbox);
        checkBox.setChecked(true);

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == createVoidblockRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get created voidblock
                Voidblock voidblock = data.getParcelableExtra(
                        VoidblockCreateActivity.EXTRA_VOIDBLOCK);

                // Add to database
                DbAdapter dbAdapter = new DbAdapter(getActivity());
                dbAdapter.open();
                dbAdapter.insertVoidblock(voidblock);
                ArrayList<Voidblock> voidblocks = dbAdapter.getVoidblocks();
                dbAdapter.close();

                // Reset the list adapter
                setListAdapter(new VoidblockArrayAdapter(getActivity(), voidblocks));
            }
        } else if (requestCode == updateVoidblockRequestCode) {
            if (resultCode == RESULT_OK) {
                // Update to database and view again
                // Get the edited task from the activity
                Voidblock voidblock = data.getParcelableExtra(
                        VoidblockCreateActivity.EXTRA_VOIDBLOCK);

                // Update the task in the database
                DbAdapter dbAdapter = new DbAdapter(getActivity().getApplicationContext());
                dbAdapter.open();
                dbAdapter.updateVoidblock(voidblock);
                ArrayList<Voidblock> voidblocks = dbAdapter.getVoidblocks();
                dbAdapter.close();

                // Reset the list adapter to show the updated task
                setListAdapter(new VoidblockArrayAdapter(getActivity(), voidblocks));
            }
        }
    }
}