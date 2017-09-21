package com.example.katie.bookinventoryapplication;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

/**
 * Created by Katie on 9/14/2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    //Book Data Loader
    private static final int EXISTING_BOOK_LOADER = 0;

    //Content URI for existing book
    private Uri mCurrentBookUri;

    //EditText Field to enter the Book Title
    private EditText mTitleEditText;

    //EditText Field to enter the Book Author
    private EditText mAuthorEditText;

    //EditText Field to enter the Supplier Email
    private EditText mSupplierEmailEditText;

    //ImageView for Product Image
    private ImageView mProductImageView;

    //EditText Field to enter the Price
    private EditText mPriceEditText;

    //EditText Field to enter the Quantity
    private EditText mQuantityEditText;

    //EditText Field to enter the Number of Pages
    private EditText mPageNumberEditText;

    //Boolean to keep track of book edits
    private boolean mBookHasChanged = false;



    /**
     * On Touch Listener for when a user touches a View, implies modification on that view and changes
     * the boolean above to true. "Book has now changed"
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent){
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
