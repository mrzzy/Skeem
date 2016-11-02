package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ScrollView;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

import java.util.ArrayList;
//TODO: Update and retain information
/*
taskCreate
    -> FROM: Helper VALUE: weekDays
    -> FROM: Helper VALUE: Duration
    -> FROM: Helper VALUE: min_time_period
    -> FROM: Helper VALUE: Deadline
    -> TO: Fragment VALUE: task
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
public class TaskCreateActivity extends AppCompatActivity {
    public final static String EXTRA_WEEKDAYS = "sstinc.prevoir.EXTRA_WEEKDAYS";
    public final static String EXTRA_DEADLINE = "sstinc.prevoir.EXTRA_DEADLINE";
    public final static String EXTRA_DURATION = "sstinc.prevoir.EXTRA_DURATION";
    public final static String EXTRA_MIN_TIME_PERIOD = "sstinc.prevoir.EXTRA_MIN_TIME_PERIOD";
    public static final String EXTRA_TASK_ID = "sstinc.prevoir.EXTRA_TASK_ID";

    public static final String EXTRA_HAS_OLD_INFORMATION = "sstinc.prevoir.EXTRA_HAS_OLD_INFORMATION";

    static final int createTaskHelperRequestCode = 111;

    boolean show_continue = true;
    boolean edit = false;
    Task cont_task = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);

        // Set back button and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create New Task");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Task task = getIntent().getParcelableExtra(TaskFragment.EXTRA_TASK);
        if (task != null) {
            edit = true;
            // Set the values of the fields
            EditText editText_name = (EditText) findViewById(R.id.field_text_task_name);
            EditText editText_subject = (EditText) findViewById(R.id.field_text_task_subject);
            EditText editText_description = (EditText) findViewById(R.id.field_text_description);

            editText_name.setText(task.getName());
            editText_subject.setText(task.getSubject());
            editText_description.setText(task.getDescription());
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.nav_shuffle).setVisible(false);
        menu.findItem(R.id.nav_done).setVisible(false);
        menu.findItem(R.id.nav_continue).setVisible(show_continue);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // Back button
        if (id == android.R.id.home) {
            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.nav_continue) {
            // start next activity
            Intent intent = new Intent(this, TaskCreateHelperActivity.class);
            if (edit) {
                Task task = getIntent().getParcelableExtra(TaskFragment.EXTRA_TASK);
                intent.putExtra(TaskFragment.EXTRA_TASK, task);
                intent.putExtra(EXTRA_TASK_ID, task.getId());
            } else if (cont_task != null) {
                intent.putExtra(TaskFragment.EXTRA_TASK, cont_task);
                intent.putExtra(EXTRA_HAS_OLD_INFORMATION, true);
            }
            startActivityForResult(intent, createTaskHelperRequestCode);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == createTaskHelperRequestCode) {
            if (resultCode == RESULT_OK) {
                // Get name, subject and description
                EditText editText_name = (EditText) findViewById(R.id.field_text_task_name);
                EditText editText_subject = (EditText) findViewById(R.id.field_text_task_subject);
                EditText editText_description = (EditText) findViewById(
                        R.id.field_text_description);

                String name = editText_name.getText().toString();
                String subject = editText_subject.getText().toString();
                String description = editText_description.getText().toString();
                // Get weekdays
                WeekDays weekDays = new WeekDays();
                ArrayList<String> stringWeekDays = data.getStringArrayListExtra(EXTRA_WEEKDAYS);
                for (String weekDay : stringWeekDays) {
                    weekDays.add(WeekDays.WeekDay.valueOf(weekDay));
                }
                // Get deadline, duration and min_time_period
                Datetime deadline = new Datetime(data.getStringExtra(EXTRA_DEADLINE));
                Period period = PeriodFormat.getDefault().parsePeriod(
                        data.getStringExtra(EXTRA_DURATION));
                Period min_time_period = PeriodFormat.getDefault().parsePeriod(
                        data.getStringExtra(EXTRA_MIN_TIME_PERIOD));
                // Create intent
                Intent intent = new Intent();
                Task task = new Task();
                task.setName(name);
                task.setSubject(subject);
                task.setWeekDays(weekDays);
                task.setDeadline(deadline);
                task.setPeriodNeeded(period);
                task.setPeriodMinimum(min_time_period);
                task.setId(data.getLongExtra(EXTRA_TASK_ID, -1));
                intent.putExtra(TaskFragment.EXTRA_TASK, task);

                setResult(RESULT_OK, intent);
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                // Get Week Days
                WeekDays weekDays = new WeekDays();
                ArrayList<String> stringWeekDays = data.getStringArrayListExtra(EXTRA_WEEKDAYS);
                for (String weekDay : stringWeekDays) {
                    weekDays.add(WeekDays.WeekDay.valueOf(weekDay));
                }
                // Get Deadline
                Datetime deadline = new Datetime(data.getStringExtra(EXTRA_DEADLINE));
                // Get duration and min_tim_period
                Period period = PeriodFormat.getDefault().parsePeriod(
                        data.getStringExtra(EXTRA_DURATION));
                Period min_time_period = PeriodFormat.getDefault().parsePeriod(
                        data.getStringExtra(EXTRA_MIN_TIME_PERIOD));

                cont_task = new Task();
                cont_task.setWeekDays(weekDays);
                cont_task.setDeadline(deadline);
                cont_task.setPeriodNeeded(period);
                cont_task.setPeriodMinimum(min_time_period);

                show_continue = true;
                invalidateOptionsMenu();
            }
        }
    }
}
