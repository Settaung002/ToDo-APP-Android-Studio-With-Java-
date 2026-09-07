package sg.edu.tmc.cos211.todo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sg.edu.tmc.cos211.todo.database.TaskContract.TaskEntry;
import sg.edu.tmc.cos211.todo.model.Task;

/**
 * Data access object that performs CRUD operations on the tasks table.
 */
public class TaskDao {

    public static final int FILTER_ALL = 0;
    public static final int FILTER_TODO = 1;
    public static final int FILTER_DONE = 2;

    private final TaskDbHelper dbHelper;
    private final SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

    public TaskDao(Context context) {
        dbHelper = new TaskDbHelper(context.getApplicationContext());
    }

    public long insert(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String now = now();
        ContentValues values = toContentValues(task);
        values.put(TaskEntry.COLUMN_CREATED_AT, now);
        values.put(TaskEntry.COLUMN_UPDATED_AT, now);
        long id = db.insert(TaskEntry.TABLE_NAME, null, values);
        task.setId(id);
        task.setCreatedAt(now);
        task.setUpdatedAt(now);
        return id;
    }

    public List<Task> getAll() {
        return query(null, null, null);
    }

    public List<Task> getByStatus(int status) {
        return query(
                TaskEntry.COLUMN_STATUS + " = ?",
                new String[]{String.valueOf(status)},
                null
        );
    }

    public List<Task> search(String keyword, int filter) {
        String selection = null;
        String[] args = null;

        List<String> clauses = new ArrayList<>();
        List<String> arguments = new ArrayList<>();

        if (keyword != null && !keyword.trim().isEmpty()) {
            clauses.add("(" + TaskEntry.COLUMN_NAME + " LIKE ? OR "
                    + TaskEntry.COLUMN_DESCRIPTION + " LIKE ?)");
            String like = "%" + keyword.trim() + "%";
            arguments.add(like);
            arguments.add(like);
        }

        if (filter == FILTER_TODO) {
            clauses.add(TaskEntry.COLUMN_STATUS + " = ?");
            arguments.add(String.valueOf(Task.STATUS_TODO));
        } else if (filter == FILTER_DONE) {
            clauses.add(TaskEntry.COLUMN_STATUS + " = ?");
            arguments.add(String.valueOf(Task.STATUS_DONE));
        }

        if (!clauses.isEmpty()) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < clauses.size(); i++) {
                if (i > 0) {
                    builder.append(" AND ");
                }
                builder.append(clauses.get(i));
            }
            selection = builder.toString();
            args = arguments.toArray(new String[0]);
        }

        return query(selection, args, null);
    }

    public Task getById(long id) {
        List<Task> results = query(
                TaskEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null
        );
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0);
    }

    public int update(Task task) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String now = now();
        ContentValues values = toContentValues(task);
        values.put(TaskEntry.COLUMN_UPDATED_AT, now);
        int rows = db.update(
                TaskEntry.TABLE_NAME,
                values,
                TaskEntry._ID + " = ?",
                new String[]{String.valueOf(task.getId())}
        );
        task.setUpdatedAt(now);
        return rows;
    }

    public int updateStatus(long id, int status) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_STATUS, status);
        values.put(TaskEntry.COLUMN_UPDATED_AT, now());
        return db.update(
                TaskEntry.TABLE_NAME,
                values,
                TaskEntry._ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int delete(long id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        return db.delete(
                TaskEntry.TABLE_NAME,
                TaskEntry._ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    public int countAll() {
        return count(null, null);
    }

    public int countByStatus(int status) {
        return count(TaskEntry.COLUMN_STATUS + " = ?", new String[]{String.valueOf(status)});
    }

    public void close() {
        dbHelper.close();
    }

    private int count(String selection, String[] args) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(
                TaskEntry.TABLE_NAME,
                new String[]{"COUNT(*)"},
                selection,
                args,
                null,
                null,
                null
        );
        int total = 0;
        if (cursor.moveToFirst()) {
            total = cursor.getInt(0);
        }
        cursor.close();
        return total;
    }

    private List<Task> query(String selection, String[] args, String limit) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String orderBy = TaskEntry.COLUMN_STATUS + " ASC, "
                + TaskEntry.COLUMN_PRIORITY + " DESC, "
                + TaskEntry.COLUMN_CREATED_AT + " DESC";
        Cursor cursor = db.query(
                TaskEntry.TABLE_NAME,
                null,
                selection,
                args,
                null,
                null,
                orderBy,
                limit
        );
        List<Task> tasks = mapCursor(cursor);
        cursor.close();
        return tasks;
    }

    private List<Task> mapCursor(Cursor cursor) {
        List<Task> tasks = new ArrayList<>();
        int idIndex = cursor.getColumnIndexOrThrow(TaskEntry._ID);
        int nameIndex = cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME);
        int descIndex = cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_DESCRIPTION);
        int statusIndex = cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_STATUS);
        int priorityIndex = cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_PRIORITY);
        int dueIndex = cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_DUE_DATE);
        int createdIndex = cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_CREATED_AT);
        int updatedIndex = cursor.getColumnIndexOrThrow(TaskEntry.COLUMN_UPDATED_AT);

        while (cursor.moveToNext()) {
            Task task = new Task(
                    cursor.getLong(idIndex),
                    cursor.getString(nameIndex),
                    cursor.getString(descIndex),
                    cursor.getInt(statusIndex),
                    cursor.getInt(priorityIndex),
                    cursor.getString(dueIndex),
                    cursor.getString(createdIndex),
                    cursor.getString(updatedIndex)
            );
            tasks.add(task);
        }
        return tasks;
    }

    private ContentValues toContentValues(Task task) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME, task.getName());
        values.put(TaskEntry.COLUMN_DESCRIPTION, task.getDescription());
        values.put(TaskEntry.COLUMN_STATUS, task.getStatus());
        values.put(TaskEntry.COLUMN_PRIORITY, task.getPriority());
        values.put(TaskEntry.COLUMN_DUE_DATE, task.getDueDate());
        return values;
    }

    private String now() {
        return dateFormat.format(new Date());
    }
}
