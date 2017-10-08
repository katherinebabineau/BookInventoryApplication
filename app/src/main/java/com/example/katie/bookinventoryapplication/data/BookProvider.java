package com.example.katie.bookinventoryapplication.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by Katie on 9/14/2017.
 */

public class BookProvider extends ContentProvider {

    //URI matcher for the content URI for books table
    public static final int BOOKS = 100;

    //URI matcher for the content URI for a single book
    public static final int BOOK_ID = 101;
    //Tag for log messages
    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    //URI matcher Object- this will match a context URI to corresponding code.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {

        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        //Cursor to hold the result of the query
        Cursor cursor;

        //Determines whether the URI matcher find the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        //listen for changes at this URI, then the Cursor needs to be updated
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


    private Uri insertBook(Uri uri, ContentValues values) {
        sanityCheck(values);

        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);

        if (id == -1) {
            Log.d(TAG, "Failed to insert row for" + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }


    private int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        sanityCheck(values);

        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        //return the number of rows updated
        return rowsUpdated;
    }


    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                //delete all rows that match the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                //delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        //notify listeners if 1 or more rows are deleted
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }


    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    /**
     * Method to provide sanity checks for the UpdateBook and InsertBook methods
     * used as a helper so as not to waste space typing sanity checks twice separately.
     */

    private void sanityCheck(ContentValues values) {

        //if the Image entry is present, check that the value is not null
        if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_IMAGE)) {
            String image = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_IMAGE);
            if (image == null) {
                throw new IllegalArgumentException("Book requires an Image");
            }
        }

        //if the Title entry is present, check that the value is not null
        if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_TITLE)) {
            String title = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_TITLE);
            if (title == null) {
                throw new IllegalArgumentException("Book requires an Title");
            }
        }

        //if the Author entry is present, check that the value is not null
        if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_AUTHOR)) {
            String author = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_AUTHOR);
            if (author == null) {
                throw new IllegalArgumentException("Book requires an Author");
            }
        }

        //if the Pages entry is present, check that the value is valid
        if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_PAGES)) {
            Integer pages = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_PAGES);
            if (pages != null && pages < 0) {
                throw new IllegalArgumentException("Invalid Page Number Quantity");
            }
        }
            //if the Price entry is present, check that the value is valid
            if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_PRICE)) {
                Float price = values.getAsFloat(BookContract.BookEntry.COLUMN_BOOK_PRICE);
                if (price != null && price < 0) {
                    throw new IllegalArgumentException("Book requires a valid price");
                }
            }
            //if the Quantity entry is present, check that the value is valid
            if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_QUANTITY)) {
                Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
                if (quantity != null && quantity < 0) {
                    throw new IllegalArgumentException("Invalid Stock Quantity");
                }
            }

    }
}
