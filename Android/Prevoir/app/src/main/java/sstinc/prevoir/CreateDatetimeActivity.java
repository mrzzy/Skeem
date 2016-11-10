package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;

public class CreateDatetimeActivity extends AppCompatActivity {
    // Extra strings
    public static final String EXTRA_RECEIVE_TITLE = "sstinc.prevoir.EXTRA_TITLE";
    public static final String EXTRA_RECEIVE_HASDATE = "sstinc.prevoir.EXTRA_RECEIVE_HASDATE";
    public static final String EXTRA_RECEIVE_HASTIME = "sstinc.prevoir.EXTRA_RECEIVE_HASTIME";
    public static final String EXTRA_RECEIVE_DATETIME = "sstinc.prevoir.EXTRA_RECEIVE_DATETIME";
    public static final String EXTRA_RECEIVE_MAX = "sstinc.prevoir.EXTRA_RECEIVE_MAX";
    public static final String EXTRA_RECEIVE_MIN = "sstinc.prevoir.EXTRA_RECEIVE_MIN";
    public static final String EXTRA_DATETIME = "sstinc.prevoir.EXTRA_DATETIME";
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Misc
    public static final String HASTIME_YES = "YES";
    public static final String HASTIME_OPTIONAL = "OPTIONAL";
    public static final String HASTIME_NO = "NO";

    boolean hasDate;
    String hasTime;
    Datetime datetime;
    Datetime max_datetime;
    Datetime min_datetime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_datetime);
        // Animation
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Set back button and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(EXTRA_RECEIVE_TITLE));
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Reset menu
        invalidateOptionsMenu();

        // Get information from intent
        Intent intent = getIntent();
        this.hasDate = intent.getBooleanExtra(EXTRA_RECEIVE_HASDATE, true);
        this.hasTime = intent.getStringExtra(EXTRA_RECEIVE_HASTIME);
        this.datetime = new Datetime((Datetime) intent.getParcelableExtra(EXTRA_RECEIVE_DATETIME));
        this.max_datetime = new Datetime((Datetime) intent.getParcelableExtra(EXTRA_RECEIVE_MAX));
        this.min_datetime = new Datetime((Datetime) intent.getParcelableExtra(EXTRA_RECEIVE_MIN));

        // Get date and time picker
        ScrollableDatePicker datePicker = (ScrollableDatePicker) findViewById(
                R.id.datetime_date_picker);
        ScrollableTimePicker timePicker = (ScrollableTimePicker) findViewById(
                R.id.datetime_time_picker);

        // Set visibility of date and time picker
        datePicker.setVisibility(this.hasDate? View.VISIBLE : View.GONE);
        if (this.hasTime.equals(HASTIME_YES) || this.hasTime.equals(HASTIME_OPTIONAL)) {
            timePicker.setVisibility(View.VISIBLE);
        } else {
            timePicker.setVisibility(View.GONE);
        }

        // Set visibility of optional time switch
        if (this.hasTime.equals(HASTIME_OPTIONAL)) {
            // Default hasTime to HASTIME_NO
            this.hasTime = HASTIME_NO;

            // Get switch
            Switch switch_addTime = (Switch) findViewById(R.id.datetime_switch_add_time);
            // Change the value of hasTime and the visibility of the time
            // picker everytime it changes.
            switch_addTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // Set value of hasTime
                    hasTime = b? HASTIME_YES : HASTIME_NO;

                    // Set visibility of time picker
                    ScrollableTimePicker timePicker = (ScrollableTimePicker) findViewById(
                            R.id.datetime_time_picker);
                    timePicker.setVisibility(b? View.VISIBLE : View.GONE);
                }
            });

            // Make time picker gone first
            timePicker.setVisibility(View.GONE);

            // Set checked if datetime has time
            if (this.datetime.getHasTime()) {
                switch_addTime.setChecked(true);
            }

            LinearLayout add_time_layout = (LinearLayout) findViewById(
                    R.id.datetime_add_time_layout);
            add_time_layout.setVisibility(View.VISIBLE);
        }

        // Fill in the values of datetime if there were any
        if (this.datetime.getHasDate()) {
            Log.w(this.getClass().getName(), this.datetime.toFormattedString());
            datePicker.updateDate(this.datetime.getYear(), this.datetime.getMonth()-1,
                    this.datetime.getDay());
        }
        if (this.datetime.getHasTime()) {
            timePicker.setCurrentHour(this.datetime.getHour());
            timePicker.setCurrentMinute(this.datetime.getMinute());
        }

        // Set the maximum and minimum if any (time is verified later
        if (this.max_datetime.getHasDate()) {
            datePicker.setMaxDate(this.max_datetime.getMillis());
        }
        if (this.min_datetime.getHasDate()) {
            datePicker.setMinDate(this.min_datetime.getMillis());
        }
    }

    /**
     * This function validates that the time given (on submission) is valid
     * in accordance with the given minimum datetime and maximum datetime.
     *
     * @return true if it is within bounds of maximum and minimum datetime
     * else false.
     */
    private boolean validate_timepicker() {
        // Get date and time picker
        ScrollableTimePicker timePicker = (ScrollableTimePicker) findViewById(
                R.id.datetime_time_picker);
        ScrollableDatePicker datePicker = (ScrollableDatePicker) findViewById(
                R.id.datetime_date_picker);

        // Create datetime object from date and time picker
        Datetime submitted_datetime = new Datetime();
        if (this.hasDate) {
            submitted_datetime.setYear(datePicker.getYear());
            submitted_datetime.setMonth(datePicker.getMonth()+1);
            submitted_datetime.setDay(datePicker.getDayOfMonth());
        }
        if (this.hasTime.equals(HASTIME_YES)) {
            submitted_datetime.setHour(timePicker.getCurrentHour());
            submitted_datetime.setMinute(timePicker.getCurrentMinute());
        }

        // Flag to indicate if valid
        boolean flag = true;
        // If the minimum datetime is set
        if (this.min_datetime.getHasDate()) {
            flag = this.min_datetime.getMillis() <= submitted_datetime.getMillis();
        }
        // If the maximum datetime is set
        if (this.max_datetime.getHasDate()) {
            flag = flag && submitted_datetime.getMillis() <= this.max_datetime.getMillis();
        }

        return flag;
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
        // Get the id of the menu item selected
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // Validate and return result when back button is pressed

            if (validate_timepicker()) {
                // If the selected datetime is valid, return the result

                // Get date and time picker
                ScrollableTimePicker timePicker = (ScrollableTimePicker) findViewById(
                        R.id.datetime_time_picker);
                ScrollableDatePicker datePicker = (ScrollableDatePicker) findViewById(
                        R.id.datetime_date_picker);

                // Create datetime object
                Datetime datetime = new Datetime();
                if (this.hasDate) {
                    datetime.setYear(datePicker.getYear());
                    datetime.setMonth(datePicker.getMonth()+1);
                    datetime.setDay(datePicker.getDayOfMonth());
                }
                if (this.hasTime.equals(HASTIME_YES)) {
                    datetime.setHour(timePicker.getCurrentHour());
                    datetime.setMinute(timePicker.getCurrentMinute());
                }

                // Create intent
                Intent intent = new Intent();
                intent.putExtra(EXTRA_DATETIME, datetime);

                // Set result and finish activity
                setResult(RESULT_OK, intent);
                finish();

                // Returning animation
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                // If the selected datetime is not valid, show an error
                // message.

                // Get the error message's layout and scroll view
                LinearLayout layout_error = (LinearLayout) findViewById(
                        R.id.layout_timeblock_error);
                ScrollView scrollView = (ScrollView) findViewById(
                        R.id.activity_voidblock_create_datetime);

                // Show the error message
                layout_error.setVisibility(View.VISIBLE);
                // Scroll to the top to see the error message
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
