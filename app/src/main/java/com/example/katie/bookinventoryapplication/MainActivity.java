package com.example.katie.bookinventoryapplication;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.example.katie.bookinventoryapplication.data.BookContract;

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

        //Setup what happens when a list item is clicked. This should bring you to the editor activity
        //but the editor activity will only show details mode, and won't be editable until another
        //button is clicked.

        getLoaderManager().initLoader(BOOK_LOADER, null, this);

    }



    // Method to test adding dummy data into the database. For debugging purposes
    private void insertBook(){

        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_TITLE, "Beauty");
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, "1.23");
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, 2);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
               insertBook();
               return true;
            case R.id.action_delete_all_entries:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry.COLUMN_BOOK_TITLE,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY};
        return new CursorLoader(this,
                BookContract.BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
      mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
      mCursorAdapter.swapCursor(null);
    }
}
