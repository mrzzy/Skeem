package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VoidblockCreateActivity extends AppCompatActivity {
    // Request Codes
    public static final int createVoidblockFromRequestCode = 200;
    public static final int createVoidblockToRequestCode = 201;
    // Extra
    public static final String EXTRA_VOIDBLOCK = "sstinc.prevoir.EXTRA_VOIDBLOCK";
    public static final String EXTRA_VOIDBLOCK_UPDATE_DATETIME =
            "sstinc.prevoir.EXTRA_VOIDBLOCK_UPDATE_DATETIME";
    public static final String EXTRA_VOIDBLOCK_DATETIME_MIN_MAX =
            "sstinc.prevoir.EXTRA_VOIDBLOCK_DATETIME_MIN_MAX";
    public static final String EXTRA_VOIDBLOCK_DATETIME =
            "sstinc.prevoir.EXTRA_VOIDBLOCK_DATETIME";
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Voidblock information
    long voidblock_id = -1;
    Datetime voidblock_from_datetime = new Datetime();
    Datetime voidblock_to_datetime = new Datetime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voidblock_create);

        // Set back button and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create New Voidblock");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Reset menu
        menu_finish = true;
        invalidateOptionsMenu();

        // Get fields
        EditText editText_name = (EditText) findViewById(R.id.field_text_voidblock_name);
        LinearLayout layout_from = (LinearLayout) findViewById(R.id.layout_from);
        LinearLayout layout_to = (LinearLayout) findViewById(R.id.layout_to);

        layout_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        VoidblockCreateDatetimeActivity.class);
                if (voidblock_from_datetime.getYear() != -1) {
                    intent.putExtra(EXTRA_VOIDBLOCK_UPDATE_DATETIME,
                            voidblock_from_datetime.toString());
                }
                intent.putExtra(EXTRA_VOIDBLOCK_DATETIME, "");
                intent.putExtra(EXTRA_VOIDBLOCK_DATETIME_MIN_MAX, "");

                if (voidblock_to_datetime.getYear() != -1) {
                    intent.putExtra(EXTRA_VOIDBLOCK_DATETIME_MIN_MAX, "MAX");
                    intent.putExtra(EXTRA_VOIDBLOCK_DATETIME, voidblock_to_datetime.toString());
                }
                startActivityForResult(intent, createVoidblockFromRequestCode);
            }
        });
        layout_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),
                        VoidblockCreateDatetimeActivity.class);
                if (voidblock_to_datetime.getYear() != -1) {
                    intent.putExtra(EXTRA_VOIDBLOCK_UPDATE_DATETIME,
                            voidblock_to_datetime.toString());
                }
                intent.putExtra(EXTRA_VOIDBLOCK_DATETIME, "");
                intent.putExtra(EXTRA_VOIDBLOCK_DATETIME_MIN_MAX, "");
                if (voidblock_from_datetime.getYear() != -1) {
                    intent.putExtra(EXTRA_VOIDBLOCK_DATETIME_MIN_MAX, "MIN");
                    intent.putExtra(EXTRA_VOIDBLOCK_DATETIME, voidblock_from_datetime.toString());
                }
                startActivityForResult(intent, createVoidblockToRequestCode);
            }
        });

        // Check if it is update
        Voidblock voidblock = getIntent().getParcelableExtra(
                VoidblockFragment.EXTRA_UPDATE_VOIDBLOCK);
        if (voidblock != null) {
            // Get the elements
            TextView textView_from_datetime = (TextView) findViewById(
                    R.id.text_view_voidblock_from);
            TextView textView_to_datetime = (TextView) findViewById(
                    R.id.text_view_voidblock_to);

            // Set the values of the fields
            editText_name.setText(voidblock.getName());
            textView_from_datetime.setText(voidblock.getScheduledStart().toFormattedString());
            textView_to_datetime.setText(voidblock.getScheduledStop().toFormattedString());
            // Set local voidblock times
            voidblock_from_datetime = voidblock.getScheduledStart();
            voidblock_to_datetime = voidblock.getScheduledStop();
            voidblock_id = voidblock.getId();
        }
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Back button
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.nav_done) {
            // Get information
            EditText editText_name = (EditText) findViewById(R.id.field_text_voidblock_name);
            String name = editText_name.getText().toString();

            if (voidblock_to_datetime.getYear() == -1 || voidblock_from_datetime.getYear() == -1) {
                return super.onOptionsItemSelected(item);
            }

            Voidblock voidblock = new Voidblock();
            voidblock.setName(name);
            voidblock.setScheduledStart(voidblock_from_datetime);
            voidblock.setScheduledStop(voidblock_to_datetime);
            if (voidblock_id != -1) {
                voidblock.setId(voidblock_id);
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_VOIDBLOCK, voidblock);
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == createVoidblockFromRequestCode) {
            if (resultCode == RESULT_OK) {
                TextView textView_from_datetime = (TextView) findViewById(
                        R.id.text_view_voidblock_from);

                voidblock_from_datetime = new Datetime(data.getStringExtra(
                        VoidblockCreateDatetimeActivity.EXTRA_VOIDBLOCK_TIME));

                textView_from_datetime.setText(voidblock_from_datetime.toFormattedString());
            }
        } else if (requestCode == createVoidblockToRequestCode) {
            if (resultCode == RESULT_OK) {
                TextView textView_to_datetime = (TextView) findViewById(
                        R.id.text_view_voidblock_to);

                voidblock_to_datetime = new Datetime(data.getStringExtra(
                        VoidblockCreateDatetimeActivity.EXTRA_VOIDBLOCK_TIME));

                textView_to_datetime.setText(voidblock_to_datetime.toFormattedString());
            }
        }
    }


}
