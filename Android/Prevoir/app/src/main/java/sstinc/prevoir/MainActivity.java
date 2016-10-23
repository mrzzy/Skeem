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

//TODO: Create copy constructors
//TODO: Check for assumed copy
//TODO: Move menu status to static class
//TODO: Expired tasks and voidblocks etc.
//TODO: Date addition is wrong

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static boolean menu_shuffle = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        toolbar.setTitle(R.string.menu_schedule);
        setSupportActionBar(toolbar);

        // Navigation Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        // Set Navigation drawer
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Default fragment to schedule fragment
        if (savedInstanceState == null) {
            onNavigationItemSelected(navigationView.getMenu().getItem(0));
            navigationView.setCheckedItem(0);
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
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
        menu.findItem(R.id.nav_shuffle).setVisible(menu_shuffle);
        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_main);

        if (id == R.id.nav_schedule) {
            ScheduleFragment scheduleFragment = new ScheduleFragment();
            getFragmentManager().beginTransaction().replace(
                    R.id.content_main,
                    scheduleFragment,
                    scheduleFragment.getTag()
            ).commit();
            toolbar.setTitle(R.string.menu_schedule);
        } else if (id == R.id.nav_tasks) {
            TaskFragment taskFragment = new TaskFragment();
            getFragmentManager().beginTransaction().replace(
                    R.id.content_main,
                    taskFragment,
                    taskFragment.getTag()
            ).commit();
            toolbar.setTitle(R.string.menu_tasks);
        } else if (id == R.id.nav_voidblocks) {
            VoidblockFragment voidblockFragment = new VoidblockFragment();
            getFragmentManager().beginTransaction().replace(
                    R.id.content_main,
                    voidblockFragment,
                    voidblockFragment.getTag()
            ).commit();
            toolbar.setTitle(R.string.menu_voidblocks);
        } else if (id == R.id.nav_settings) {
            SettingFragment settingFragment= new SettingFragment();
            getFragmentManager().beginTransaction().replace(
                    R.id.content_main,
                    settingFragment,
                    settingFragment.getTag()
            ).commit();
            toolbar.setTitle(R.string.menu_settings);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
