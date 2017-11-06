package com.example.katie.bookinventoryapplication;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.katie.bookinventoryapplication.data.BookContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int BOOK_LOADER= 0;
    private BookCursorAdapter mCursorAdapter;
    private static final String TAG = "MyActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Finding the listView for the MainActivity-- this will be populated with book data
        ListView bookListView = (ListView) findViewById(R.id.list);
        Log.i(TAG, "listView: " + bookListView );
        //Set the empty view on the ListView so that it will show when there are no books.
        View emptyView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(emptyView);

        //Link the Adapter to add data to each list item of book data in the Cursor.
        mCursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(mCursorAdapter);
        Log.i(TAG, "end onCreate" );
        //Setup what happens when a list item is clicked. This should bring you to the details activity
        //and won't be editable until another editing button is clicked.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.i(TAG, "onItemClick" );
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.setData(currentBookUri);
                Log.i(TAG, "detailsActivityShouldStart" );
                startActivity(intent);

            }
        });

        Log.i(TAG, "end onCreate" );
        //Links the FAB to become an intent to the editor activity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (MainActivity.this, EditorActivity.class);
                startActivity(intent);
            }
                               });

        getLoaderManager().initLoader(BOOK_LOADER, null, this);

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
               return true;
            case R.id.action_delete_all_entries:
                deleteAllProduct(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
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

    private static void deleteAllProduct(Context context) {
        int numRows = context.getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
        if (numRows > 0) {
            Toast.makeText(context, context.getString(R.string.toast_delete_single_book).replace("#", String.valueOf(numRows)), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, context.getString(R.string.toast_error_deleting), Toast.LENGTH_SHORT).show();
        }
    }
}
