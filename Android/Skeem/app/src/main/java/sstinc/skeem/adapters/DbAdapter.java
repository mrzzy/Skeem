package sstinc.skeem.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.joda.time.format.PeriodFormat;

import java.util.ArrayList;
import java.util.Collections;

import sstinc.skeem.models.Datetime;
import sstinc.skeem.models.Task;
import sstinc.skeem.utils.TaskComparator;
import sstinc.skeem.models.Voidblock;
import sstinc.skeem.utils.VoidblockComparator;
import sstinc.skeem.models.WeekDays;

/**
 * This class handles the database CRUD, create, read, update and delete
 * operations. There are four tables, Task, Deadline, Days and Voidblock.
 *
 * <p>Task Table:</p>
 * <ul>
 *     <li>id (int)</li>
 *     <li>days_id (int)</li>
 *     <li>name (String)</li>
 *     <li>subject (String)</li>
 *     <li>description (String)</li>
 *     <li>period_needed (String)</li>
 *     <li>period_minimum (String)</li>
 *     <li>deadline (String)</li>
 *     <li>days_id references Days(id)</li>
 * </ul>
 *
 * <p>Days Table:</p>
 * <ul>
 *     <li>id (int)</li>
 *     <li>monday (int)</li>
 *     <li>tuesday (int)</li>
 *     <li>wednesday (int)</li>
 *     <li>thursday (int)</li>
 *     <li>friday (int)</li>
 *     <li>saturday (int)</li>
 *     <li>sunday (int)</li>
 * </ul>
 *
 * <p>Voidblock Table:</p>
 * <ul>
 *     <li>id (int)</li>
 *     <li>days_id (int)</li>
 *     <li>name (String)</li>
 *     <li>scheduled_start (String)</li>
 *     <li>scheduled_stop (String)</li>
 *     <li>days_id references Days(id)</li>
 * </ul>
 */
@SuppressWarnings("unused")
public class DbAdapter {
    // Define constants
    // Constants for database
    private static final String DATABASE_NAME = "skeem.db";
    private static final int DATABASE_VERSION = 2;

    // Constants for Days Table
    private static final String DAYS_TABLE = "days";
    private static final String DAYS_TABLE_COL_ID = "id";
    private static final String DAYS_TABLE_COL_MONDAY = "monday";
    private static final String DAYS_TABLE_COL_TUESDAY = "tuesday";
    private static final String DAYS_TABLE_COL_WEDNESDAY = "wednesday";
    private static final String DAYS_TABLE_COL_THURSDAY = "thursday";
    private static final String DAYS_TABLE_COL_FRIDAY = "friday";
    private static final String DAYS_TABLE_COL_SATURDAY = "saturday";
    private static final String DAYS_TABLE_COL_SUNDAY = "sunday";
    // Days Table column list
    private String[] DAYS_TABLE_COLUMNS = {
            DAYS_TABLE_COL_ID, DAYS_TABLE_COL_MONDAY, DAYS_TABLE_COL_TUESDAY,
            DAYS_TABLE_COL_WEDNESDAY, DAYS_TABLE_COL_THURSDAY, DAYS_TABLE_COL_FRIDAY,
            DAYS_TABLE_COL_SATURDAY, DAYS_TABLE_COL_SUNDAY
    };
    // Command to create table
    private static final String DAYS_TABLE_CREATE = "CREATE TABLE " + DAYS_TABLE + "("
            + DAYS_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DAYS_TABLE_COL_MONDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_TUESDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_WEDNESDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_THURSDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_FRIDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_SATURDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_SUNDAY + " INTEGER NOT NULL"
            + ");";

    // Constants for Tasks Table
    private static final String TASKS_TABLE = "tasks";
    private static final String TASKS_TABLE_COL_ID = "id";
    private static final String TASKS_TABLE_COL_DAYS_ID = "days_id";
    private static final String TASKS_TABLE_COL_NAME =  "name";
    private static final String TASKS_TABLE_COL_SUBJECT = "subject";
    private static final String TASKS_TABLE_COL_DESCRIPTION = "description";
    private static final String TASKS_TABLE_COL_PERIOD_NEEDED = "period_needed";
    private static final String TASKS_TABLE_COL_PERIOD_MINIMUM = "period_minimum";
    private static final String TASKS_TABLE_COL_DEADLINE = "deadline";
    private static final String TASKS_TABLE_COL_DEADLINE_PER_DAY = "deadline_per_day";
    // Task Table column list
    private String[] TASKS_TABLE_COLUMNS = {
            TASKS_TABLE_COL_ID, TASKS_TABLE_COL_DAYS_ID, TASKS_TABLE_COL_NAME,
            TASKS_TABLE_COL_SUBJECT, TASKS_TABLE_COL_DESCRIPTION, TASKS_TABLE_COL_PERIOD_NEEDED,
            TASKS_TABLE_COL_PERIOD_MINIMUM, TASKS_TABLE_COL_DEADLINE,
            TASKS_TABLE_COL_DEADLINE_PER_DAY
    };
    // Command to create table
    private static final String TASKS_TABLE_CREATE = "CREATE TABLE " + TASKS_TABLE  + "("
            + TASKS_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASKS_TABLE_COL_DAYS_ID + " INTEGER, "
            + TASKS_TABLE_COL_NAME + " TEXT NOT NULL, "
            + TASKS_TABLE_COL_SUBJECT + " TEXT NOT NULL, "
            + TASKS_TABLE_COL_DESCRIPTION + " TEXT NOT NULL, "
            + TASKS_TABLE_COL_PERIOD_NEEDED + " TEXT NOT NULL, "
            + TASKS_TABLE_COL_PERIOD_MINIMUM + " TEXT, "
            + TASKS_TABLE_COL_DEADLINE + " TEXT NOT NULL, "
            + TASKS_TABLE_COL_DEADLINE_PER_DAY + " TEXT NOT NULL, "
            + "FOREIGN KEY(" + TASKS_TABLE_COL_DAYS_ID + ") REFERENCES " + DAYS_TABLE + "("
            + DAYS_TABLE_COL_ID + "), "
            + "CONSTRAINT VALID_DURATION CHECK(" + TASKS_TABLE_COL_PERIOD_NEEDED + " > 0)"
            + ");";

    // Constants for Voidblocks Table
    private static final String VOIDBLOCKS_TABLE = "voidblocks";
    private static final String VOIDBLOCKS_TABLE_COL_ID = "id";
    private static final String VOIDBLOCKS_TABLE_COL_DAYS_ID = "days_id";
    private static final String VOIDBLOCKS_TABLE_COL_NAME = "name";
    private static final String VOIDBLOCKS_TABLE_COL_SCHEDULED_START = "scheduled_start";
    private static final String VOIDBLOCKS_TABLE_COL_SCHEDULED_STOP = "scheduled_stop";
    // Voidblocks Table column list
    private String[] VOIDBLOCKS_TABLE_COLUMNS = {
            VOIDBLOCKS_TABLE_COL_ID, VOIDBLOCKS_TABLE_COL_DAYS_ID, VOIDBLOCKS_TABLE_COL_NAME,
            VOIDBLOCKS_TABLE_COL_SCHEDULED_START, VOIDBLOCKS_TABLE_COL_SCHEDULED_STOP
    };
    // Command to create table
    private static final String VOIDBLOCKS_TABLE_CREATE = "CREATE TABLE " + VOIDBLOCKS_TABLE + "("
            + VOIDBLOCKS_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VOIDBLOCKS_TABLE_COL_DAYS_ID + " INTEGER, "
            + VOIDBLOCKS_TABLE_COL_NAME + " TEXT NOT NULL, "
            + VOIDBLOCKS_TABLE_COL_SCHEDULED_START + " TEXT NOT NULL, "
            + VOIDBLOCKS_TABLE_COL_SCHEDULED_STOP + " TEXT NOT NULL, "
            + "FOREIGN KEY(" + VOIDBLOCKS_TABLE_COL_DAYS_ID + ") REFERENCES " + DAYS_TABLE + "("
            + DAYS_TABLE_COL_ID + ")"
            + ");";

    private SQLiteDatabase SQLdb;
    private DbHelper dbHelper;
    private Context context;

    //Constructor

    /**
     * Construct a DbAdapter with Context
     * @param ctx Context to construct with
     */
    public DbAdapter(Context ctx) {
        context = ctx;
    }

    /**
     * Opens the database.
     * @return Returns <code>this</code> DbAdapter Object
     * @throws android.database.SQLException
     */
    public DbAdapter open() throws android.database.SQLException {
        dbHelper = new DbHelper(context);
        SQLdb = dbHelper.getWritableDatabase();
        return this;
    }

    /**
     * Closes the database.
     */
    public void close() {
        dbHelper.close();
    }

    // Create
    /**
     * Add a new task to the database.
     * @param task task to add
     */
    public void insertTask(Task task) {
        // Days Table
        ContentValues daysTableValues = new ContentValues();
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 0);

        for (WeekDays.WeekDay weekDay : task.getWeekDays().getWeekDays_list()) {
            switch (weekDay) {
                case MONDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 1);
                    break;
                case TUESDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 1);
                    break;
                case WEDNESDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 1);
                    break;
                case THURSDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 1);
                    break;
                case FRIDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 1);
                    break;
                case SATURDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 1);
                    break;
                case SUNDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 1);
                    break;
            }
        }
        long days_id = SQLdb.insert(DbAdapter.DAYS_TABLE, null, daysTableValues);

        // Tasks Table
        ContentValues taskTableValues = new ContentValues();
        taskTableValues.put(DbAdapter.TASKS_TABLE_COL_DAYS_ID, days_id);
        taskTableValues.put(DbAdapter.TASKS_TABLE_COL_NAME, task.getName());
        taskTableValues.put(DbAdapter.TASKS_TABLE_COL_SUBJECT, task.getSubject());
        taskTableValues.put(DbAdapter.TASKS_TABLE_COL_DESCRIPTION, task.getDescription());
        taskTableValues.put(DbAdapter.TASKS_TABLE_COL_PERIOD_NEEDED,
                PeriodFormat.getDefault().print(task.getPeriodNeeded()));
        taskTableValues.put(DbAdapter.TASKS_TABLE_COL_DEADLINE, task.getDeadline().toString());
        taskTableValues.put(DbAdapter.TASKS_TABLE_COL_DEADLINE_PER_DAY, task.getDeadlinePerDay().toString());
        // Insert into database and set the task's id.
        task.setId(SQLdb.insert(DbAdapter.TASKS_TABLE, null, taskTableValues));
    }

    /**
     * Adds a new voidblock into the database.
     * @param voidblock voidblock to add
     */
    public void insertVoidblock(Voidblock voidblock) {
        // Days Table
        ContentValues daysTableValues = new ContentValues();
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 0);

        for (WeekDays.WeekDay weekDay : voidblock.getWeekDays().getWeekDays_list()) {
            switch (weekDay) {
                case MONDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 1);
                    break;
                case TUESDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 1);
                    break;
                case WEDNESDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 1);
                    break;
                case THURSDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 1);
                    break;
                case FRIDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 1);
                    break;
                case SATURDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 1);
                    break;
                case SUNDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 1);
                    break;
            }
        }

        long days_id = SQLdb.insert(DbAdapter.DAYS_TABLE, null, daysTableValues);

        ContentValues taskTableValues = new ContentValues();
        taskTableValues.put(DbAdapter.VOIDBLOCKS_TABLE_COL_DAYS_ID, days_id);
        taskTableValues.put(DbAdapter.VOIDBLOCKS_TABLE_COL_NAME, voidblock.getName());
        taskTableValues.put(DbAdapter.VOIDBLOCKS_TABLE_COL_SCHEDULED_START,
                voidblock.getScheduledStart().toString());
        taskTableValues.put(DbAdapter.VOIDBLOCKS_TABLE_COL_SCHEDULED_STOP,
                voidblock.getScheduledStop().toString());
        // Insert into database and set the voidblock's id
        voidblock.setId(SQLdb.insert(DbAdapter.VOIDBLOCKS_TABLE, null, taskTableValues));
    }

    // Read
    /**
     * Private function that to get the respective days value with it's id.
     *
     * @param id id of the days value
     * @return weekdays instance
     */
    private WeekDays getDays(long id) {
        // Query the days table
        Cursor cursor = SQLdb.query(DAYS_TABLE, DAYS_TABLE_COLUMNS,
                DAYS_TABLE_COL_ID + " = " + id, null, null, null, null);

        // Create WeekDays instance
        WeekDays weekDays = new WeekDays();
        // ArrayList to index from to get the values
        WeekDays.WeekDay[] weekDayIndex = WeekDays.WeekDay.values();

        cursor.moveToFirst();
        for (int i=1; i<8; i++) {
            if (cursor.getInt(i) == 1) {
                weekDays.add(weekDayIndex[i-1]);
            }
        }
        cursor.close();
        return weekDays;
    }

    /**
     * Gets the task based on its id.
     * @param taskId task's id
     * @return task queried from database
     */
    public Task getTask(long taskId) {
        // Query task table with task id
        Cursor cursor = SQLdb.query(TASKS_TABLE, TASKS_TABLE_COLUMNS,
                TASKS_TABLE_COL_ID + " = " + taskId , null, null, null, null);
        cursor.moveToFirst();

        // Create task instance
        Task task = new Task();

        long days_id = cursor.getLong(1);
        task.setName(cursor.getString(2));
        task.setSubject(cursor.getString(3));
        task.setDescription(cursor.getString(4));

        task.setPeriodNeeded(PeriodFormat.getDefault().parsePeriod(cursor.getString(5)));
        task.setDeadline(new Datetime(cursor.getString(7)));

        cursor.close();

        task.setId(taskId);
        task.setWeekDays(getDays(days_id));

        return task;
    }

    /**
     * Gets all the tasks in the database.
     * @return array list of all the tasks in the database
     */
    public ArrayList<Task> getTasks() {
        // Prepare array list
        ArrayList<Task> tasks = new ArrayList<>();

        // Query database for all tasks
        Cursor cursor = SQLdb.query(TASKS_TABLE, TASKS_TABLE_COLUMNS,
                null, null, null, null, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            Task task = new Task();

            task.setId(cursor.getLong(0));
            long days_id = cursor.getLong(1);
            task.setWeekDays(getDays(days_id));

            task.setName(cursor.getString(2));
            task.setSubject(cursor.getString(3));
            task.setDescription(cursor.getString(4));

            task.setPeriodNeeded(PeriodFormat.getDefault().parsePeriod(cursor.getString(5)));
            task.setDeadline(new Datetime(cursor.getString(7)));
            task.setDeadlinePerDay(new Datetime(cursor.getString(8)));

            tasks.add(task);
        }
        cursor.close();

        // Sort the tasks
        TaskComparator taskComparator = new TaskComparator();
        taskComparator.setSortBy(TaskComparator.Order.DEADLINE, true);
        Collections.sort(tasks, taskComparator);
        return tasks;
    }

    /**
     * Gets the voidblock based on its id.
     * @param voidblockId voidblock's id
     * @return voidblock queried from database
     */
    public Voidblock getVoidblock(long voidblockId) {
        // Query database
        Cursor cursor = SQLdb.query(VOIDBLOCKS_TABLE, VOIDBLOCKS_TABLE_COLUMNS,
                VOIDBLOCKS_TABLE_COL_ID + " = " + voidblockId, null, null, null, null);
        cursor.moveToFirst();

        Voidblock voidblock = new Voidblock();

        long days_id = cursor.getLong(1);

        voidblock.setName(cursor.getString(2));
        voidblock.setScheduledStart(new Datetime(cursor.getString(3)));
        voidblock.setScheduledStop(new Datetime(cursor.getString(4)));

        cursor.close();

        voidblock.setId(voidblockId);
        voidblock.setWeekDays(getDays(days_id));

        return voidblock;
    }

    /**
     * Gets all the voidblocks in the database.
     * @return array list of all the voidblocks in the database
     */
    public ArrayList<Voidblock> getVoidblocks() {
        // Prepare array list
        ArrayList<Voidblock> voidblocks = new ArrayList<>();

        // Query database
        Cursor cursor = SQLdb.query(VOIDBLOCKS_TABLE, VOIDBLOCKS_TABLE_COLUMNS,
                null, null, null, null, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            Voidblock voidblock = new Voidblock();

            voidblock.setId(cursor.getLong(0));
            voidblock.setWeekDays(getDays(cursor.getLong(1)));
            voidblock.setName(cursor.getString(2));
            voidblock.setScheduledStart(new Datetime(cursor.getString(3)));
            voidblock.setScheduledStop(new Datetime(cursor.getString(4)));

            voidblocks.add(voidblock);
        }
        cursor.close();

        // Sort the voidblocks, most recent first
        VoidblockComparator voidBlockComparator = new VoidblockComparator();
        voidBlockComparator.setSortBy(VoidblockComparator.Order.SCHEDULED_START, true);
        Collections.sort(voidblocks, voidBlockComparator);

        return voidblocks;
    }

    // Update
    /**
     * Updates the task's values completely. Does not check for changes or
     * empty values.
     * @param task new values to update
     */
    public void updateTask(Task task) {
        long taskId = task.getId();
        ContentValues taskTableValues = new ContentValues();

        // Tasks Table
        taskTableValues.put(TASKS_TABLE_COL_NAME, task.getName());
        taskTableValues.put(TASKS_TABLE_COL_SUBJECT, task.getSubject());
        taskTableValues.put(TASKS_TABLE_COL_DESCRIPTION, task.getDescription());
        taskTableValues.put(TASKS_TABLE_COL_PERIOD_NEEDED,
                PeriodFormat.getDefault().print(task.getPeriodNeeded()));
        taskTableValues.put(TASKS_TABLE_COL_DEADLINE, task.getDeadline().toString());
        taskTableValues.put(TASKS_TABLE_COL_DEADLINE_PER_DAY, task.getDeadlinePerDay().toString());

        SQLdb.update(TASKS_TABLE, taskTableValues, TASKS_TABLE_COL_ID + "=" + taskId, null);
        // Query task table with task id
        Cursor cursor = SQLdb.query(TASKS_TABLE, new String[] {TASKS_TABLE_COL_DAYS_ID},
                TASKS_TABLE_COL_ID + " = " + taskId, null, null, null, null);
        cursor.moveToFirst();

        // Get the days id
        long days_id = cursor.getLong(0);
        cursor.close();

        // Days Table
        ContentValues daysTableValues = new ContentValues();
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 0);

        for (WeekDays.WeekDay weekDay : task.getWeekDays().getWeekDays_list()) {
            switch (weekDay) {
                case MONDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 1);
                    break;
                case TUESDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 1);
                    break;
                case WEDNESDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 1);
                    break;
                case THURSDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 1);
                    break;
                case FRIDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 1);
                    break;
                case SATURDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 1);
                    break;
                case SUNDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 1);
                    break;
            }
        }
        SQLdb.update(DbAdapter.DAYS_TABLE, daysTableValues, DAYS_TABLE_COL_ID + "=" + days_id, null);
    }

    /**
     * Updates the voidblock's values completely. Does not check for changes or
     * empty values.
     * @param voidblock new values to update
     */
    public void updateVoidblock(Voidblock voidblock) {
        long voidblockId = voidblock.getId();
        ContentValues voidBlockTableValues = new ContentValues();
        voidBlockTableValues.put(DbAdapter.VOIDBLOCKS_TABLE_COL_NAME, voidblock.getName());
        voidBlockTableValues.put(DbAdapter.VOIDBLOCKS_TABLE_COL_SCHEDULED_START,
                voidblock.getScheduledStart().toString());
        voidBlockTableValues.put(DbAdapter.VOIDBLOCKS_TABLE_COL_SCHEDULED_STOP,
                voidblock.getScheduledStop().toString());
        // Update voidblocks table
        SQLdb.update(VOIDBLOCKS_TABLE, voidBlockTableValues, VOIDBLOCKS_TABLE_COL_ID + "=" + voidblockId, null);
        // Query voidblock table with voidblock id
        Cursor cursor = SQLdb.query(VOIDBLOCKS_TABLE, new String[] {VOIDBLOCKS_TABLE_COL_DAYS_ID},
                TASKS_TABLE_COL_ID + " = " + voidblockId, null, null, null, null);
        cursor.moveToFirst();

        // Get the days id
        long days_id = cursor.getLong(0);
        cursor.close();
        // Days Table
        ContentValues daysTableValues = new ContentValues();
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 0);
        daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 0);

        for (WeekDays.WeekDay weekDay : voidblock.getWeekDays().getWeekDays_list()) {
            switch (weekDay) {
                case MONDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 1);
                    break;
                case TUESDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 1);
                    break;
                case WEDNESDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 1);
                    break;
                case THURSDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 1);
                    break;
                case FRIDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 1);
                    break;
                case SATURDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 1);
                    break;
                case SUNDAY:
                    daysTableValues.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 1);
                    break;
            }
        }
        // Update days table with voidblock's daysTableValues
        SQLdb.update(DbAdapter.DAYS_TABLE, daysTableValues, DAYS_TABLE_COL_ID + "=" + days_id, null);
    }

    // Delete
    /**
     * Deletes a task based on it's id.
     * @param taskId id of task to delete
     */
    public void deleteTask(long taskId) {
        // Query task table with task id
        Cursor cursor = SQLdb.query(TASKS_TABLE, new String[] {TASKS_TABLE_COL_DAYS_ID},
                TASKS_TABLE_COL_ID + " = " + taskId, null, null, null, null);
        cursor.moveToFirst();

        // Get the days id
        long days_id = cursor.getLong(0);
        cursor.close();
        // Delete foreign keys first
        SQLdb.delete(DAYS_TABLE, DAYS_TABLE_COL_ID + "=" + days_id, null);
        // Delete from Task Table
        SQLdb.delete(TASKS_TABLE, TASKS_TABLE_COL_ID + "=" + taskId, null);
    }

    /**
     * Delets a voidblock based on it's id.
     * @param voidblockId id of voidblock to delete
     */
    public void deleteVoidblock(long voidblockId) {
        // Query task table with task id
        Cursor cursor = SQLdb.query(VOIDBLOCKS_TABLE, new String[] {VOIDBLOCKS_TABLE_COL_DAYS_ID},
                VOIDBLOCKS_TABLE_COL_ID + " = " + voidblockId, null, null, null, null);
        cursor.moveToFirst();

        // Get the days id
        long days_id = cursor.getLong(0);
        cursor.close();
        // Delete foreign key first
        SQLdb.delete(DAYS_TABLE, DAYS_TABLE_COL_ID + "=" + days_id, null);
        // Delete from Voidblock Table
        SQLdb.delete(VOIDBLOCKS_TABLE, VOIDBLOCKS_TABLE_COL_ID + "=" + voidblockId, null);
    }

    /**
     * Deletes all values by dropping all tables and recreating them again.
     */
    public void deleteAll() {
        SQLdb.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
        SQLdb.execSQL("DROP TABLE IF EXISTS " + DAYS_TABLE);
        SQLdb.execSQL("DROP TABLE IF EXISTS " + VOIDBLOCKS_TABLE);
        dbHelper.onCreate(SQLdb);
    }

    // Database Helper
    public static class DbHelper extends SQLiteOpenHelper {
        public DbHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TASKS_TABLE_CREATE);
            db.execSQL(DAYS_TABLE_CREATE);
            db.execSQL(VOIDBLOCKS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DbHelper.class.getName(),
                    "Updating database version from version " + oldVersion +
                    " to version " + newVersion + ". This will destroy all data.");
            db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DAYS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + VOIDBLOCKS_TABLE);
            onCreate(db);
        }

        public ArrayList<Cursor> getData(String Query){
            //get writable database
            SQLiteDatabase sqlDB = this.getWritableDatabase();
            String[] columns = new String[] { "message" };
            //an array list of cursor to save two cursors one has results from the query
            //other cursor stores error message if any errors are triggered
            ArrayList<Cursor> cursorList = new ArrayList<>(2);
            MatrixCursor errorCursor = new MatrixCursor(columns);
            cursorList.add(null);
            cursorList.add(null);


            try{
                //execute the query results will be save in Cursor c
                Cursor resultCursor = sqlDB.rawQuery(Query, null);

                //add value to error Cursor
                errorCursor.addRow(new Object[] {
                        "Success"
                });

                cursorList.set(1,errorCursor);
                if (resultCursor != null && resultCursor.getCount() > 0) {

                    cursorList.set(0,resultCursor);
                    resultCursor.moveToFirst();

                    return cursorList;
                }

                return cursorList;
            } catch(SQLException sqlEx){
                Log.d("printing exception", sqlEx.getMessage());
                //if any exceptions are triggered save the error message to cursor an return the arraylist
                errorCursor.addRow(new Object[] { ""+sqlEx.getMessage() });
                cursorList.set(1,errorCursor);
                return cursorList;
            } catch(Exception ex){

                Log.d("printing exception", ex.getMessage());

                //if any exceptions are triggered save the error message to cursor an return the arraylist
                errorCursor.addRow(new Object[] { ""+ex.getMessage() });
                cursorList.set(1,errorCursor);
                return cursorList;
            }
        }
    }
}
