package com.example.studentapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DB_NAME = "student.db";
    public static final int DB_VERSION = 1;
    public static final String TABLE = "students";
    public static final String COL_ID = "student_id";
    public static final String COL_NAME = "student_name";
    public static final String COL_AGE = "student_age";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // Called once, when the database is first created
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE + " ("
                + COL_ID + " TEXT PRIMARY KEY, "
                + COL_NAME + " TEXT NOT NULL, "
                + COL_AGE + " INTEGER NOT NULL CHECK(" + COL_AGE + " BETWEEN 17 AND 50))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }

    // ADD
    public boolean insertStudent(Student s) {
        ContentValues cv = new ContentValues();
        cv.put(COL_ID, s.getId());
        cv.put(COL_NAME, s.getName());
        cv.put(COL_AGE, s.getAge());
        // insert() returns -1 on failure (e.g. the ID already exists)
        return getWritableDatabase().insert(TABLE, null, cv) != -1;
    }

    // EDIT (matched by Student ID)
    public boolean updateStudent(Student s) {
        ContentValues cv = new ContentValues();
        cv.put(COL_NAME, s.getName());
        cv.put(COL_AGE, s.getAge());
        return getWritableDatabase().update(TABLE, cv, COL_ID + " = ?",
                new String[]{s.getId()}) > 0;
    }

    // DELETE (matched by Student ID)
    public boolean deleteStudent(String id) {
        return getWritableDatabase().delete(TABLE, COL_ID + " = ?",
                new String[]{id}) > 0;
    }

    // VIEW
    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        Cursor cursor = getReadableDatabase().rawQuery(
                "SELECT " + COL_ID + ", " + COL_NAME + ", " + COL_AGE
                        + " FROM " + TABLE + " ORDER BY " + COL_NAME, null);
        while (cursor.moveToNext()) {
            list.add(new Student(cursor.getString(0), cursor.getString(1), cursor.getInt(2)));
        }
        cursor.close();
        return list;
    }
}