package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import org.joda.time.Period;
import org.joda.time.format.PeriodFormat;

/**
 * This activity handles the creation of a task. The activity handles the
 * name, subject and description of the task and calls other activities to
 * get more information. Upon gathering all the information, it will create
 * a task and pass it to TaskFragment. The activity is then finished.
 *
 * @see TaskCreateHelperActivity
 * @see CreateRepeatedDaysActivity
 * @see TaskFragment
 */
public class TaskCreateActivity extends AppCompatActivity {
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Request Codes
    static final int createTaskHelperRequestCode = 110;
    // Intent Extras
    public static final String EXTRA_TASK = TaskFragment.EXTRA_TASK;
    // Misc
    Task task = null;

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

        // Get the task from TaskFragment
        this.task = getIntent().getParcelableExtra(TaskFragment.EXTRA_TASK);
        if (this.task != null) {
            // If there is a task, fill in the fields with the task's values
            // Get edit text views
            EditText editText_name = (EditText) findViewById(R.id.field_text_task_name);
            EditText editText_subject = (EditText) findViewById(R.id.field_text_task_subject);
            EditText editText_description = (EditText) findViewById(R.id.field_text_description);
            // Set edit text views
            editText_name.setText(task.getName());
            editText_subject.setText(task.getSubject());
            editText_description.setText(task.getDescription());
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
        // Get the id of the menu item selected
        int id = item.getItemId();

        if (id == android.R.id.home) {
            // If the id is the back button, finish cancel and finish the
            // activity.

            setResult(RESULT_CANCELED);
            finish();
        } else if (id == R.id.nav_continue) {
            // If the id is the continue button, start the next activity,
            // TaskCreateHelperActivity.

            // Create intent for next activity
            Intent intent = new Intent(this, TaskCreateHelperActivity.class);

            // Add extra information to the intent
            if (this.task != null) {
                // If it is an update or has information on the task
                // (retained information during create process)
                intent.putExtra(EXTRA_TASK, this.task);
            }
            // Start the activity
            startActivityForResult(intent, createTaskHelperRequestCode);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == createTaskHelperRequestCode) {
            if (resultCode == RESULT_OK) {
                // User has finished creating the task. Create the task from
                // the returned intent and pass it to TaskFragment. After
                // that, finish the activity.

                // Get name, subject and description
                EditText editText_name = (EditText) findViewById(R.id.field_text_task_name);
                EditText editText_subject = (EditText) findViewById(R.id.field_text_task_subject);
                EditText editText_description = (EditText) findViewById(
                        R.id.field_text_description);

                String name = editText_name.getText().toString();
                String subject = editText_subject.getText().toString();
                String description = editText_description.getText().toString();

                // Get weekdays, deadline, period and min_time_period
                WeekDays weekDays = new WeekDays(data.getStringArrayExtra(
                        TaskCreateHelperActivity.EXTRA_WEEKDAYS));
                Datetime deadline = new Datetime(
                        data.getStringExtra(TaskCreateHelperActivity.EXTRA_DEADLINE));
                Period period = PeriodFormat.getDefault().parsePeriod(
                        data.getStringExtra(TaskCreateHelperActivity.EXTRA_DURATION));
                Period min_time_period = PeriodFormat.getDefault().parsePeriod(
                        data.getStringExtra(TaskCreateHelperActivity.EXTRA_MIN_TIME_PERIOD));

                // Create intent
                Intent intent = new Intent();
                this.task.setName(name);
                this.task.setSubject(subject);
                this.task.setDescription(description);
                this.task.setWeekDays(weekDays);
                this.task.setDeadline(deadline);
                this.task.setPeriodNeeded(period);
                this.task.setPeriodMinimum(min_time_period);
                intent.putExtra(TaskFragment.EXTRA_TASK, this.task);

                // Pass task to TaskFragment
                setResult(RESULT_OK, intent);
                finish();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled setting the task settings. Store the values
                // in the current task.

                // Get weekdays, deadline, period and min_time_period
                WeekDays weekDays = new WeekDays(data.getStringArrayExtra(
                        TaskCreateHelperActivity.EXTRA_WEEKDAYS));
                Datetime deadline = new Datetime(
                        data.getStringExtra(TaskCreateHelperActivity.EXTRA_DEADLINE));
                Period period = PeriodFormat.getDefault().parsePeriod(
                        data.getStringExtra(TaskCreateHelperActivity.EXTRA_DURATION));
                Period min_time_period = PeriodFormat.getDefault().parsePeriod(
                        data.getStringExtra(TaskCreateHelperActivity.EXTRA_MIN_TIME_PERIOD));

                // Set the activities task for future update
                this.task.setWeekDays(weekDays);
                this.task.setDeadline(deadline);
                this.task.setPeriodNeeded(period);
                this.task.setPeriodMinimum(min_time_period);

                // Reset menu
                menu_continue = true;
                invalidateOptionsMenu();
            }
        }
    }
}
