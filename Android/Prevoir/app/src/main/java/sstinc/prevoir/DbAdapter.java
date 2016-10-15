package sstinc.prevoir;

/*
Task:
    - ID (int)
    - Name (String)
    - Subject (String)
    - description (String)
    - duration (time)
    - minimum_time_period (time)

Deadline Table
    - TaskID (int)
    - deadline (date)
    - deadline (time)

Days
    - TaskID (int)
    - List of days (array list)

Void block
    - Name (String)
    - from (datetime)
    - to (datetime)
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DbAdapter {
    // Define constants
    // Constants for database
    private static final String DATABASE_NAME = "prevoir.db";
    private static final int DATABASE_VERSION = 1;

    // Constants for Tasks Table
    private static final String TASKS_TABLE = "tasks";
    private static final String TASKS_TABLE_COL_ID = "id";
    private static final String TASKS_TABLE_COL_NAME =  "name";
    private static final String TASKS_TABLE_COL_SUBJECT = "subject";
    private static final String TASKS_TABLE_COL_DESCRIPTION = "description";
    private static final String TASKS_TABLE_COL_DURATION = "duration";
    private static final String TASKS_TABLE_COL_MIN_TIME_PERIOD = "min_time_period";
    // Task Table column list
    private String[] TASKS_TABLE_COLUMNS = {
            TASKS_TABLE_COL_ID, TASKS_TABLE_COL_NAME, TASKS_TABLE_COL_SUBJECT,
            TASKS_TABLE_COL_DESCRIPTION, TASKS_TABLE_COL_DURATION, TASKS_TABLE_COL_MIN_TIME_PERIOD
    };
    // Command to create table
    private static final String TASKS_TABLE_CREATE = "CREATE TABLE " + TASKS_TABLE  + "("
            + TASKS_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + TASKS_TABLE_COL_NAME + " TEXT NOT NULL, "
            + TASKS_TABLE_COL_SUBJECT + " TEXT NOT NULL, "
            + TASKS_TABLE_COL_DESCRIPTION + " TEXT NOT NULL, "
            + TASKS_TABLE_COL_DURATION + " TEXT NOT NULL, "
            + TASKS_TABLE_COL_MIN_TIME_PERIOD + " TEXT, "
            + "CONSTRAINT VALID_DURATION CHECK(" + TASKS_TABLE_COL_DURATION + " > 0)"
            + ");";

    // Constants for Deadlines Table
    private static final String DEADLINES_TABLE = "deadlines";
    private static final String DEADLINES_TABLE_COL_TASK_ID = "task_id";
    private static final String DEADLINES_TABLE_COL_DEADLINE_DATETIME = "deadline_datetime";
    private static final String DEADLINES_TABLE_COL_DEADLINE_HAS_TIME = "deadline_has_time";
    // Deadlines Table column list
    private String[] DEADLINES_TABLE_COLUMNS = {
            DEADLINES_TABLE_COL_TASK_ID, DEADLINES_TABLE_COL_DEADLINE_DATETIME,
            DEADLINES_TABLE_COL_DEADLINE_HAS_TIME
    };
    // Command to create table
    private static final String DEADLINES_TABLE_CREATE = "CREATE TABLE " + DEADLINES_TABLE + "("
            + DEADLINES_TABLE_COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DEADLINES_TABLE_COL_DEADLINE_DATETIME + " TEXT NOT NULL, "
            + DEADLINES_TABLE_COL_DEADLINE_HAS_TIME + " INTEGER NOT NULL, "
            + "FOREIGN KEY(task_id) REFERENCES " +
                TASKS_TABLE + "(" + TASKS_TABLE_COL_ID + ")"
            + ");";

    // Constants for Days Table
    private static final String DAYS_TABLE = "days";
    private static final String DAYS_TABLE_COL_TASK_ID = "task_id";
    private static final String DAYS_TABLE_COL_MONDAY = "monday";
    private static final String DAYS_TABLE_COL_TUESDAY = "tuesday";
    private static final String DAYS_TABLE_COL_WEDNESDAY = "wednesday";
    private static final String DAYS_TABLE_COL_THURSDAY = "thursday";
    private static final String DAYS_TABLE_COL_FRIDAY = "friday";
    private static final String DAYS_TABLE_COL_SATURDAY = "saturday";
    private static final String DAYS_TABLE_COL_SUNDAY = "sunday";
    // Days Table column list
    private String[] DAYS_TABLE_COLUMNS = {
            DAYS_TABLE_COL_TASK_ID, DAYS_TABLE_COL_MONDAY, DAYS_TABLE_COL_TUESDAY,
            DAYS_TABLE_COL_WEDNESDAY, DAYS_TABLE_COL_THURSDAY, DAYS_TABLE_COL_FRIDAY,
            DAYS_TABLE_COL_SATURDAY, DAYS_TABLE_COL_SUNDAY
    };
    // Command to create table
    private static final String DAYS_TABLE_CREATE = "CREATE TABLE " + DAYS_TABLE + "("
            + DAYS_TABLE_COL_TASK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + DAYS_TABLE + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_TASK_ID + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_MONDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_TUESDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_WEDNESDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_THURSDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_FRIDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_SATURDAY + " INTEGER NOT NULL, "
            + DAYS_TABLE_COL_SUNDAY + " INTEGER NOT NULL, "
            + "FOREIGN KEY(" + DAYS_TABLE_COL_TASK_ID + ") REFERENCES " +
                TASKS_TABLE + "(" + TASKS_TABLE_COL_ID + ")"
            + ");";

    /*
    Void block
    - Name (String)
    - from (datetime)
    - to (datetime)
     */
    // Constants for Voidblocks Table
    private static final String VOIDBLOCKS_TABLE = "voidblocks";
    private static final String VOIDBLOCKS_TABLE_COL_ID = "id";
    private static final String VOIDBLOCKS_TABLE_COL_NAME = "name";
    private static final String VOIDBLOCKS_TABLE_COL_FROM_DATETIME = "from_datetime";
    private static final String VOIDBLOCKS_TABLE_COL_TO_DATETIME = "to_datetime";
    // Voidblocks Table column list
    private String[] VOIDBLOCKS_TABLE_COLUMNS = {
            VOIDBLOCKS_TABLE_COL_ID, VOIDBLOCKS_TABLE_COL_NAME,
            VOIDBLOCKS_TABLE_COL_FROM_DATETIME, VOIDBLOCKS_TABLE_COL_TO_DATETIME
    };
    // Command to create table
    private static final String VOIDBLOCKS_TABLE_CREATE = "CREATE TABLE " + VOIDBLOCKS_TABLE + "("
            + VOIDBLOCKS_TABLE_COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + VOIDBLOCKS_TABLE_COL_NAME + " TEXT NOT NULL, "
            + VOIDBLOCKS_TABLE_COL_FROM_DATETIME + " TEXT NOT NULL, "
            + VOIDBLOCKS_TABLE_COL_TO_DATETIME + " TEXT NOT NULL"
            + ");";

    private SQLiteDatabase SQLdb;
    private DbHelper dbHelper;
    private Context context;

    // Databse Methods
    DbAdapter(Context ctx) {
        context = ctx;
    }

    public DbAdapter open() throws android.database.SQLException {
        dbHelper = new DbHelper(context);
        SQLdb = dbHelper.getWritableDatabase();
        return this;
    }

    void close() {
        dbHelper.close();
    }

    // Create
    void insertTask(Task task) {
        ContentValues values = new ContentValues();

        // Tasks Table
        values.put(DbAdapter.TASKS_TABLE_COL_NAME, task.name);
        values.put(DbAdapter.TASKS_TABLE_COL_SUBJECT, task.subject);
        values.put(DbAdapter.TASKS_TABLE_COL_DESCRIPTION, task.description);
        values.put(DbAdapter.TASKS_TABLE_COL_DURATION, task.duration.toString());
        values.put(DbAdapter.TASKS_TABLE_COL_MIN_TIME_PERIOD, task.min_time_period.toString());
        task.setId(SQLdb.insert(DbAdapter.TASKS_TABLE, null, values));

        // Deadlines Table
        values = new ContentValues();
        values.put(DbAdapter.DEADLINES_TABLE_COL_TASK_ID, task.getId());

        values.put(DbAdapter.DEADLINES_TABLE_COL_DEADLINE_DATETIME,
                task.deadline.deadline.toString());
        values.put(DbAdapter.DEADLINES_TABLE_COL_DEADLINE_HAS_TIME, task.deadline.hasDueTime ? 1:0);
        SQLdb.insert(DbAdapter.DEADLINES_TABLE, null, values);

        // Days Table
        values = new ContentValues();
        values.put(DbAdapter.DAYS_TABLE_COL_TASK_ID, task.getId());
        values.put(DbAdapter.DAYS_TABLE_COL_MONDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_TUESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_WEDNESDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_THURSDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_FRIDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SATURDAY, 0);
        values.put(DbAdapter.DAYS_TABLE_COL_SUNDAY, 0);

        for (Task.WeekDay weekDay : task.weekDays) {
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
        SQLdb.insert(DbAdapter.DAYS_TABLE, null, values);
    }

    void insertVoidblock(Voidblock voidblock) {
        ContentValues values = new ContentValues();
        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_NAME, voidblock.name);
        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_FROM_DATETIME, voidblock.from.toString());
        values.put(DbAdapter.VOIDBLOCKS_TABLE_COL_TO_DATETIME, voidblock.to.toString());
        voidblock.setId(SQLdb.insert(DbAdapter.VOIDBLOCKS_TABLE, null, values));
    }

    // TODO: Read

    // TODO: Update

    // TODO: Delete

    // Database Helper
    private static class DbHelper extends SQLiteOpenHelper {
        DbHelper(Context ctx) {
            super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(TASKS_TABLE_CREATE);
            db.execSQL(DEADLINES_TABLE_CREATE);
            db.execSQL(DAYS_TABLE_CREATE);
            db.execSQL(VOIDBLOCKS_TABLE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(DbHelper.class.getName(),
                    "Updating database version from version " + oldVersion +
                    " to version " + newVersion + ". This will destroy all data.");
            db.execSQL("DROP TABLE IF EXISTS " + TASKS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DEADLINES_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DAYS_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + VOIDBLOCKS_TABLE);
            onCreate(db);
        }
    }
}
