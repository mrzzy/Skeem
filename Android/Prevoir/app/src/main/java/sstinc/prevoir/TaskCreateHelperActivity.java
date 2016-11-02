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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Switch;

import java.util.ArrayList;
import java.util.Calendar;
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
    // Intent Extras
    public static final String EXTRA_WEEKDAYS = "sstinc.prevoir.EXTRA_WEEKDAYS";
    public static final String EXTRA_DURATION = "sstinc.prevoir.EXTRA_DURATION";
    public static final String EXTRA_MIN_TIME_PERIOD = "sstinc.prevoir.EXTRA_MIN_TIME_PERIOD";
    public static final String EXTRA_DEADLINE = "sstinc.prevoir.EXTRA_DEADLINE";
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

        // Set date picker minimum time to current date
        DatePicker datePicker_deadline = (DatePicker) findViewById(R.id.date_picker_deadline);
        Calendar cal = Calendar.getInstance();
        datePicker_deadline.setMinDate(cal.getTimeInMillis());

        // Set toggle button actions
        // Repeat Yes/No Toggle Button
        Switch switch_onetime_repetitive = (Switch) findViewById(
                R.id.switch_onetime_repetitive);
        Button setDaysButton = (Button) findViewById(R.id.button_repetitions);
        setDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDayValues();
            }
        });
        switch_onetime_repetitive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If it is repetitive
                Switch switch_onetime_repetitive = (Switch) findViewById(
                        R.id.switch_onetime_repetitive);
                Button setDaysButton = (Button) findViewById(R.id.button_repetitions);
                Switch switch_deadline = (Switch) findViewById(R.id.switch_deadline);

                if (switch_onetime_repetitive.isChecked()) {
                    if (weekDays.isEmpty()) {
                        getDayValues();
                        if (!weekDays.isEmpty()) {
                            switch_deadline.setVisibility(View.VISIBLE);
                        }
                    } else {
                        setDaysButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    switch_deadline.setVisibility(View.INVISIBLE);
                    switch_deadline.setChecked(true);
                    setDaysButton.setVisibility(View.GONE);
                }
            }
        });
        // Minimum Time Period Yes/No Toggle Button
        Switch switch_min_time_period = (Switch) findViewById(
                R.id.switch_min_time_period);
        switch_min_time_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch switch_min_time_period = (Switch) findViewById(
                        R.id.switch_min_time_period);
                Spinner spinner_min_time_period_hours = (Spinner) findViewById(
                        R.id.spinner_min_time_period_hours);
                Spinner spinner_min_time_period_minutes = (Spinner) findViewById(
                        R.id.spinner_min_time_period_minutes);
                TextView textView_min_time_period_hours = (TextView) findViewById(
                        R.id.text_view_min_time_period_hours);
                TextView textView_min_time_period_minutes = (TextView) findViewById(
                        R.id.text_view_min_time_period_minutes);
                // Set minimum time period
                if (switch_min_time_period.isChecked()) {
                    spinner_min_time_period_hours.setVisibility(View.VISIBLE);
                    spinner_min_time_period_minutes.setVisibility(View.VISIBLE);
                    textView_min_time_period_hours.setVisibility(View.VISIBLE);
                    textView_min_time_period_minutes.setVisibility(View.VISIBLE);
                } else {
                    spinner_min_time_period_hours.setVisibility(View.GONE);
                    spinner_min_time_period_minutes.setVisibility(View.GONE);
                    textView_min_time_period_hours.setVisibility(View.GONE);
                    textView_min_time_period_minutes.setVisibility(View.GONE);
                }
            }
        });
        // Deadline Time Switch
        Switch switch_deadline_time = (Switch) findViewById(
                R.id.switch_deadline_add_time);
        switch_deadline_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Switch switch_deadline_time = (Switch) findViewById(
                        R.id.switch_deadline_add_time);
                TimePicker timePicker_deadline = (TimePicker) findViewById(
                        R.id.time_picker_deadline);
                // Set deadline time
                if (switch_deadline_time.isChecked()) {
                    timePicker_deadline.setVisibility(View.VISIBLE);
                } else {
                    timePicker_deadline.setVisibility(View.GONE);
                }
            }
        });
        // Deadline Switch
        Switch switch_deadline = (Switch) findViewById(R.id.switch_deadline);
        switch_deadline.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                // Time Text View
                TextView textView_deadline_time = (TextView) findViewById(
                        R.id.text_view_deadline_time);
                // Time Switch
                Switch switch_deadline_time = (Switch) findViewById(
                        R.id.switch_deadline_add_time);
                // Date Picker
                DatePicker datePicker_deadline = (DatePicker) findViewById(
                        R.id.date_picker_deadline);
                // Time Picker
                TimePicker timePicker_deadline = (TimePicker) findViewById(
                        R.id.time_picker_deadline);

                if (compoundButton.isChecked()) {
                    // Make components visible
                    textView_deadline_time.setVisibility(View.VISIBLE);
                    switch_deadline_time.setVisibility(View.VISIBLE);
                    datePicker_deadline.setVisibility(View.VISIBLE);
                    if (switch_deadline_time.isChecked()) {
                        timePicker_deadline.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Make components invisible
                    textView_deadline_time.setVisibility(View.GONE);
                    switch_deadline_time.setVisibility(View.GONE);
                    datePicker_deadline.setVisibility(View.GONE);
                    timePicker_deadline.setVisibility(View.GONE);
                }
            }
        });


        // Set spinner values
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

        // Set values if it is edit
        Task task = getIntent().getParcelableExtra(TaskFragment.EXTRA_TASK);

        if (task != null) {
            edit = getIntent().getBooleanExtra(TaskCreateActivity.EXTRA_HAS_OLD_INFORMATION, false);

            // Set weekDays
            weekDays = task.getWeekDays().getWeekDays_list();
            if (!weekDays.isEmpty()) {
                switch_onetime_repetitive.setChecked(true);
                setDaysButton.setText(getTextToSet());
                setDaysButton.setVisibility(View.VISIBLE);
            }
            // Set duration
            spinner_duration_hours.setSelection(
                    hourArrayAdapter.getPosition(
                            Integer.toString(task.getPeriodNeeded().getHours())));
            spinner_duration_minutes.setSelection(
                    minuteArrayAdapter.getPosition(
                            Integer.toString(task.getPeriodNeeded().getMinutes())));

            // Minimum Time Period
            if (task.getPeriodMinimum().getHours() != -1) {
                switch_min_time_period.setChecked(true);
                spinner_min_time_period_hours.setSelection(hourArrayAdapter.getPosition(
                        Integer.toString(task.getPeriodMinimum().getHours())));
                spinner_min_time_period_minutes.setSelection(minuteArrayAdapter.getPosition(
                        Integer.toString(task.getPeriodMinimum().getMinutes())));
                spinner_min_time_period_hours.setVisibility(View.VISIBLE);
                spinner_min_time_period_minutes.setVisibility(View.VISIBLE);
            }

            // Deadline
            datePicker_deadline.updateDate(task.getDeadline().getYear(),
                    task.getDeadline().getMonth(), task.getDeadline().getDay());
            if (task.getDeadline().getHasTime()) {
                ScrollableTimePicker timePicker_deadline = (ScrollableTimePicker) findViewById(
                        R.id.time_picker_deadline);

                switch_deadline_time.setChecked(true);
                timePicker_deadline.setVisibility(View.VISIBLE);

                // Set time for time picker
                timePicker_deadline.setCurrentHour(task.getDeadline().getHour());
                timePicker_deadline.setCurrentMinute(task.getDeadline().getMinute());
            }
        }
    }

    private void getDayValues() {
        // Start new activity to set days
        Intent intent = new Intent(getApplicationContext(),
                TaskCreateDaysActivity.class);
        ArrayList<String> stringWeekDays = new ArrayList<>();
        for (WeekDays.WeekDay weekDay : weekDays) {
            stringWeekDays.add(weekDay.toString());
        }
        intent.putExtra(EXTRA_CURRENT_DAYS, stringWeekDays);
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
        int id = item.getItemId();
        // Return values back to previous activity
        Intent intent = new Intent();
        // WeekDays (Repeated Days)
        // Convert weekDays to ArrayList<String>
        ArrayList<String> arrayListWeekDays = new ArrayList<>();
        for (WeekDays.WeekDay weekDay : weekDays) {
            arrayListWeekDays.add(weekDay.toString());
        }
        intent.putExtra(TaskCreateActivity.EXTRA_WEEKDAYS, arrayListWeekDays);
        // Duration
        Spinner spinner_duration_hours = (Spinner) findViewById(R.id.spinner_duration_hours);
        Spinner spinner_duration_minutes = (Spinner) findViewById(
                R.id.spinner_duration_minutes);
        int duration_hours = Integer.parseInt(
                (String) spinner_duration_hours.getSelectedItem());
        int duration_minutes = Integer.parseInt(
                (String) spinner_duration_minutes.getSelectedItem());

        intent.putExtra(TaskCreateActivity.EXTRA_DURATION,
                (new Duration(duration_hours, duration_minutes).toString()));
        // Minimum Time Period
        Spinner spinner_min_time_period_hours = (Spinner) findViewById(
                R.id.spinner_min_time_period_hours);
        Spinner spinner_min_time_period_minutes = (Spinner) findViewById(
                R.id.spinner_min_time_period_minutes);
        Switch switch_min_time_period = (Switch) findViewById(
                R.id.switch_min_time_period);
        int min_time_period_hours = -1;
        int min_time_period_minutes = -1;

        if (switch_min_time_period.isChecked()) {
            min_time_period_hours = Integer.parseInt(
                    (String) spinner_min_time_period_hours.getSelectedItem());
            min_time_period_minutes = Integer.parseInt(
                    (String) spinner_min_time_period_minutes.getSelectedItem());
        }

        intent.putExtra(TaskCreateActivity.EXTRA_MIN_TIME_PERIOD,
                (new Duration(min_time_period_hours, min_time_period_minutes).toString()));

        // Deadline Date
        ScrollableDatePicker datePicker_deadline = (ScrollableDatePicker) findViewById(
                R.id.date_picker_deadline);
        ScrollableTimePicker timePicker_deadline = (ScrollableTimePicker) findViewById(
                R.id.time_picker_deadline);
        Switch switch_add_time = (Switch) findViewById(
                R.id.switch_deadline_add_time);
        Switch switch_deadline = (Switch) findViewById(R.id.switch_deadline);

        Datetime datetime = new Datetime();
        if (switch_deadline.isChecked()) {
            datetime.setYear(datePicker_deadline.getYear());
            datetime.setMonth(datePicker_deadline.getMonth());
            datetime.setDay(datePicker_deadline.getDayOfMonth());
            if (switch_add_time.isChecked()) {
                datetime.setHour(timePicker_deadline.getCurrentHour());
                datetime.setMinute(timePicker_deadline.getCurrentMinute());
            }
        }

        intent.putExtra(TaskCreateActivity.EXTRA_DEADLINE, datetime.toString());

        // Return ID
        intent.putExtra(TaskCreateActivity.EXTRA_TASK_ID, getIntent().getLongExtra(
                TaskCreateActivity.EXTRA_TASK_ID, -1));

        // Back button
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

    private String getTextToSet() {
        String textToSet = "";
        for (WeekDays.WeekDay weekDay : weekDays) {
            textToSet += weekDay.toString().substring(0, 3);
            textToSet += ", ";
        }
        if (!textToSet.equals("")) {
            textToSet = textToSet.substring(0, textToSet.length()-2);
        } else {
            textToSet = getString(R.string.button_repetitions_unset);
        }

        return textToSet;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == createDaysRequestCode) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> days = data.getStringArrayListExtra(EXTRA_DAYS);
                weekDays = new ArrayList<>();
                for (String day : days) {
                    weekDays.add(WeekDays.WeekDay.valueOf(day.toUpperCase()));
                }

                Switch switch_deadline = (Switch) findViewById(R.id.switch_deadline);
                if (!weekDays.isEmpty()) {
                    switch_deadline.setVisibility(View.VISIBLE);
                } else {
                    switch_deadline.setVisibility(View.INVISIBLE);
                }

                // Set button text
                Button setWeekDaysButton = (Button) findViewById(R.id.button_repetitions);
                setWeekDaysButton.setText(getTextToSet());
                setWeekDaysButton.setVisibility(View.VISIBLE);
            } else {
                invalidateOptionsMenu();
            }
        }
    }
}
