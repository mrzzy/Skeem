package sstinc.prevoir;

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
//TODO: Add task deadline per day
class DbAdapter {
    // Define constants
    // Constants for database
    private static final String DATABASE_NAME = "prevoir.db";
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

    // Database Methods
    DbAdapter(Context ctx) {
        context = ctx;
    }

    // Opens the database
    DbAdapter open() throws android.database.SQLException {
        dbHelper = new DbHelper(context);
        SQLdb = dbHelper.getWritableDatabase();
        return this;
    }

    // Closes the database
    void close() {
        dbHelper.close();
    }

    // Create
    /**
     * Add a new task to the database.
     * @param task task to add
     */
    void insertTask(Task task) {
        ContentValues values = new ContentValues();

        // Days Table
        values = new ContentValues();
        values.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 0);

        for (WeekDays.WeekDay weekDay : task.getWeekDays().getWeekDays_list()) {
            switch (weekDay) {
                case MONDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 1);
                case TUESDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 1);
                case WEDNESDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 1);
                case THURSDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 1);
                case FRIDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 1);
                case SATURDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 1);
                case SUNDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 1);
            }
        }
        long days_id = SQLdb.insert(DbAdapter.DAYS_TABLE, null, values);

        // Tasks Table
        values = new ContentValues();
        values.put(DbAdapter.TASKS_TABLE_COL_DAYS_ID, days_id);
        values.put(DbAdapter.TASKS_TABLE_COL_NAME, task.getName());
        values.put(DbAdapter.TASKS_TABLE_COL_SUBJECT, task.getSubject());
        values.put(DbAdapter.TASKS_TABLE_COL_DESCRIPTION, task.getDescription());
        values.put(DbAdapter.TASKS_TABLE_COL_PERIOD_NEEDED,
                PeriodFormat.getDefault().print(task.getPeriodNeeded()));
        values.put(DbAdapter.TASKS_TABLE_COL_PERIOD_MINIMUM,
                PeriodFormat.getDefault().print(task.getPeriodMinimum()));
        values.put(DbAdapter.TASKS_TABLE_COL_DEADLINE, task.getDeadline().toString());
        values.put(DbAdapter.TASKS_TABLE_COL_DEADLINE_PER_DAY, task.getDeadlinePerDay().toString());
        // Insert into database and set the task's id.
        task.setId(SQLdb.insert(DbAdapter.TASKS_TABLE, null, values));
    }

    /**
     * Adds a new voidblock into the database.
     * @param voidblock voidblock to add
     */
    void insertVoidblock(Voidblock voidblock) {
        ContentValues values = new ContentValues();

        // Days Table
        values = new ContentValues();
        values.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 0);

        for (WeekDays.WeekDay weekDay : voidblock.getWeekDays().getWeekDays_list()) {
            switch (weekDay) {
                case MONDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 1);
                case TUESDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 1);
                case WEDNESDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 1);
                case THURSDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 1);
                case FRIDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 1);
                case SATURDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 1);
                case SUNDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 1);
            }
        }
        long days_id = SQLdb.insert(DbAdapter.DAYS_TABLE, null, values);

        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_DAYS_ID, days_id);
        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_NAME, voidblock.getName());
        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_SCHEDULED_START,
                voidblock.getScheduledStart().toString());
        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_SCHEDULED_STOP,
                voidblock.getScheduledStop().toString());
        // Insert into database and set the voidblock's id
        voidblock.setId(SQLdb.insert(DbAdapter.VOIDBLOCKS_TABLE, null, values));
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
            // FIXME: 30/10/16 getString to getInt
            if (cursor.getString(i).equals("1")) {
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
    Task getTask(long taskId) {
        // Query task table with task id
        Cursor cursor = SQLdb.query(TASKS_TABLE, TASKS_TABLE_COLUMNS,
                TASKS_TABLE_COL_ID + " = " + taskId , null, null, null, null);
        cursor.moveToFirst();

        // Create task instance
        Task task = new Task();

        // FIXME: 31/10/16 getString to getLong
        long days_id = Long.parseLong(cursor.getString(1));
        task.setName(cursor.getString(2));
        task.setSubject(cursor.getString(3));
        task.setDescription(cursor.getString(4));

        task.setPeriodNeeded(PeriodFormat.getDefault().parsePeriod(cursor.getString(5)));
        task.setPeriodMinimum(PeriodFormat.getDefault().parsePeriod(cursor.getString(6)));
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
    ArrayList<Task> getTasks() {
        // Prepare array list
        ArrayList<Task> tasks = new ArrayList<>();

        // Query database for all tasks
        Cursor cursor = SQLdb.query(TASKS_TABLE, TASKS_TABLE_COLUMNS,
                null, null, null, null, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            Task task = new Task();
            // FIXME: 30/10/16 getString to getLong
            task.setId(Long.parseLong(cursor.getString(0)));
            // FIXME: 31/10/16 getString to getLong
            long days_id = Long.parseLong(cursor.getString(1));
            task.setWeekDays(getDays(days_id));

            task.setName(cursor.getString(2));
            task.setSubject(cursor.getString(3));
            task.setDescription(cursor.getString(4));

            task.setPeriodNeeded(PeriodFormat.getDefault().parsePeriod(cursor.getString(5)));
            task.setPeriodMinimum(PeriodFormat.getDefault().parsePeriod(cursor.getString(6)));
            task.setDeadline(new Datetime(cursor.getString(7)));
            task.setDeadlinePerDay(new Datetime(cursor.getString(8)));

            tasks.add(task);
        }
        cursor.close();

        // Sort the tasks
        TaskComparator taskComparator = new TaskComparator();
        taskComparator.setSortBy(TaskComparator.Order.DEADLINE, false);
        Collections.sort(tasks, taskComparator);
        return tasks;
    }

    /**
     * Gets the voidblock based on its id.
     * @param voidblockId voidblock's id
     * @return voidblock queried from database
     */
    Voidblock getVoidblock(long voidblockId) {
        // Query database
        Cursor cursor = SQLdb.query(VOIDBLOCKS_TABLE, VOIDBLOCKS_TABLE_COLUMNS,
                VOIDBLOCKS_TABLE_COL_ID + " = " + voidblockId, null, null, null, null);
        cursor.moveToFirst();

        Voidblock voidblock = new Voidblock();

        // FIXME: 31/10/16 getString to getLong
        long days_id = Long.parseLong(cursor.getString(1));

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
    ArrayList<Voidblock> getVoidblocks() {
        // Prepare array list
        ArrayList<Voidblock> voidblocks = new ArrayList<>();

        // Query database
        Cursor cursor = SQLdb.query(VOIDBLOCKS_TABLE, VOIDBLOCKS_TABLE_COLUMNS,
                null, null, null, null, null);

        for (cursor.moveToLast(); !cursor.isBeforeFirst(); cursor.moveToPrevious()) {
            Voidblock voidblock = new Voidblock();
            // FIXME: 31/10/16 getString to getLong
            voidblock.setId(Long.parseLong(cursor.getString(0)));
            // FIXME: 31/10/16 getString to getLong
            voidblock.setWeekDays(getDays(Long.parseLong(cursor.getString(1))));
            voidblock.setName(cursor.getString(1));
            voidblock.setScheduledStart(new Datetime(cursor.getString(2)));
            voidblock.setScheduledStop(new Datetime(cursor.getString(3)));

            voidblocks.add(voidblock);
        }
        cursor.close();

        // Sort the voidblocks, latest first
        VoidblockComparator voidblockComparator = new VoidblockComparator();
        voidblockComparator.setSortBy(VoidblockComparator.Order.SCHEDULED_START, true);
        Collections.sort(voidblocks, voidblockComparator);

        return voidblocks;
    }

    // Update
    /**
     * Updates the task's values completely. Does not check for changes or
     * empty values.
     * @param task new values to update
     */
    void updateTask(Task task) {
        long taskId = task.getId();
        ContentValues values = new ContentValues();

        // Tasks Table
        values.put(TASKS_TABLE_COL_NAME, task.getName());
        values.put(TASKS_TABLE_COL_SUBJECT, task.getSubject());
        values.put(TASKS_TABLE_COL_DESCRIPTION, task.getDescription());
        values.put(TASKS_TABLE_COL_PERIOD_NEEDED,
                PeriodFormat.getDefault().print(task.getPeriodNeeded()));
        values.put(TASKS_TABLE_COL_PERIOD_MINIMUM,
                PeriodFormat.getDefault().print(task.getPeriodMinimum()));
        values.put(TASKS_TABLE_COL_DEADLINE, task.getDeadline().toString());
        values.put(TASKS_TABLE_COL_DEADLINE_PER_DAY, task.getDeadlinePerDay().toString());

        SQLdb.update(TASKS_TABLE, values, TASKS_TABLE_COL_ID + "=" + taskId, null);
        // Query task table with task id
        Cursor cursor = SQLdb.query(TASKS_TABLE, new String[] {TASKS_TABLE_COL_DAYS_ID},
                TASKS_TABLE_COL_ID + " = " + taskId, null, null, null, null);
        cursor.moveToFirst();

        // Get the days id
        // FIXME: 31/10/16 getString to getLong
        long days_id = Long.parseLong(cursor.getString(0));
        cursor.close();

        // Days Table
        values = new ContentValues();
        values.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 0);

        for (WeekDays.WeekDay weekDay : task.getWeekDays().getWeekDays_list()) {
            switch (weekDay) {
                case MONDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 1);
                case TUESDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 1);
                case WEDNESDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 1);
                case THURSDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 1);
                case FRIDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 1);
                case SATURDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 1);
                case SUNDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 1);
            }
        }
        SQLdb.update(DbAdapter.DAYS_TABLE, values, DAYS_TABLE_COL_ID + "=" + days_id, null);
    }

    /**
     * Updates the voidblock's values completely. Does not check for changes or
     * empty values.
     * @param voidblock new values to update
     */
    void updateVoidblock(Voidblock voidblock) {
        long voidblockId = voidblock.getId();
        ContentValues values = new ContentValues();
        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_NAME, voidblock.getName());
        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_SCHEDULED_START,
                voidblock.getScheduledStart().toString());
        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_SCHEDULED_STOP,
                voidblock.getScheduledStop().toString());
        // Update voidblocks table
        SQLdb.update(VOIDBLOCKS_TABLE, values, VOIDBLOCKS_TABLE_COL_ID + "=" + voidblockId, null);
        // Query voidblock table with voidblock id
        Cursor cursor = SQLdb.query(VOIDBLOCKS_TABLE, new String[] {VOIDBLOCKS_TABLE_COL_DAYS_ID},
                TASKS_TABLE_COL_ID + " = " + voidblockId, null, null, null, null);
        cursor.moveToFirst();

        // Get the days id
        // FIXME: 31/10/16 getString to getLong
        long days_id = Long.parseLong(cursor.getString(0));
        cursor.close();
        // Days Table
        values = new ContentValues();
        values.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 0);

        for (WeekDays.WeekDay weekDay : voidblock.getWeekDays().getWeekDays_list()) {
            switch (weekDay) {
                case MONDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 1);
                case TUESDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 1);
                case WEDNESDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 1);
                case THURSDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 1);
                case FRIDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 1);
                case SATURDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 1);
                case SUNDAY:
                    values.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 1);
            }
        }
        // Update days table with voidblock's values
        SQLdb.update(DbAdapter.DAYS_TABLE, values, DAYS_TABLE_COL_ID + "=" + days_id, null);
    }

    // Delete
    /**
     * Deletes a task based on it's id.
     * @param taskId id of task to delete
     */
    void deleteTask(long taskId) {
        // Query task table with task id
        Cursor cursor = SQLdb.query(TASKS_TABLE, new String[] {TASKS_TABLE_COL_DAYS_ID},
                TASKS_TABLE_COL_ID + " = " + taskId, null, null, null, null);
        cursor.moveToFirst();

        // Get the days id
        // FIXME: 31/10/16 getString to getLong
        long days_id = Long.parseLong(cursor.getString(0));
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
    void deleteVoidblock(long voidblockId) {
        // Query task table with task id
        Cursor cursor = SQLdb.query(TASKS_TABLE, new String[] {TASKS_TABLE_COL_DAYS_ID},
                TASKS_TABLE_COL_ID + " = " + voidblockId, null, null, null, null);
        cursor.moveToFirst();

        // Get the days id
        // FIXME: 31/10/16 getString to getLong
        long days_id = Long.parseLong(cursor.getString(0));
        cursor.close();
        // Delete foreign key first
        SQLdb.delete(DAYS_TABLE, DAYS_TABLE_COL_ID + "=" + days_id, null);
        // Delete from Voidblock Table
        SQLdb.delete(VOIDBLOCKS_TABLE, VOIDBLOCKS_TABLE_COL_ID + "=" + voidblockId, null);
    }

    /**
     * Deletes all values by dropping all tables and recreating them again.
     */
    void deleteAll() {
        SQLdb.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
        SQLdb.execSQL("DROP TABLE IF EXISTS " + DAYS_TABLE);
        SQLdb.execSQL("DROP TABLE IF EXISTS " + VOIDBLOCKS_TABLE);
        dbHelper.onCreate(SQLdb);
    }

    // Database Helper
    static class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context ctx) {
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
            String[] columns = new String[] { "mesage" };
            //an array list of cursor to save two cursors one has results from the query
            //other cursor stores error message if any errors are triggered
            ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
            MatrixCursor Cursor2= new MatrixCursor(columns);
            alc.add(null);
            alc.add(null);


            try{
                String maxQuery = Query ;
                //execute the query results will be save in Cursor c
                Cursor c = sqlDB.rawQuery(maxQuery, null);


                //add value to cursor2
                Cursor2.addRow(new Object[] { "Success" });

                alc.set(1,Cursor2);
                if (null != c && c.getCount() > 0) {


                    alc.set(0,c);
                    c.moveToFirst();

                    return alc ;
                }
                return alc;
            } catch(SQLException sqlEx){
                Log.d("printing exception", sqlEx.getMessage());
                //if any exceptions are triggered save the error message to cursor an return the arraylist
                Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
                alc.set(1,Cursor2);
                return alc;
            } catch(Exception ex){

                Log.d("printing exception", ex.getMessage());

                //if any exceptions are triggered save the error message to cursor an return the arraylist
                Cursor2.addRow(new Object[] { ""+ex.getMessage() });
                alc.set(1,Cursor2);
                return alc;
            }


        }
    }
}
