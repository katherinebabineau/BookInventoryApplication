package com.example.katie.bookinventoryapplication;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.katie.bookinventoryapplication.data.BookContract;

/**
 * Created by Katie on 9/21/2017.
 */

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>, View.OnClickListener{

    //Variables for available views
    private ImageView mImageView;
    private TextView mTitleTextView;
    private TextView mAuthorTextView;
    private TextView mPagesTextView;
    private TextView mPriceTextView;
    private TextView mQuantityTextView;
    private TextView mSupplierEmailTextView;

    private Button mEmailSupplierButton;
    private Button mIncQuantityButton;
    private Button mDecQuantityButton;


    private Uri mCurrentBookUri;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        //Get the URI of the book clicked from the MainActivity
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        //Connect all views to the Java code
        mTitleTextView = (TextView) findViewById(R.id.bookTitleTextViewDetails);
        mAuthorTextView = (TextView) findViewById(R.id.bookAuthorTextViewDetails);
        mPagesTextView = (TextView) findViewById(R.id.bookPagesTextViewDetails);
        mPriceTextView = (TextView) findViewById(R.id.bookPriceTextViewDetails);
        mQuantityTextView = (TextView) findViewById(R.id.quantitySummaryDetails);
        mSupplierEmailTextView = (TextView) findViewById(R.id.supplierEmailtextViewDetails);

        mEmailSupplierButton = (Button) findViewById(R.id.emailButtonDetails);
        mImageView = (ImageView) findViewById(R.id.imageView_details);
        mIncQuantityButton = (Button) findViewById(R.id.addQuantityButtonDetails);
        mDecQuantityButton = (Button) findViewById(R.id.decreaseQuantityButtonDetails);


        mIncQuantityButton.setOnClickListener(this);
        mDecQuantityButton.setOnClickListener(this);
        mEmailSupplierButton.setOnClickListener(this);

        //Initiate Loader Manager
        getLoaderManager().initLoader(2, null, this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;

    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        //We need all columns this time
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_IMAGE,
                BookContract.BookEntry.COLUMN_BOOK_TITLE,
                BookContract.BookEntry.COLUMN_BOOK_AUTHOR,
                BookContract.BookEntry.COLUMN_BOOK_PAGES,
                BookContract.BookEntry.COLUMN_BOOK_PAGES,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY
        };
        return new CursorLoader(this, mCurrentBookUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        //If cursor has data move to first and read
        if (cursor.moveToFirst()) {
            //Get index of column in cursor
            int imageColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_IMAGE);
            int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_AUTHOR);
            int pagesColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PAGES);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int suppEmailColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);



            //Get data from column using column index and store them in variables
            String image = cursor.getString(imageColumnIndex);
            String title = cursor.getString(titleColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            String pages = cursor.getString(pagesColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            String suppEmail = cursor.getString(suppEmailColumnIndex);
            Integer quantity = cursor.getInt(quantityColumnIndex);



            //Present the data to user
            Uri uri = Uri.parse(image);
            mImageView.setImageURI(uri);
            mTitleTextView.setText(title);
            mAuthorTextView.setText(author);
            mPagesTextView.setText(pages);
            mPriceTextView.setText(String.valueOf(price));
            mSupplierEmailTextView.setText(suppEmail);
            mQuantityTextView.setText(String.valueOf(quantity));

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTitleTextView.setText("");
        mAuthorTextView.setText("");
        mPagesTextView.setText("");
        mPriceTextView.setText("");
        mQuantityTextView.setText("");
        mImageView.setImageURI(null);
        mSupplierEmailTextView.setText("");
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.decreaseQuantityButtonDetails:
                changeInventory(-1);
                break;
            case R.id.addQuantityButtonDetails:
                changeInventory(1);
                break;
            case R.id.emailButtonDetails:
                emailSupplier();
                break;
        }
    }

    /**
     * Helper method to increment and decrement stock
     * @param value
     */
    private void changeInventory(int value) {
        //Get the previous value first
        int prevValue = Integer.valueOf(mQuantityTextView.getText().toString());

        //Determine if previous value is >0(for decrement) & previous value >=0 (for increment)
        if ((prevValue > 0) || (prevValue >= 0 && value > 0)) {
            //Add the new value. Make key-pair data and update
            value += prevValue;
            ContentValues contentValues = new ContentValues();
            contentValues.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, value);
            getContentResolver().update(mCurrentBookUri, contentValues, null, null);
        }
    }

    /**
     * Helper method to contact supplier via E-Mail
     * Starts email app if available
     */
    private void emailSupplier() {
        String email = mSupplierEmailTextView.getText().toString();
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, email);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.toast_no_email), Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * Helper method to open {@link EditorActivity} when "Edit" icon in action bar is tapped
     */
    private void openEditorActivity() {
        Intent intent = new Intent(DetailsActivity.this, EditorActivity.class);
        intent.setData(mCurrentBookUri);
        startActivity(intent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_edit:
                openEditorActivity();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (mCurrentBookUri != null) {
            // Call the ContentResolver to delete the book at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentBookUri
            // content URI already identifies the book that we want.
            int rowsDeleted = getContentResolver().delete(mCurrentBookUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.editor_delete_book_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_delete_book_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity
        finish();
    }

}
