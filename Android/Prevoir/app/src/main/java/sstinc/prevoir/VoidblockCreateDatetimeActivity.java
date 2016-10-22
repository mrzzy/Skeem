package sstinc.prevoir;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.util.Calendar;

public class VoidblockCreateDatetimeActivity extends AppCompatActivity {
    // Extra strings
    public static final String EXTRA_VOIDBLOCK_TIME = "sstinc.prevoir.EXTRA_VOIDBLCOK_DATETIME";
    // Menu status
    boolean menu_shuffle = false;
    boolean menu_continue = false;
    boolean menu_finish = false;
    boolean menu_duplicate = false;
    boolean menu_delete = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voidblock_create_datetime);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        // Set back button and title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Create New Voidblock");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        // Set menu
        invalidateOptionsMenu();

        // Get elements
        ScrollableDatePicker datePicker = (ScrollableDatePicker) findViewById(
                R.id.date_picker_voidblock);
        ScrollableTimePicker timePicker = (ScrollableTimePicker) findViewById(
                R.id.time_picker_voidblock);

        String asdf = getIntent().getStringExtra(
                VoidblockCreateActivity.EXTRA_VOIDBLOCK_DATETIME);
        Datetime datetime = new Datetime(asdf);

        // Set minimum date
        Calendar cal = Calendar.getInstance();

        String min_max = getIntent().getStringExtra(
                VoidblockCreateActivity.EXTRA_VOIDBLOCK_DATETIME_MIN_MAX);

        if (min_max.equals("MIN")) {
            cal.set(datetime.getYear(), datetime.getMonth(), datetime.getDay(),
                    datetime.getHour(), datetime.getMinute());
        } else if (min_max.equals("MAX")) {
            Calendar max_cal = Calendar.getInstance();
            max_cal.set(datetime.getYear(), datetime.getMonth(), datetime.getDay(),
                    datetime.getHour(), datetime.getMinute());
            datePicker.setMaxDate(max_cal.getTimeInMillis());
        }
        datePicker.setMinDate(cal.getTimeInMillis());


        String datetime_string = getIntent().getStringExtra(
                VoidblockCreateActivity.EXTRA_VOIDBLOCK_UPDATE_DATETIME);
        if (datetime_string != null) {
            Datetime set_datetime = new Datetime(datetime_string);
            datePicker.updateDate(set_datetime.getYear(), set_datetime.getMonth(),
                    set_datetime.getDay());
            timePicker.setCurrentHour(set_datetime.getHour());
            timePicker.setCurrentMinute(set_datetime.getMinute());
        }
    }

    private boolean validate_timepicker() {
        ScrollableTimePicker timePicker = (ScrollableTimePicker) findViewById(
                R.id.time_picker_voidblock);
        ScrollableDatePicker datePicker = (ScrollableDatePicker) findViewById(
                R.id.date_picker_voidblock);
        String min_max = getIntent().getStringExtra(
                VoidblockCreateActivity.EXTRA_VOIDBLOCK_DATETIME_MIN_MAX);
        Datetime datetime = new Datetime(getIntent().getStringExtra(
                VoidblockCreateActivity.EXTRA_VOIDBLOCK_DATETIME));

        Calendar cal = Calendar.getInstance();

        int picked_day = datePicker.getDayOfMonth();
        int picked_month = datePicker.getMonth();
        int picked_year = datePicker.getYear();

        int current_day = cal.get(Calendar.DAY_OF_MONTH);
        int current_month = cal.get(Calendar.MONTH);
        int current_year = cal.get(Calendar.YEAR);

        if (!min_max.isEmpty()) {
            current_day = datetime.getDay();
            current_month = datetime.getMonth();
            current_year = datetime.getYear();
        }

        if (picked_day != current_day ||
                picked_month != current_month ||
                picked_year != current_year) {
            return true;
        }

        int current_hour = cal.get(Calendar.HOUR_OF_DAY);
        int current_minute = cal.get(Calendar.MINUTE);

        if (!min_max.isEmpty()) {
            current_hour = datetime.getHour();
            current_minute = datetime.getMinute();
        }

        int picked_hour = timePicker.getCurrentHour();
        int picked_minute = timePicker.getCurrentMinute();

        if (min_max.equals("MAX")) {
            // Setting From Value. Values should be past of current.
            if (picked_hour < current_hour) {
                return true;
            } else {
                return current_hour == picked_hour && picked_minute <= current_minute;
            }
        } else {
            if (picked_hour > current_hour) {
                return true;
            } else {
                return current_hour == picked_hour && picked_minute >= current_minute;
            }
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
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (validate_timepicker()) {
                ScrollableTimePicker timePicker = (ScrollableTimePicker) findViewById(
                        R.id.time_picker_voidblock);
                ScrollableDatePicker datePicker = (ScrollableDatePicker) findViewById(
                        R.id.date_picker_voidblock);
                Datetime datetime = new Datetime();

                datetime.setYear(datePicker.getYear());
                datetime.setMonth(datePicker.getMonth());
                datetime.setDay(datePicker.getDayOfMonth());

                datetime.setHour(timePicker.getCurrentHour());
                datetime.setMinute(timePicker.getCurrentMinute());

                Intent intent = new Intent();
                intent.putExtra(EXTRA_VOIDBLOCK_TIME, datetime.toString());
                setResult(RESULT_OK, intent);
                finish();
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
            } else {
                LinearLayout layout_error = (LinearLayout) findViewById(
                        R.id.layout_timeblock_error);
                ScrollView scrollView = (ScrollView) findViewById(
                        R.id.activity_voidblock_create_datetime);

                layout_error.setVisibility(View.VISIBLE);
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        }
        return super.onOptionsItemSelected(item);
    }
}
