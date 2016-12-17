package sstinc.skeem;

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

/* TODO: You can go before the current time but that would not be allowed.
   To replicate: Go to next year. Set date to a day before the minimum day and month of last year.
   Go to the previous year.
 */

public class CreateDatetimeActivity extends AppCompatActivity {
    // Intent Extras
    //NOTE: These Constants are Deprecated
    @Deprecated
    public static final String EXTRA_RECEIVE_HASDATE = "sstinc.skeem.EXTRA_RECEIVE_HASDATE";
    @Deprecated
    public static final String EXTRA_RECEIVE_HASTIME = "sstinc.skeem.EXTRA_RECEIVE_HASTIME";
    //NOTE: Use these constants instead
    public static final String EXTRA_RECEIVE_HAS_DATE = "sstinc.skeem.EXTRA_RECEIVE_HASDATE";
    public static final String EXTRA_RECEIVE_HAS_TIME = "sstinc.skeem.EXTRA_RECEIVE_HASTIME";

    public static final String EXTRA_RECEIVE_TITLE = "sstinc.skeem.EXTRA_TITLE";
    public static final String EXTRA_RECEIVE_DATETIME = "sstinc.skeem.EXTRA_RECEIVE_DATETIME";
    public static final String EXTRA_RECEIVE_MAX = "sstinc.skeem.EXTRA_RECEIVE_MAX";
    public static final String EXTRA_RECEIVE_MIN = "sstinc.skeem.EXTRA_RECEIVE_MIN";
    public static final String EXTRA_DATETIME = "sstinc.skeem.EXTRA_DATETIME";
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Misc
    //NOTE: These Constants are Deprecated
    @Deprecated
    public static final String HASTIME_YES = "YES";
    @Deprecated
    public static final String HASTIME_OPTIONAL = "OPTIONAL";
    @Deprecated
    public static final String HASTIME_NO = "NO";
    //NOTE: Use these constants instead
    public static final String HAS_TIME_TRUE = "YES";
    public static final String HAS_TIME_OPTIONAL = "OPTIONAL";
    public static final String HAS_TIME_FALSE = "NO";

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

        // Retrieve Information from intent
        Intent intent = getIntent();
        this.hasDate = intent.getBooleanExtra(EXTRA_RECEIVE_HAS_DATE, true);
        this.hasTime = intent.getStringExtra(EXTRA_RECEIVE_HAS_TIME);
        this.datetime = intent.getParcelableExtra(EXTRA_RECEIVE_DATETIME);
        this.max_datetime = intent.getParcelableExtra(EXTRA_RECEIVE_MAX);
        this.min_datetime = intent.getParcelableExtra(EXTRA_RECEIVE_MIN);
        if (this.min_datetime == null) {
            this.min_datetime = new Datetime(org.joda.time.DateTime.now());
        }
        if (this.max_datetime == null) {
            this.max_datetime = new Datetime();
        }

        //Setup Date Picker
        ScrollableDatePicker datePicker = (ScrollableDatePicker) findViewById(
                R.id.datetime_date_picker);
        datePicker.setVisibility(this.hasDate? View.VISIBLE : View.GONE);
        // Fill in the values of datetime if there were any
        if (this.datetime.getHasDate()) {
            datePicker.updateDate(this.datetime.getYear(), this.datetime.getMonth()-1,
                    this.datetime.getDay());
        }
        // Set the maximum and minimum if any (time is verified later)
        if (this.max_datetime.getHasDate()) {
            datePicker.setMaxDate(this.max_datetime.getMillis());
        }
        if (this.min_datetime.getHasDate()) {
            //TODO: Test this thoroughly
            // Move maximum one day back
            datePicker.setMinDate(this.min_datetime.getMillis());
        }

        //Setup Time Picker
        ScrollableTimePicker timePicker = (ScrollableTimePicker) findViewById(
                R.id.datetime_time_picker);
        if (this.datetime.getHasTime()) {
            timePicker.setCurrentHour(this.datetime.getHour());
            timePicker.setCurrentMinute(this.datetime.getMinute());
        }
        if (this.hasTime.equals(HAS_TIME_TRUE)) {
            timePicker.setVisibility(View.VISIBLE);
        } else {
            timePicker.setVisibility(View.GONE);
        }

        // Set visibility of optional time switch
        if (this.hasTime.equals(HAS_TIME_OPTIONAL)) {
            // Default hasTime to HAS_TIME_FALSE
            this.hasTime = HAS_TIME_FALSE;

            // Get switch
            Switch switch_addTime = (Switch) findViewById(R.id.datetime_switch_add_time);
            // Change the value of hasTime and the visibility of the time
            // picker every time it changes.
            switch_addTime.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    // Set value of hasTime
                    hasTime = b? HAS_TIME_TRUE : HAS_TIME_FALSE;

                    // Set visibility of time picker
                    ScrollableTimePicker timePicker = (ScrollableTimePicker) findViewById(
                            R.id.datetime_time_picker);
                    timePicker.setVisibility(b? View.VISIBLE : View.GONE);
                }
            });

            if (this.datetime.getHasTime()) {
                switch_addTime.setChecked(true);
            }

            LinearLayout add_time_layout = (LinearLayout) findViewById(
                    R.id.datetime_add_time_layout);
            add_time_layout.setVisibility(View.VISIBLE);
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
        if (this.hasTime.equals(HAS_TIME_TRUE)) {
            submitted_datetime.setHour(timePicker.getCurrentHour());
            submitted_datetime.setMinute(timePicker.getCurrentMinute());
        }

        // Flag to indicate if valid
        boolean dateTimeVaild = true;
        // If the minimum datetime is set
        if (this.min_datetime.getHasDate() && this.hasDate) {
            // Compare date
            dateTimeVaild = this.min_datetime.compareDates(submitted_datetime) != 1;
        }
        if (this.min_datetime.getHasTime() && this.hasTime.equals(HAS_TIME_TRUE)) {
            // Compare time
            dateTimeVaild = dateTimeVaild &&
                    this.min_datetime.compareTimes(submitted_datetime) != 1;
        }
        // If the maximum datetime is set
        if (this.max_datetime.getHasDate() && this.hasDate) {
            // Compare date
            dateTimeVaild = dateTimeVaild &&
                    this.max_datetime.compareDates(submitted_datetime) != 1;
        }
        if (this.max_datetime.getHasTime() && this.hasTime.equals(HAS_TIME_TRUE)) {
            // Compare time
            dateTimeVaild = dateTimeVaild &&
                    this.max_datetime.compareTimes(submitted_datetime) != 1;
        }

        return dateTimeVaild;
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
                if (this.hasTime.equals(HAS_TIME_TRUE)) {
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
