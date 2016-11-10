package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;

public class SelectCreateActivity extends AppCompatActivity {
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Request codes
    private static final int createTaskRequestCode = TaskFragment.createTaskRequestCode;
    private static final int createVoidblockRequestCode =
            VoidblockFragment.createVoidblockRequestCode;
    // Intent extras
    private static final String EXTRA_TASK = TaskFragment.EXTRA_TASK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_create);

        // Set back button and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Reset menu
        invalidateOptionsMenu();

        // Get layout references
        RelativeLayout layout_task = (RelativeLayout) findViewById(R.id.layout_create_task);
        RelativeLayout layout_voidblock = (RelativeLayout) findViewById(
                R.id.layout_create_voidblock);

        // Set the onClickListeners for the layouts
        layout_task.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), TaskCreateActivity.class);
                startActivityForResult(intent, createTaskRequestCode);
            }
        });

        layout_voidblock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), VoidblockCreateActivity.class);
                startActivityForResult(intent, createVoidblockRequestCode);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.nav_shuffle).setVisible(menu_shuffle);
        menu.findItem(R.id.nav_continue).setVisible(menu_continue);
        menu.findItem(R.id.nav_done).setVisible(menu_finish);
        menu.findItem(R.id.nav_copy).setVisible(menu_duplicate);
        menu.findItem(R.id.nav_delete).setVisible(menu_delete);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        long id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == createTaskRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get the new task from the activity
                Task task = data.getParcelableExtra(EXTRA_TASK);

                // Add task to database
                DbAdapter dbAdapter = new DbAdapter(this);
                dbAdapter.open();
                dbAdapter.insertTask(task);
                dbAdapter.close();

                // Finish
                finish();
            } else {
                finish();
            }
        } else if (requestCode == createVoidblockRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get created voidblock
                Voidblock voidblock = data.getParcelableExtra(
                        VoidblockCreateActivity.EXTRA_VOIDBLOCK);

                // Add to database
                DbAdapter dbAdapter = new DbAdapter(this);
                dbAdapter.open();
                dbAdapter.insertVoidblock(voidblock);
                dbAdapter.close();

                // Finish
                finish();
            } else {
                finish();
            }
        }
    }
}
