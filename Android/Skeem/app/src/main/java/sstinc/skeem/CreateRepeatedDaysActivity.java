package sstinc.skeem;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

public class CreateRepeatedDaysActivity extends AppCompatActivity {
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;
    // Intent Extras
    public static final String EXTRA_RECEIVE_DAYS = "sstinc.skeem.EXTRA_RECEIVE_DAYS";
    public static final String EXTRA_DAYS = "sstinc.skeem.EXTRA_DAYS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create_days);

        // Setup Checkboxes
        CheckBox monday = (CheckBox) findViewById(R.id.checkBox_monday);
        CheckBox tuesday = (CheckBox) findViewById(R.id.checkBox_tuesday);
        CheckBox wednesday = (CheckBox) findViewById(R.id.checkBox_wednesday);
        CheckBox thursday = (CheckBox) findViewById(R.id.checkBox_thursday);
        CheckBox friday = (CheckBox) findViewById(R.id.checkBox_friday);
        CheckBox saturday = (CheckBox) findViewById(R.id.checkBox_saturday);
        CheckBox sunday = (CheckBox) findViewById(R.id.checkBox_sunday);
        String[] weekDays = getIntent().getStringArrayExtra(EXTRA_RECEIVE_DAYS);
        for (String weekDay : weekDays) {
            if (weekDay.equals("MONDAY")) {
                monday.setChecked(true);
            }
            if (weekDay.equals("TUESDAY")) {
                tuesday.setChecked(true);
            }
            if (weekDay.equals("WEDNESDAY")) {
                wednesday.setChecked(true);
            }
            if (weekDay.equals("THURSDAY")) {
                thursday.setChecked(true);
            }
            if (weekDay.equals("FRIDAY")) {
                friday.setChecked(true);
            }
            if (weekDay.equals("SATURDAY")) {
                saturday.setChecked(true);
            }
            if (weekDay.equals("SUNDAY")) {
                sunday.setChecked(true);
            }
        }

        // Setup Menu
        invalidateOptionsMenu();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Set Repeated Days");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        CheckBox monday = (CheckBox) findViewById(R.id.checkBox_monday);
        CheckBox tuesday = (CheckBox) findViewById(R.id.checkBox_tuesday);
        CheckBox wednesday = (CheckBox) findViewById(R.id.checkBox_wednesday);
        CheckBox thursday = (CheckBox) findViewById(R.id.checkBox_thursday);
        CheckBox friday = (CheckBox) findViewById(R.id.checkBox_friday);
        CheckBox saturday = (CheckBox) findViewById(R.id.checkBox_saturday);
        CheckBox sunday = (CheckBox) findViewById(R.id.checkBox_sunday);

        // Back button
        if (id == android.R.id.home) {
            // Get values
            WeekDays weekDays = new WeekDays();

            if (monday.isChecked()) {
                weekDays.add(WeekDays.WeekDay.MONDAY);
            }
            if (tuesday.isChecked()) {
                weekDays.add(WeekDays.WeekDay.TUESDAY);
            }
            if (wednesday.isChecked()) {
                weekDays.add(WeekDays.WeekDay.WEDNESDAY);
            }
            if (thursday.isChecked()) {
                weekDays.add(WeekDays.WeekDay.THURSDAY);
            }
            if (friday.isChecked()) {
                weekDays.add(WeekDays.WeekDay.FRIDAY);
            }
            if (saturday.isChecked()) {
                weekDays.add(WeekDays.WeekDay.SATURDAY);
            }
            if (sunday.isChecked()) {
                weekDays.add(WeekDays.WeekDay.SUNDAY);
            }

            Intent intent = new Intent();
            intent.putExtra(EXTRA_DAYS, weekDays.toStringArray());
            setResult(RESULT_OK, intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        menu.findItem(R.id.nav_shuffle).setVisible(false);
        menu.findItem(R.id.nav_continue).setVisible(false);
        menu.findItem(R.id.nav_done).setVisible(false);
        return true;
    }
}
