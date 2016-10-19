package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;

import java.util.ArrayList;

public class TaskCreateDaysActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_create_days);

        CheckBox monday = (CheckBox) findViewById(R.id.checkBox_monday);
        CheckBox tuesday = (CheckBox) findViewById(R.id.checkBox_tuesday);
        CheckBox wednesday = (CheckBox) findViewById(R.id.checkBox_wednesday);
        CheckBox thursday = (CheckBox) findViewById(R.id.checkBox_thursday);
        CheckBox friday = (CheckBox) findViewById(R.id.checkBox_friday);
        CheckBox saturday = (CheckBox) findViewById(R.id.checkBox_saturday);
        CheckBox sunday = (CheckBox) findViewById(R.id.checkBox_sunday);

        // Set the proper menu
        invalidateOptionsMenu();

        // Set back button
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Set Repeated Days");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        ArrayList<String> weekDays = getIntent().getStringArrayListExtra(
                TaskCreateHelperActivity.EXTRA_CURRENT_DAYS);
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
            ArrayList<String> days = new ArrayList<>();

            if (monday.isChecked()) {
                days.add("monday");
            }
            if (tuesday.isChecked()) {
                days.add("tuesday");
            }
            if (wednesday.isChecked()) {
                days.add("wednesday");
            }
            if (thursday.isChecked()) {
                days.add("thursday");
            }
            if (friday.isChecked()) {
                days.add("friday");
            }
            if (saturday.isChecked()) {
                days.add("saturday");
            }
            if (sunday.isChecked()) {
                days.add("sunday");
            }

            Intent intent = new Intent();
            intent.putExtra(TaskCreateHelperActivity.EXTRA_DAYS, days);
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