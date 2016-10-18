package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

public class TaskCreateActivity extends AppCompatActivity {

    public final static String EXTRA_WEEKDAYS = "sstinc.prevoir.EXTRA_WEEKDAYS";
    public final static String EXTRA_DEADLINE = "sstinc.prevoir.EXTRA_DEADLINE";
    public final static String EXTRA_DURATION = "sstinc.prevoir.EXTRA_DURATION";
    public final static String EXTRA_MIN_TIME_PERIOD = "sstinc.prevoir.EXTRA_MIN_TIME_PERIOD";
    public static final String EXTRA_TASK_ID = "sstinc.prevoir.EXTRA_TASK_ID";

    static final int createTaskHelperRequestCode = 101;

    boolean show_continue = true;
    boolean edit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create);

        // Set back button
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

            editText_name.setText(task.name);
            editText_subject.setText(task.subject);
            editText_description.setText(task.description);
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
                ArrayList<String> stringWeekDays = data.getStringArrayListExtra(EXTRA_WEEKDAYS);
                ArrayList<Task.WeekDay> weekDays = new ArrayList<>();
                for (String weekDay : stringWeekDays) {
                    weekDays.add(Task.WeekDay.valueOf(weekDay));
                }
                // Get deadline, duration and min_time_period
                Deadline deadline = new Deadline(new Datetime(data.getStringExtra(EXTRA_DEADLINE)));
                Duration duration = new Duration(data.getStringExtra(EXTRA_DURATION));
                Duration min_time_period = new Duration(data.getStringExtra(EXTRA_MIN_TIME_PERIOD));
                // Create intent
                Intent intent = new Intent();
                Task task = new Task(name, subject, weekDays, deadline,
                        description, duration, min_time_period);
                task.setId(data.getLongExtra(EXTRA_TASK_ID, -1));
                intent.putExtra(TaskFragment.EXTRA_TASK, task);

                setResult(RESULT_OK, intent);
                finish();
            } else {
                show_continue = true;
                invalidateOptionsMenu();
            }
        }
    }
}
