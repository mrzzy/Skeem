package sstinc.skeem.activities;

import android.app.AlertDialog;
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

import sstinc.skeem.models.Datetime;
import sstinc.skeem.R;
import sstinc.skeem.models.Task;
import sstinc.skeem.models.WeekDays;

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
    public static final String EXTRA_WEEKDAYS = "sstinc.skeem.EXTRA_WEEKDAYS";
    public static final String EXTRA_DURATION = "sstinc.skeem.EXTRA_DURATION";
    public static final String EXTRA_MIN_TIME_PERIOD = "sstinc.skeem.EXTRA_MIN_TIME_PERIOD";
    public static final String EXTRA_DEADLINE = "sstinc.skeem.EXTRA_DEADLINE";
    public static final String EXTRA_DEADLINE_PER_DAY = "sstinc.skeem.EXTRA_DEADLINE_PER_DAY";
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
        if (this.task == null) {
            this.task = new Task();
        }

        // Set the setDaysButton to update the days when clicked
        Button setDaysButton = (Button) findViewById(R.id.button_repetitions);
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

                // Set setDaysButton text
                if (task.getWeekDays().toString().equals("")) {
                    setDaysButton.setText(R.string.button_repetitions_unset);
                } else {
                    setDaysButton.setText(task.getWeekDays().toString());
                }

                setDaysButton.setVisibility(b? View.VISIBLE : View.GONE);
                deadline_per_day_layout.setVisibility(b? View.VISIBLE : View.GONE);
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
        ArrayAdapter<String> hourArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, hourValues);
        ArrayAdapter<String> minuteArrayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, minuteValues);

        // Get duration spinners
        Spinner spinner_duration_hours = (Spinner) findViewById(R.id.spinner_duration_hours);
        Spinner spinner_duration_minutes = (Spinner) findViewById(R.id.spinner_duration_minutes);
        // Set duration spinner adapters
        spinner_duration_hours.setAdapter(hourArrayAdapter);
        spinner_duration_minutes.setAdapter(minuteArrayAdapter);


        // Set deadline and deadline per day datetime selectors
        LinearLayout deadline_layout = (LinearLayout) findViewById(R.id.layout_deadline);
        LinearLayout deadline_per_day_layout = (LinearLayout) findViewById(
                R.id.layout_deadline_per_day);

        deadline_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateDatetimeActivity.class);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_TITLE, "Set deadline");
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HAS_DATE, true);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HAS_TIME,
                        CreateDatetimeActivity.HAS_TIME_OPTIONAL);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_DATETIME, task.getDeadline());
                startActivityForResult(intent, createDeadlineRequestCode);
            }
        });
        deadline_per_day_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CreateDatetimeActivity.class);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_TITLE,
                        "Set time to finish before");
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HAS_DATE, false);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_HAS_TIME,
                        CreateDatetimeActivity.HAS_TIME_TRUE);
                intent.putExtra(CreateDatetimeActivity.EXTRA_RECEIVE_DATETIME,
                        task.getDeadlinePerDay());
                startActivityForResult(intent, createDeadlinePerDayRequestCode);
            }
        });

        // Set the values from the given task object
        // Set button text to any previously selected weekdays
        setDaysButton.setText(task.getWeekDays().toString());

        // Set weekDays
        if (!this.task.getWeekDays().getWeekDays_list().isEmpty()) {
            // Check repetitive switch
            switch_onetime_repetitive.setChecked(true);
            // Set text for setDaysButton and show it
            setDaysButton.setText(this.task.getWeekDays().toString());
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
                        Integer.toString(this.task.getPeriodNeeded().getHours())));
        spinner_duration_minutes.setSelection(minuteArrayAdapter.getPosition(
                        Integer.toString(this.task.getPeriodNeeded().getMinutes())));

        // Set Deadline
        TextView deadline = (TextView) findViewById(R.id.text_view_deadline);
        if (this.task.getDeadline().getHasDate()) {
            deadline.setText(task.getDeadline().toFormattedString());
        }
    }

    private void getDayValues() {
        // Start new activity to set days
        Intent intent = new Intent(getApplicationContext(),
                CreateRepeatedDaysActivity.class);
        intent.putExtra(CreateRepeatedDaysActivity.EXTRA_RECEIVE_DAYS,
                this.task.getWeekDays().toStringArray());
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
    public void onBackPressed() {
        // Return all values back to TaskCreateActivity
        Intent intent = new Intent();
        // Add weekdays to intent if repeat is checked
        Switch switch_repeat = (Switch) findViewById(R.id.switch_onetime_repetitive);
        if (switch_repeat.isChecked()) {
            intent.putExtra(EXTRA_WEEKDAYS, this.task.getWeekDays().toStringArray());
        } else {
            intent.putExtra(EXTRA_WEEKDAYS, new String[] {});
        }

        // Add duration to intent
        Spinner spinner_duration_hours = (Spinner) findViewById(R.id.spinner_duration_hours);
        Spinner spinner_duration_minutes = (Spinner) findViewById(
                R.id.spinner_duration_minutes);
        // Create new period for duration
        Period duration = new Period();
        duration = duration.plusHours(
                Integer.parseInt((String) spinner_duration_hours.getSelectedItem()));
        duration = duration.plusMinutes(
                Integer.parseInt((String) spinner_duration_minutes.getSelectedItem()));
        // Convert duration to PeriodFormat String and add to intent
        intent.putExtra(EXTRA_DURATION, PeriodFormat.getDefault().print(duration));

        // Deadline
        intent.putExtra(EXTRA_DEADLINE, this.task.getDeadline());
        // Deadline per day
        intent.putExtra(EXTRA_DEADLINE_PER_DAY, this.task.getDeadlinePerDay());

        setResult(RESULT_CANCELED, intent);
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Get the id of the menu item selected
        int id = item.getItemId();

        // Return all values back to TaskCreateActivity
        Intent intent = new Intent();
        // Add weekdays to intent if repeat is checked
        Switch switch_repeat = (Switch) findViewById(R.id.switch_onetime_repetitive);
        if (switch_repeat.isChecked()) {
            intent.putExtra(EXTRA_WEEKDAYS, this.task.getWeekDays().toStringArray());
        } else {
            intent.putExtra(EXTRA_WEEKDAYS, new String[] {});
        }

        // Add duration to intent
        Spinner spinner_duration_hours = (Spinner) findViewById(R.id.spinner_duration_hours);
        Spinner spinner_duration_minutes = (Spinner) findViewById(
                R.id.spinner_duration_minutes);
        // Create new period for duration
        Period duration = new Period();
        duration = duration.plusHours(
                Integer.parseInt((String) spinner_duration_hours.getSelectedItem()));
        duration = duration.plusMinutes(
                Integer.parseInt((String) spinner_duration_minutes.getSelectedItem()));
        // Convert duration to PeriodFormat String and add to intent
        intent.putExtra(EXTRA_DURATION, PeriodFormat.getDefault().print(duration));

        // Deadline
        intent.putExtra(EXTRA_DEADLINE, this.task.getDeadline());
        // Deadline per day
        intent.putExtra(EXTRA_DEADLINE_PER_DAY, this.task.getDeadlinePerDay());

        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED, intent);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (!(this.task.getDeadline().getHasDate() ||
                this.task.getDeadline().getHasTime())) {
            // If deadline not set show an alert and don't do anything
            AlertDialog.Builder deadline_not_set = new AlertDialog.Builder(this);
            deadline_not_set.setTitle(R.string.dialog_no_deadline_title);
            deadline_not_set.setMessage(R.string.dialog_no_deadline_message);
            deadline_not_set.show();
        } else if (duration.getHours() == 0 && duration.getMinutes() == 0) {
            // If the duration given is zero, show an alert and don't do
            // anything.
            AlertDialog.Builder zero_duration = new AlertDialog.Builder(this);
            zero_duration.setTitle(R.string.dialog_zero_duration_title);
            zero_duration.setMessage(R.string.dialog_zero_duration_message);
            zero_duration.show();
        } else if (!this.task.getDeadlinePerDay().getHasTime() &&
                !this.task.getWeekDays().getWeekDays_list().isEmpty() &&
                switch_repeat.isChecked()) {
            // If deadline per day is not set but there are repeated days,
            // show an alert and don't do anything.
            AlertDialog.Builder deadline_per_day_not_set = new AlertDialog.Builder(this);
            deadline_per_day_not_set.setTitle(R.string.dialog_no_deadline_per_day_title);
            deadline_per_day_not_set.setMessage(R.string.dialog_no_deadline_per_day_message);
            deadline_per_day_not_set.show();
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
                if (this.task.getWeekDays().toString().isEmpty()) {
                    setWeekDaysButton.setText(R.string.button_repetitions_unset);
                } else {
                    setWeekDaysButton.setText(this.task.getWeekDays().toString());
                }
                setWeekDaysButton.setVisibility(View.VISIBLE);
            }
        } else if (requestCode == createDeadlineRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get deadline
                this.task.setDeadline(new Datetime((Datetime) data.getParcelableExtra(
                        CreateDatetimeActivity.EXTRA_DATETIME)));

                TextView deadline = (TextView) findViewById(R.id.text_view_deadline);
                deadline.setText(this.task.getDeadline().toFormattedString());
            }
        }
        else if (requestCode == createDeadlinePerDayRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get deadline per day
                this.task.setDeadlinePerDay(new Datetime((Datetime) data.getParcelableExtra(
                        CreateDatetimeActivity.EXTRA_DATETIME)));

                TextView deadline_per_day = (TextView) findViewById(
                        R.id.text_view_deadline_per_day);
                deadline_per_day.setText(this.task.getDeadlinePerDay().toFormattedString());
            }
        }
    }
}
