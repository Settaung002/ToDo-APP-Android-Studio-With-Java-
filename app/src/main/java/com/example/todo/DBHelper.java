package com.example.todo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.material.color.utilities.ContrastCurve;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Tododata.db";
    public static final String TASK = "task";
    public static final String TASK_NAME = "task_name";
    public static final String DESCRIPTION = "description";
    public static final String STATUS = "status";
    public static final String DUE_DATE = "due_date";

    public DBHelper(Context context) {super(context, DATABASE_NAME, null, 2);}

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(" CREATE TABLE " + TASK + "("
                  + TASK_NAME + "TEXT PRIMARY KEY, "
                  + DESCRIPTION + "TEXT,"
                  + STATUS + "INTERGER DEFAULT 0,"
                  + DUE_DATE + "TEXT)");
        insertSampleDate(db);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
    db.execSQL("DROP TABLE if exists " + TASK);
        onCreate(db);
    }

    private void insertSampleDate(SQLiteDatabase db) {
        indertRow(db, "To read and pracitice Android Development",
                  "Read Andriod docs and practice layouts, activities and SQLite",0, "2026-9-15");
        indertRow(db, "To play online game",
                "Take a short break and play an onlinegame.",0, "2026-9-20");
        indertRow(db, "To practice python programming",
                "Review Python classes loops and Database",1, "2026-9-18");

    }

    private void indertRow(SQLiteDatabase db, String taskName, String description, int status, String dueDate){
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_NAME, taskName);
        contentValues.put(DESCRIPTION, description);
        contentValues.put(STATUS, status);
        contentValues.put(DUE_DATE, dueDate);
        db.insert(TASK, null, contentValues);
    }

    public boolean insertTaskData(String task){ return insertTaskData(task,
            "", 0, "");}

    public boolean insertTaskData(String task, String description, int status, String dueDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_NAME, task);
        contentValues.put(DESCRIPTION, description);
        contentValues.put(STATUS, status);
        contentValues.put(DUE_DATE,dueDate);
        long result = db.insert(TASK, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    public Cursor selectData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor  cursor = db.rawQuery("SELECT * FROM " + TASK, null);
        return cursor;
    }

    public Cursor selectTaskData(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(" SELECT * FROM " + TASK + " WHERE " + TASK_NAME + "=?"
                 , new String[]{task});
        return  cursor;
    }

    public boolean updateTaksData(String task, int status){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK_NAME, task);
        contentValues.put(STATUS,status);

        Cursor cursor = db.rawQuery(" SELECT * FROM " + TASK + " WHERE " + TASK_NAME + "=?"
                , new String[]{task});
        if (cursor.getCount() > 0){
            long result = db.update(TASK, contentValues,
                    TASK_NAME + "=?", new String[]{task});
            cursor.close();
            if (result == -1)
                return false;
            else return true;
        }
        else {
            cursor.close();
            return false;
        }
    }

    public boolean updateTaskData(String oldName, String task, String description, int status, String dueDate){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TASK, task);
        contentValues.put(DESCRIPTION, description);
        contentValues.put(STATUS, status);
        contentValues.put(DUE_DATE, dueDate);

        Cursor cursor = db.rawQuery(" SELECT * FROM " + TASK_NAME +
                " WHERE " + TASK_NAME + "=?", new String[]{oldName});
        if (cursor.getCount() > 0){
            long result = db.update(TASK_NAME, contentValues, TASK_NAME + "=?"
                      , new String[]{oldName});
            cursor.close();
            if (result == -1)
                return false;
            else
                return true;
        }
        else {
            cursor.close();
            return false;
        }

    }

    public boolean deleteTaskData(String task){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery( " SELECT * FROM " + TASK + " WHERE " + TASK_NAME +
                "=?" ,new String[]{task});

        if (cursor.getCount() > 0){
            long result = db.delete(TASK, TASK_NAME + "=?", new String[]{task});
            cursor.close();
            if (result == -1)
                return false;
            else
                return true;
        }
        else {
            cursor.close();
            return false;
        }

    }

}
