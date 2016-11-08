package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.Calendar;

/**
 * This activity handles the creation of a voidblock. The activity handles
 * the name of the voidblock and calls other activities to get more
 * information. Upon gathering all the information, it will create a
 * voidblock and pass it the VoidblockFragment.
 *
 * @see CreateDatetimeActivity
 * @see CreateRepeatedDaysActivity
 * @see VoidblockFragment
 */
public class VoidblockCreateActivity extends AppCompatActivity {
    /*
    voidblockCreate
        -> FROM: days VALUE: weekDays
        -> FROM: Datetime VALUE: from_datetime
        -> FROM: Datetime VALUE: to_datetime
        -> TO: days VALUE: weekDays
        -> TO: Datetime VALUE: from_datetime
        -> TO: Datetime VALUE: to_datetime
    voidblockCreateDatetime
        -> FROM: Create VALUE: from_datetime
        -> FROM: Create VALUE: to_datetime
        -> TO: Create VALUE: from_datetime
        -> TO: Create VALUE: to_datetime
    days
        -> FROM: Create VALUE: weekDays
        -> TO: Create VALUE: weekDays
     */
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Request codes
    public static final int createVoidblockFromRequestCode = 200;
    public static final int createVoidblockToRequestCode = 201;
    public static final int createDaysRequestCode = 310;
    // Intent extras
    public static final String EXTRA_VOIDBLOCK = "sstinc.prevoir.EXTRA_VOIDBLOCK";
    // Voidblock information
    Voidblock voidblock = null;

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
        Switch switch_repeat = (Switch) findViewById(R.id.switch_voidblock_repeat);
        Button button_repeats = (Button) findViewById(R.id.button_voidblock_repeats);

        // Start CreateDatetimeActivity when setting scheduled start
        layout_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent
                Intent intent = new Intent(getApplicationContext(),
                        CreateDatetimeActivity.class);

                // Get current date in datetime
                Datetime currentDatetime = new Datetime();
                currentDatetime.setMillis(Calendar.getInstance().getTimeInMillis());
                // Set datetime selector to have date and time
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HASDATE, true);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HASTIME,
                        CreateDatetimeActivity.HASTIME_YES);
                // Set maximum to scheduled_stop if it exists
                if (voidblock.getScheduledStop().getHasDate() &&
                        voidblock.getScheduledStop().getHasTime()) {
                    intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_MAX,
                            voidblock.getScheduledStop().toString());
                }
                // Set minimum to current datetime
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_MIN,
                        currentDatetime.toString());
                // Set the current datetime to the scheduled_start
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_DATETIME,
                        voidblock.getScheduledStart().toString());

                // Start activity
                startActivityForResult(intent, createVoidblockFromRequestCode);
            }
        });

        // Start CreateDatetimeActivity when setting scheduled stop
        layout_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create intent
                Intent intent = new Intent(getApplicationContext(),
                        CreateDatetimeActivity.class);

                // Get current date in datetime
                Datetime currentDatetime = new Datetime();
                currentDatetime.setMillis(Calendar.getInstance().getTimeInMillis());
                // Set datetime selector to have date and time
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HASDATE, true);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HASTIME,
                        CreateDatetimeActivity.HASTIME_YES);
                // Set minimum to scheduled_start if it exists. If not, set
                // it to the current datetime
                if (voidblock.getScheduledStart().getHasDate() &&
                        voidblock.getScheduledStart().getHasTime()) {
                    intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_MIN,
                            voidblock.getScheduledStart().toString());
                } else {
                    intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_MIN,
                            currentDatetime.toString());
                }

                // Start activity
                startActivityForResult(intent, createVoidblockToRequestCode);
            }
        });

        // Check if it is update
        this.voidblock = getIntent().getParcelableExtra(
                VoidblockFragment.EXTRA_VOIDBLOCK);

        if (this.voidblock != null) {
            // Get the elements
            TextView textView_from_datetime = (TextView) findViewById(
                    R.id.text_view_voidblock_from);
            TextView textView_to_datetime = (TextView) findViewById(
                    R.id.text_view_voidblock_to);

            // Set the values of the fields
            editText_name.setText(this.voidblock.getName());
            textView_from_datetime.setText(this.voidblock.getScheduledStart().toFormattedString());
            textView_to_datetime.setText(this.voidblock.getScheduledStop().toFormattedString());
            if (this.voidblock.getWeekDays().getWeekDays_list().isEmpty()) {
                button_repeats.setText(R.string.button_repetitions_unset);
            } else {
                button_repeats.setText(this.voidblock.getWeekDays().toString());
            }
        } else {
            this.voidblock = new Voidblock();
        }

        // Show repeats button when repeats switch is checked
        switch_repeat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Set visibility of button
                Button button_repeats = (Button) findViewById(R.id.button_voidblock_repeats);
                button_repeats.setVisibility(isChecked? View.VISIBLE: View.GONE);

                // If it is checked, change the "from" and "to" to only have
                // time.
                if (isChecked) {
                    // Create new datetimes for voidblock
                    Datetime scheduled_start = new Datetime();
                    Datetime scheduled_stop = new Datetime();
                    // Scheduled start
                    scheduled_start.setHour(voidblock.getScheduledStart().getHour());
                    scheduled_start.setMinute(voidblock.getScheduledStart().getMinute());
                    // Scheduled stop
                    scheduled_stop.setHour(voidblock.getScheduledStop().getHour());
                    scheduled_stop.setMinute(voidblock.getScheduledStop().getMinute());
                    // Set new scheduled start and stop
                    voidblock.setScheduledStart(scheduled_start);
                    voidblock.setScheduledStop(scheduled_stop);

                    // Reset the textviews only if they are set
                    TextView textView_from_datetime = (TextView) findViewById(
                            R.id.text_view_voidblock_from);
                    TextView textView_to_datetime = (TextView) findViewById(
                            R.id.text_view_voidblock_to);

                    if (textView_from_datetime.getText().length() != 0) {
                        textView_from_datetime.setText(voidblock.getScheduledStart().toFormattedString());
                    }
                    if (textView_to_datetime.getText().length() != 0) {
                        textView_to_datetime.setText(voidblock.getScheduledStop().toFormattedString());
                    }
                }
            }
        });

        // Set repeats button onClickListener
        button_repeats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start new activity to set days
                Intent intent = new Intent(getApplicationContext(),
                        CreateRepeatedDaysActivity.class);
                intent.putExtra(CreateRepeatedDaysActivity.EXTRA_RECEIVE_DAYS,
                        voidblock.getWeekDays().toStringArray());
                startActivityForResult(intent, createDaysRequestCode);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get the id of the menu item selected
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Set to cancel and finish
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.nav_done) {
            // Check that voidblock all fields have been set then finish.

            // Set new name
            EditText editText_name = (EditText) findViewById(R.id.field_text_voidblock_name);
            String name = editText_name.getText().toString();
            this.voidblock.setName(name);

            // Check that the voidblock has scheduled start and stop
            if (this.voidblock.getScheduledStart().getHasDate() &&
                    this.voidblock.getScheduledStop().getHasDate()) {
                return super.onOptionsItemSelected(item);
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_VOIDBLOCK, this.voidblock);
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

                // Set the new scheduled start
                this.voidblock.setScheduledStart(new Datetime(data.getStringExtra(
                        CreateDatetimeActivity.EXTRA_DATETIME)));
                // Reset from text view
                textView_from_datetime.setText(
                        this.voidblock.getScheduledStart().toFormattedString());
            }
        } else if (requestCode == createVoidblockToRequestCode) {
            if (resultCode == RESULT_OK) {
                TextView textView_to_datetime = (TextView) findViewById(
                        R.id.text_view_voidblock_to);

                // Set the new scheduled stop
                this.voidblock.setScheduledStop(new Datetime(data.getStringExtra(
                        CreateDatetimeActivity.EXTRA_DATETIME)));
                // Reset to text view
                textView_to_datetime.setText(this.voidblock.getScheduledStop().toFormattedString());
            }
        } else if (requestCode == createDaysRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get weekdays
                WeekDays weekdays = new WeekDays(data.getStringArrayExtra(
                        CreateRepeatedDaysActivity.EXTRA_DAYS));
                // Set button text
                Button button_repeat = (Button) findViewById(R.id.button_voidblock_repeats);
                if (weekdays.toString().isEmpty()) {
                    button_repeat.setText(R.string.button_repetitions_unset);
                } else {
                    button_repeat.setText(weekdays.toString());
                }

                // Set voidblock
                this.voidblock.setWeekDays(weekdays);
            }
        }
    }


}
