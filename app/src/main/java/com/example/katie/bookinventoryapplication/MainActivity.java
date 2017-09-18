package com.example.katie.bookinventoryapplication;

import android.app.LoaderManager;
import android.content.Loader;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER= 0;
    BookCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Links the FAB to become an intent to the editor activity

        //Finding the listView for the MainActivity-- this will be populated with book data
        ListView bookListView = (ListView) findViewById(R.id.list);

        //Set the empty view on the ListView so that it will show when there are no books.

        //Link the Adapter to add data to each list item of book data in the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);

        //Setup what happens when a list item is clicked. This should bring you to the details activity

        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
