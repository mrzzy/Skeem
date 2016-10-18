package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;

import static android.view.View.GONE;

public class TaskCreateHelperActivity extends AppCompatActivity {

    static final int createTaskDaysRequestCode = 102;

    public static final String EXTRA_DAYS = "sstinc.prevoir.EXTRA_DAYS";
    public static final String EXTRA_CURRENT_DAYS = "sstinc.prevoir.EXTRA_CURRENT_DAYS";

    static boolean show_done = true;
    static boolean edit = false;

    // Values of the task
    private ArrayList<Task.WeekDay> weekDays = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create_helper);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Set the proper menu
        invalidateOptionsMenu();

        // Set back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Task Settings");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set date picker min time
        DatePicker datePicker_deadline = (DatePicker) findViewById(R.id.date_picker_deadline);
        Calendar cal = Calendar.getInstance();
        datePicker_deadline.setMinDate(cal.getTimeInMillis());

        // Set toggle button actions
        // Repeat Yes/No Toggle Button
        ToggleButton toggleButton_onetime_repetitive = (ToggleButton) findViewById(
                R.id.field_toggle_onetime_repetitive);
        Button setDaysButton = (Button) findViewById(R.id.button_repetitions);
        setDaysButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDayValues();
            }
        });
        toggleButton_onetime_repetitive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If it is repetitive
                ToggleButton toggleButton_onetime_repetitive = (ToggleButton) findViewById(
                        R.id.field_toggle_onetime_repetitive);
                Button setDaysButton = (Button) findViewById(R.id.button_repetitions);
                if (toggleButton_onetime_repetitive.isChecked()) {
                    if (weekDays.isEmpty()) {
                        getDayValues();
                    } else {
                        setDaysButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    setDaysButton.setVisibility(View.GONE);
                }
            }
        });
        // Minimum Time Period Yes/No Toggle Button
        ToggleButton toggleButton_min_time_period = (ToggleButton) findViewById(
                R.id.field_toggle_min_time_period);
        toggleButton_min_time_period.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton toggleButton_min_time_period = (ToggleButton) findViewById(
                        R.id.field_toggle_min_time_period);
                Spinner spinner_min_time_period_hours = (Spinner) findViewById(
                        R.id.spinner_min_time_period_hours);
                Spinner spinner_min_time_period_minutes = (Spinner) findViewById(
                        R.id.spinner_min_time_period_minutes);
                TextView textView_min_time_period_hours = (TextView) findViewById(
                        R.id.text_view_min_time_period_hours);
                TextView textView_min_time_period_minutes = (TextView) findViewById(
                        R.id.text_view_min_time_period_minutes);
                // Set minimum time period
                if (toggleButton_min_time_period.isChecked()) {
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
        // Deadline Time With/Without Toggle Button
        ToggleButton toggleButton_deadline_time = (ToggleButton) findViewById(
                R.id.field_toggle_deadline_add_time);
        toggleButton_deadline_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleButton toggleButton_deadline_time = (ToggleButton) findViewById(
                        R.id.field_toggle_deadline_add_time);
                TimePicker timePicker_deadline = (TimePicker) findViewById(
                        R.id.time_picker_deadline);
                // Set deadline time
                if (toggleButton_deadline_time.isChecked()) {
                    timePicker_deadline.setVisibility(View.VISIBLE);
                } else {
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
        // TODO: Set item selected listeners
        // Get minimum time period spinners
        Spinner spinner_min_time_period_hours = (Spinner) findViewById(
                R.id.spinner_min_time_period_hours);
        Spinner spinner_min_time_period_minutes = (Spinner) findViewById(
                R.id.spinner_min_time_period_minutes);
        // Set minimum time period spinner adapters
        spinner_min_time_period_hours.setAdapter(hourArrayAdapter);
        spinner_min_time_period_minutes.setAdapter(minuteArrayAdapter);
        // TODO: set item selected listeners

        // Set values if it is edit
        Task task = getIntent().getParcelableExtra(TaskFragment.EXTRA_TASK);
        if (task != null) {
            edit = true;
            // Set weekDays
            weekDays = task.weekDays;
            if (!weekDays.isEmpty()) {
                toggleButton_onetime_repetitive.setChecked(true);
                setDaysButton.setText(getTextToSet());
                setDaysButton.setVisibility(View.VISIBLE);
            }
            // Set duration
            spinner_duration_hours.setSelection(
                    hourArrayAdapter.getPosition(Integer.toString(task.duration.getHours())));
            spinner_duration_minutes.setSelection(
                    minuteArrayAdapter.getPosition(Integer.toString(task.duration.getMinutes())));

            // Minimum Time Period
            if (task.min_time_period.getHours() != -1) {
                toggleButton_min_time_period.setChecked(true);
                spinner_min_time_period_hours.setSelection(hourArrayAdapter.getPosition(
                        Integer.toString(task.min_time_period.getHours())));
                spinner_min_time_period_minutes.setSelection(minuteArrayAdapter.getPosition(
                        Integer.toString(task.min_time_period.getMinutes())));
                spinner_min_time_period_hours.setVisibility(View.VISIBLE);
                spinner_min_time_period_minutes.setVisibility(View.VISIBLE);
            }

            // Deadline
            datePicker_deadline.updateDate(task.deadline.deadline.getYear(),
                    task.deadline. deadline.getMonth(), task.deadline.deadline.getDay());
            if (task.deadline.hasDueTime) {
                toggleButton_deadline_time.setChecked(true);
                spinner_min_time_period_hours.setSelection(hourArrayAdapter.getPosition(
                        Integer.toString(task.deadline.deadline.getHour())));
                spinner_min_time_period_minutes.setSelection(minuteArrayAdapter.getPosition(
                        Integer.toString(task.deadline.deadline.getMinute())));
            }
        }

    }

    private void getDayValues() {
        // Start new activity to set days
        Intent intent = new Intent(getApplicationContext(),
                TaskCreateDaysActivity.class);
        ArrayList<String> stringWeekDays = new ArrayList<>();
        for (Task.WeekDay weekDay : weekDays) {
            stringWeekDays.add(weekDay.toString());
        }
        intent.putExtra(EXTRA_CURRENT_DAYS, stringWeekDays);
        startActivityForResult(intent, createTaskDaysRequestCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.nav_shuffle).setVisible(false);
        menu.findItem(R.id.nav_continue).setVisible(false);
        menu.findItem(R.id.nav_done).setVisible(show_done);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Back button
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        } else if (id == R.id.nav_done) {
            // Return values back to previous activity
            Intent intent = new Intent();
            // WeekDays (Repeated Days)
            // Convert weekDays to ArrayList<String>
            ArrayList<String> arrayListWeekDays = new ArrayList<>();
            for (Task.WeekDay weekDay : weekDays) {
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
            ToggleButton toggleButton_min_time_period = (ToggleButton) findViewById(
                    R.id.field_toggle_min_time_period);
            int min_time_period_hours = 0;
            int min_time_period_minutes = 0;

            if (toggleButton_min_time_period.isChecked()) {
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
            ToggleButton toggleButton_add_time = (ToggleButton) findViewById(
                    R.id.field_toggle_deadline_add_time);

            Datetime datetime = new Datetime();
            datetime.setYear(datePicker_deadline.getYear());
            datetime.setMonth(datePicker_deadline.getMonth());
            datetime.setDay(datePicker_deadline.getDayOfMonth());
            if (toggleButton_add_time.isChecked()) {
                datetime.setHour(timePicker_deadline.getCurrentHour());
                datetime.setMinute(timePicker_deadline.getCurrentMinute());
            }

            intent.putExtra(TaskCreateActivity.EXTRA_DEADLINE, datetime.toString());

            // Return ID
            Log.w(this.getClass().getName(),
                    "Returning ID: " + getIntent().getLongExtra(TaskCreateActivity.EXTRA_TASK_ID, -1));
            intent.putExtra(TaskCreateActivity.EXTRA_TASK_ID, getIntent().getLongExtra(
                    TaskCreateActivity.EXTRA_TASK_ID, -1));
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    private String getTextToSet() {
        String textToSet = "";
        for (Task.WeekDay weekDay : weekDays) {
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
        if (requestCode == createTaskDaysRequestCode) {
            if (resultCode == RESULT_OK) {
                ArrayList<String> days = data.getStringArrayListExtra(EXTRA_DAYS);
                weekDays = new ArrayList<>();
                for (String day : days) {
                    weekDays.add(Task.WeekDay.valueOf(day.toUpperCase()));
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
