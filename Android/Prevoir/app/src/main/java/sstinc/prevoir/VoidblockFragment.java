package sstinc.prevoir;
//TODO: long press

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import static android.app.Activity.RESULT_OK;

public class VoidblockFragment extends ListFragment {
    // Request codes
    public static final int createVoidblockRequestCode = 210;
    public static final int updateVoidblockRequestCode = 220;
    // Extra strings
    public static final String EXTRA_UPDATE_VOIDBLOCK = "sstinc.prevoir.EXTRA_UPDATE_VOIDBLOCK";
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;

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
        intent.putExtra(EXTRA_UPDATE_VOIDBLOCK, voidblock);
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
        MainActivity.menu_shuffle = false;
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

        // Set the sample data
        setListAdapter(new VoidblockArrayAdapter(getActivity(), voidblocks));

        // Floating Action Button for adding new tasks
        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createVoidblock();
            }
        });
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

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
                dbAdapter.updateVoidblock(voidblock.getId(), voidblock);
                ArrayList<Voidblock> voidblocks = dbAdapter.getVoidblocks();
                dbAdapter.close();

                // Reset the list adapter to show the updated task
                setListAdapter(new VoidblockArrayAdapter(getActivity(), voidblocks));
            }
        }
    }
}