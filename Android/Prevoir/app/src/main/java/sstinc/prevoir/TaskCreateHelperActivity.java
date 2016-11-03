package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Switch;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import java.util.ArrayList;

//TODO: Settle UI First. Stop Repeating date(time) and deadline on day time buttons.
/*
taskCreate
    -> FROM: Helper VALUE: weekDays
    -> FROM: Helper VALUE: Duration
    -> FROM: Helper VALUE: min_time_period
    -> FROM: Helper VALUE: Deadline
taskCreateHelper
    -> FROM: days VALUE: weekDays
    -> TO: days VALUE: weekDays
    -> TO: Create VALUE: weekDays
    -> TO: Create VALUE: Duration
    -> TO: Create VALUE: min_time_period
    -> TO: Create VALUE: Deadline
days
    -> FROM: Helper VALUE: weekDays
    -> TO: Helper VALUE: weekDays
 */
public class TaskCreateHelperActivity extends AppCompatActivity {
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Request Codes
    static final int createDaysRequestCode = 310;
    static final int createDeadlineRequestCode = 111;
    static final int createDeadlinePerDayRequestCode = 112;
    // Intent Extras
    public static final String EXTRA_WEEKDAYS = "sstinc.prevoir.EXTRA_WEEKDAYS";
    public static final String EXTRA_DURATION = "sstinc.prevoir.EXTRA_DURATION";
    public static final String EXTRA_MIN_TIME_PERIOD = "sstinc.prevoir.EXTRA_MIN_TIME_PERIOD";
    public static final String EXTRA_DEADLINE = "sstinc.prevoir.EXTRA_DEADLINE";
    public static final String EXTRA_DEADLINE_PER_DAY = "sstinc.prevoir.EXTRA_DEADLINE_PER_DAY";
    // Misc
    Task task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create_helper);
        // Animate
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Set the proper menu
        menu_finish = true;
        invalidateOptionsMenu();

        // Set back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Task Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Get task from intent
        this.task = getIntent().getParcelableExtra(TaskCreateActivity.EXTRA_TASK);

        // Set the setDaysButton to update the days when clicked
        Button setDaysButton = (Button) findViewById(R.id.button_repetitions);
        // Set button text to any previously selected weekdays
        setDaysButton.setText(task.getWeekDays().toString());
        setDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDayValues();
            }
        });

        // Set switch actions
        // Repeat switch
        Switch switch_onetime_repetitive = (Switch) findViewById(
                R.id.switch_onetime_repetitive);
        switch_onetime_repetitive.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Toggle visibility of setDaysButton
                Button setDaysButton = (Button) findViewById(R.id.button_repetitions);
                LinearLayout deadline_per_day_layout = (LinearLayout) findViewById(
                        R.id.layout_deadline_per_day);

                setDaysButton.setVisibility(b? View.VISIBLE : View.GONE);
                deadline_per_day_layout.setVisibility(b? View.VISIBLE : View.GONE);
            }
        });

        // Minimum time period switch
        Switch switch_min_time_period = (Switch) findViewById(
                R.id.switch_min_time_period);
        switch_min_time_period.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Toggle visibility of min_time_period_layout
                LinearLayout min_time_period_layout = (LinearLayout) findViewById(
                        R.id.min_time_period_layout);
                min_time_period_layout.setVisibility(b? View.VISIBLE : View.GONE);
            }
        });

        // Set spinner values
        // Create defined set of values to choose from
        String[] hourValues = {"0", "1", "2", "3", "4", "5", "6"};
        String[] minuteValues = new String[60];
        for (int i=0; i<60; i++) {
            minuteValues[i] = Integer.toString(i);
        }

        // Create adapters for hours and minutes
        ArrayAdapter<String> hourArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, hourValues);
        ArrayAdapter<String> minuteArrayAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, minuteValues);

        // Get duration spinners
        Spinner spinner_duration_hours = (Spinner) findViewById(R.id.spinner_duration_hours);
        Spinner spinner_duration_minutes = (Spinner) findViewById(R.id.spinner_duration_minutes);
        // Set duration spinner adapters
        spinner_duration_hours.setAdapter(hourArrayAdapter);
        spinner_duration_minutes.setAdapter(minuteArrayAdapter);

        // Get minimum time period spinners
        Spinner spinner_min_time_period_hours = (Spinner) findViewById(
                R.id.spinner_min_time_period_hours);
        Spinner spinner_min_time_period_minutes = (Spinner) findViewById(
                R.id.spinner_min_time_period_minutes);
        // Set minimum time period spinner adapters
        spinner_min_time_period_hours.setAdapter(hourArrayAdapter);
        spinner_min_time_period_minutes.setAdapter(minuteArrayAdapter);

        // Set deadline and deadline per day datetime selectors
        LinearLayout deadline_layout = (LinearLayout) findViewById(R.id.layout_deadline);
        LinearLayout deadline_per_day_layout = (LinearLayout) findViewById(
                R.id.layout_deadline_per_day);

        deadline_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateDatetimeActivity.class);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HASDATE, true);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HASTIME,
                        CreateDatetimeActivity.HASTIME_YES);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_DATETIME,
                        task.getDeadline().toString());
                startActivityForResult(intent, createDeadlineRequestCode);
            }
        });
        deadline_per_day_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateDatetimeActivity.class);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HASDATE, true);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HASTIME,
                        CreateDatetimeActivity.HASTIME_OPTIONAL);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_DATETIME,
                        task.getDeadlinePerDay().toString());
                startActivityForResult(intent, createDeadlinePerDayRequestCode);
            }
        });

        // Set values if it is edit
        if (this.task != null) {
            // Set weekDays
            if (!task.getWeekDays().getWeekDays_list().isEmpty()) {
                // Check repetitive switch
                switch_onetime_repetitive.setChecked(true);
                // Set text for setDaysButton and show it
                setDaysButton.setText(task.getWeekDays().toString());
                setDaysButton.setVisibility(View.VISIBLE);

                // Set deadline per day
                TextView deadline_per_day = (TextView) findViewById(
                        R.id.text_view_deadline_per_day);
                deadline_per_day.setText(task.getDeadlinePerDay().toFormattedString());

                // Make deadline per day visible
                deadline_per_day_layout.setVisibility(View.VISIBLE);
            }

            // Set duration
            spinner_duration_hours.setSelection(hourArrayAdapter.getPosition(
                            Integer.toString(task.getPeriodNeeded().getHours())));
            spinner_duration_minutes.setSelection(minuteArrayAdapter.getPosition(
                            Integer.toString(task.getPeriodNeeded().getMinutes())));

            // Set minimum time period
            if (!task.getPeriodMinimum().equals(new Period())) {
                // Check the min_time_period switch
                switch_min_time_period.setChecked(true);
                // Set the appropriate hours and minutes
                spinner_min_time_period_hours.setSelection(hourArrayAdapter.getPosition(
                        Integer.toString(task.getPeriodMinimum().getHours())));
                spinner_min_time_period_minutes.setSelection(minuteArrayAdapter.getPosition(
                        Integer.toString(task.getPeriodMinimum().getMinutes())));

                // Get layout and make it visible
                LinearLayout min_time_period_layout = (LinearLayout) findViewById(
                        R.id.min_time_period_layout);
                min_time_period_layout.setVisibility(View.VISIBLE);
            }

            // Set Deadline
            TextView deadline = (TextView) findViewById(R.id.text_view_deadline);
            deadline.setText(task.getDeadline().toFormattedString());
        }
    }

    private void getDayValues() {
        // Start new activity to set days
        Intent intent = new Intent(getApplicationContext(),
                CreateRepeatedDaysActivity.class);
        intent.putExtra(EXTRA_WEEKDAYS, this.task.getWeekDays().toStringArray());
        startActivityForResult(intent, createDaysRequestCode);
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

        // Return all values back to TaskCreateActivity
        Intent intent = new Intent();
        // Add weekdays to intent
        intent.putExtra(EXTRA_WEEKDAYS, this.task.getWeekDays().toStringArray());

        // Add duration to intent
        Spinner spinner_duration_hours = (Spinner) findViewById(R.id.spinner_duration_hours);
        Spinner spinner_duration_minutes = (Spinner) findViewById(
                R.id.spinner_duration_minutes);
        // Create new period for duration
        Period duration = new Period();
        duration.plusHours(Integer.parseInt((String) spinner_duration_hours.getSelectedItem()));
        duration.plusMinutes(Integer.parseInt((String) spinner_duration_minutes.getSelectedItem()));
        // Convert duration to PeriodFormat String and add to intent
        intent.putExtra(EXTRA_DURATION, PeriodFormat.getDefault().print(duration));

        // Add minimum time period to intent
        Spinner spinner_min_time_period_hours = (Spinner) findViewById(
                R.id.spinner_min_time_period_hours);
        Spinner spinner_min_time_period_minutes = (Spinner) findViewById(
                R.id.spinner_min_time_period_minutes);
        // Create new period for min_time_period
        Period min_time_period = new Period();
        min_time_period.plusHours(Integer.parseInt(
                (String) spinner_min_time_period_hours.getSelectedItem()));
        min_time_period.plusMinutes(Integer.parseInt(
                (String) spinner_min_time_period_minutes.getSelectedItem()));
        // Convert min_time_period to PeriodFormat String and add to intent
        intent.putExtra(EXTRA_MIN_TIME_PERIOD, PeriodFormat.getDefault().print(min_time_period));

        // Deadline
        intent.putExtra(EXTRA_DEADLINE, this.task.getDeadline().toString());
        // Deadline per day
        intent.putExtra(EXTRA_DEADLINE_PER_DAY, this.task.getDeadlinePerDay().toString());

        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED, intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (id == R.id.nav_done) {
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == createDaysRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get repeated days
                this.task.setWeekDays(new WeekDays(data.getStringArrayExtra(
                        CreateRepeatedDaysActivity.EXTRA_DAYS)));

                // Set button text and make it visible
                Button setWeekDaysButton = (Button) findViewById(R.id.button_repetitions);
                setWeekDaysButton.setText(this.task.getWeekDays().toString());
                setWeekDaysButton.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == createDeadlineRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get deadline
                this.task.setDeadline(new Datetime(data.getStringExtra(
                        CreateDatetimeActivity.EXTRA_DATETIME)));

                TextView deadline = (TextView) findViewById(R.id.text_view_deadline);
                deadline.setText(this.task.getDeadline().toFormattedString());
            }
        }
        else if (requestCode == createDeadlinePerDayRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get deadline per day
                this.task.setDeadlinePerDay(new Datetime(data.getStringExtra(
                        CreateDatetimeActivity.EXTRA_DATETIME)));

                TextView deadline_per_day = (TextView) findViewById(
                        R.id.text_view_deadline_per_day);
                deadline_per_day.setText(this.task.getDeadlinePerDay().toFormattedString());
            }
        }
    }
}
