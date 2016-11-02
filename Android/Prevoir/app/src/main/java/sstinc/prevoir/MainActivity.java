package sstinc.prevoir;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

//TODO: Check for assumed copy
//TODO: Move menu status to static class
//TODO: Expired tasks and voidblocks etc.
//TODO: Refactor TaskCreateDaysActivity to DaysActivity

/*
Intent codes:
    First digit: 1: Task, 2: Voidblock, 3: Days
    Second digit: 1: Create, 2: Update
    Third digit: Misc
Intent flow:
    Voidblock
        voidblockCreate
            -> FROM: days VALUE: weekDays
            -> FROM: Datetime VALUE: from_datetime
            -> FROM: Datetime VALUE: to_datetime
            -> TO: days VALUE: weekDays
            -> TO: Datetime VALUE: from_datetime
            -> TO: Datetime VALUE: to_datetime
        voidblockCreateDatetime
            -> FROM: Create VALUE: from_datetime
            -> FROM: Create VALUE: to_datetime
            -> TO: Create VALUE: from_datetime
            -> TO: Create VALUE: to_datetime
        days
            -> FROM: Create VALUE: weekDays
            -> TO: Create VALUE: weekDays
    Task
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

/**
 * The main activity of the android app. Houses the four main fragments of
 * the application: ScheduleFragment, TaskFragment, VoidblockFragment and
 * SettingFragment.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    // Toolbar status
    static boolean menu_shuffle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Toolbar */
        // Get the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        // Set the title of the toolbar
        toolbar.setTitle(R.string.menu_schedule);
        // Use the toolbar
        setSupportActionBar(toolbar);

        /* Navigation Drawer */
        // Get the navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        // Create a new toggle function for the navigation drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        // Set the toggle button of the navigation drawer
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // Use the navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /* Others */
        // Default fragment to schedule fragment
        if (savedInstanceState == null) {
            // Set schedule to be selected on navigation drawer
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            navigationView.setCheckedItem(0);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        // Close drawer when back is pressed
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        // Set the nav_shuffle button on the menu
        menu.findItem(R.id.nav_shuffle).setVisible(menu_shuffle);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Navigation drawer menu item click functions
        int id = item.getItemId();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);

        if (id == R.id.nav_schedule) {
            // Schedule
            ScheduleFragment scheduleFragment = new ScheduleFragment();
            getFragmentManager().beginTransaction().replace(
                    R.id.content_main,
                    scheduleFragment,
                    scheduleFragment.getTag()
            ).commit();
            toolbar.setTitle(R.string.menu_schedule);
        } else if (id == R.id.nav_tasks) {
            // Tasks
            TaskFragment taskFragment = new TaskFragment();
            getFragmentManager().beginTransaction().replace(
                    R.id.content_main,
                    taskFragment,
                    taskFragment.getTag()
            ).commit();
            toolbar.setTitle(R.string.menu_tasks);
        } else if (id == R.id.nav_voidblocks) {
            // Voidblocks
            VoidblockFragment voidblockFragment = new VoidblockFragment();
            getFragmentManager().beginTransaction().replace(
                    R.id.content_main,
                    voidblockFragment,
                    voidblockFragment.getTag()
            ).commit();
            toolbar.setTitle(R.string.menu_voidblocks);
        } else if (id == R.id.nav_settings) {
            // Settings
            SettingFragment settingFragment= new SettingFragment();
            getFragmentManager().beginTransaction().replace(
                    R.id.content_main,
                    settingFragment,
                    settingFragment.getTag()
            ).commit();
            toolbar.setTitle(R.string.menu_settings);
        }

        // Close the drawer once done
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
