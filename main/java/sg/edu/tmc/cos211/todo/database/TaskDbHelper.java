package sg.edu.tmc.cos211.todo.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLiteOpenHelper that creates and upgrades the ToDo database.
 */
public class TaskDbHelper extends SQLiteOpenHelper {

    public TaskDbHelper(Context context) {
        super(context, TaskContract.DATABASE_NAME, null, TaskContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TaskContract.SQL_CREATE_TABLE);
        seedSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(TaskContract.SQL_DROP_TABLE);
        onCreate(db);
    }

    private void seedSampleData(SQLiteDatabase db) {
        String now = "2026-09-04 10:00:00";
        db.execSQL(
                "INSERT INTO " + TaskContract.TaskEntry.TABLE_NAME
                        + " (name, description, status, priority, due_date, created_at, updated_at) VALUES "
                        + "('To read and practice Android Development',"
                        + " 'Review lecture notes and complete the COS211 lab exercises.',"
                        + " 0, 2, '2026-09-12', '" + now + "', '" + now + "'),"
                        + "('To play online game',"
                        + " 'Take a short break after study sessions.',"
                        + " 0, 0, '', '" + now + "', '" + now + "'),"
                        + "('To practice Java programming',"
                        + " 'Rewrite CRUD methods and practise exception handling.',"
                        + " 1, 1, '2026-09-08', '" + now + "', '" + now + "');"
        );
    }
}
