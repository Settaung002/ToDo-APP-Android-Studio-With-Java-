package sg.edu.tmc.cos211.todo.database;

import android.provider.BaseColumns;

/**
 * Contract class that defines the SQLite schema for the ToDo application.
 *
 * Table: tasks
 * Columns:
 *   _id          INTEGER PRIMARY KEY AUTOINCREMENT
 *   name         TEXT NOT NULL
 *   description  TEXT
 *   status       INTEGER NOT NULL DEFAULT 0   (0 = to do, 1 = done)
 *   priority     INTEGER NOT NULL DEFAULT 1   (0 = low, 1 = medium, 2 = high)
 *   due_date     TEXT
 *   created_at   TEXT NOT NULL
 *   updated_at   TEXT NOT NULL
 */
public final class TaskContract {

    private TaskContract() {
    }

    public static final String DATABASE_NAME = "todo.db";
    public static final int DATABASE_VERSION = 1;

    public static class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "tasks";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_PRIORITY = "priority";
        public static final String COLUMN_DUE_DATE = "due_date";
        public static final String COLUMN_CREATED_AT = "created_at";
        public static final String COLUMN_UPDATED_AT = "updated_at";
    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + TaskEntry.TABLE_NAME + " ("
                    + TaskEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + TaskEntry.COLUMN_NAME + " TEXT NOT NULL, "
                    + TaskEntry.COLUMN_DESCRIPTION + " TEXT, "
                    + TaskEntry.COLUMN_STATUS + " INTEGER NOT NULL DEFAULT 0, "
                    + TaskEntry.COLUMN_PRIORITY + " INTEGER NOT NULL DEFAULT 1, "
                    + TaskEntry.COLUMN_DUE_DATE + " TEXT, "
                    + TaskEntry.COLUMN_CREATED_AT + " TEXT NOT NULL, "
                    + TaskEntry.COLUMN_UPDATED_AT + " TEXT NOT NULL"
                    + ");";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + TaskEntry.TABLE_NAME;
}
