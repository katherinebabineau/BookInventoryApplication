package com.example.katie.bookinventoryapplication.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Katie on 9/14/2017.
 *
 */

public class BookDbHelper extends SQLiteOpenHelper {

    //log statement
    private static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    //The name of the Database file.
    private static final String DATABASE_NAME = "books.db";

    //Database version
    private static final int DATABASE_VERSION = 1;

    //constructor
    public  BookDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //This is created upon the first use of the database
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //A string that contains the SQL statement to create the books table.
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
           + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
           + BookContract.BookEntry.COLUMN_BOOK_IMAGE + " TEXT NOT NULL, "
           + BookContract.BookEntry.COLUMN_BOOK_TITLE + " TEXT NOT NULL, "
           + BookContract.BookEntry.COLUMN_BOOK_AUTHOR + " TEXT NOT NULL, "
           + BookContract.BookEntry.COLUMN_BOOK_PAGES + " INTEGER NOT NULL DEFAULT 0, "
           + BookContract.BookEntry.COLUMN_BOOK_PRICE + " REAL NOT NULL, "
           + BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL + " TEXT NOT NULL, "
           + BookContract.BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL DEFAULT 0);";


        //Execute the SQL statement
         sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);

        //Log the query
        Log.d(LOG_TAG, SQL_CREATE_BOOKS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
     //database at version 1 for now.
    }
}
