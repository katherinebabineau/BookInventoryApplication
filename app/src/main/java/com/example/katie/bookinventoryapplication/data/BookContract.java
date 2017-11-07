package com.example.katie.bookinventoryapplication.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Katie on 9/14/2017.
 */

public final class BookContract {

    // The name for the content provider- string used is the content authority for the app.
    public static final String CONTENT_AUTHORITY = "com.example.katie.bookinventoryapplication";
    //content authority becomes the base for all URI's
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BOOKS = "books";

    //possible path

    //private and empty constructor
    private BookContract() {
    }

    //inner class define constant values for the books database table.

    public static abstract class BookEntry implements BaseColumns {

        // The MIME type of the content uri for a list of books.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //MIME type for a single book.
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        //Content URI to access book data in the provider.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        public final static String TABLE_NAME = "pets";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_BOOK_TITLE = "title";
        public static final String COLUMN_BOOK_AUTHOR = "author";
        public static final String COLUMN_BOOK_IMAGE = "image";
        public static final String COLUMN_BOOK_PAGES = "pages";
        public static final String COLUMN_BOOK_PRICE = "price";
        public static final String COLUMN_SUPPLIER_EMAIL = "supplier_email";
        public static final String COLUMN_BOOK_QUANTITY = "quantity_available";

    }
}
