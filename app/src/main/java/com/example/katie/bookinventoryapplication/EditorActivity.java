package com.example.katie.bookinventoryapplication;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.katie.bookinventoryapplication.data.BookContract;


/**
 * Created by Katie on 9/14/2017.
 */

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private static final int IMAGE_REQUEST_CODE = 1100;
    //Content URI for existing book
    private Uri mCurrentBookUri;
    //ImageView for Product Image
    private ImageView mProductImageView;
    //EditText Field to enter the Book Title
    private EditText mTitleEditText;
    //EditText Field to enter the Book Author
    private EditText mAuthorEditText;
    //EditText Field to enter the Number of Pages
    private EditText mPageNumberEditText;
    //EditText Field to enter the Price
    private EditText mPriceEditText;
    //EditTex Field to enter the Supplier Email
    private EditText mSupplierEmail;
    //EditText Field to enter the Quantity
    private EditText mQuantityEditText;
    //Boolean to keep track of book edits
    private boolean mBookHasChanged = false;
    //Store the URI of the Book Image selected
    private Uri mSelectedImage;
    //Store the URI of the book which is selected for editing
    /**
     * On Touch Listener for when a user touches a View, implies modification on that view and changes
     * the boolean above to true. "Book has now changed"
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mBookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        //Examine if the intent sent here was for creating a new book or editing one.
        Intent intent = getIntent();
        mCurrentBookUri = intent.getData();

        //If the intent does not contain book URI- create new book.
        if (mCurrentBookUri == null) {
            setTitle(getString(R.string.add_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_book));
            getLoaderManager().initLoader(1, null, this);
        }

        //Find all views
        mProductImageView = (ImageView) findViewById(R.id.imageView_editor);
        mTitleEditText = (EditText) findViewById(R.id.bookTitleEditTextEditor);
        mAuthorEditText = (EditText) findViewById(R.id.bookAuthorEditTextEditor);
        mPageNumberEditText = (EditText) findViewById(R.id.bookPagesEditTextEditor);
        mPriceEditText = (EditText) findViewById(R.id.bookPriceEditTextEditor);
        mSupplierEmail = (EditText) findViewById(R.id.supplierEmaileditor);
        mQuantityEditText = (EditText) findViewById(R.id.quantitySummaryEditor);


        //Image Picker Intent
        mProductImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }
                intent.setType("image/*");
                startActivityForResult(intent, IMAGE_REQUEST_CODE);
            }
        });


        //onTouchListener for all fields
        mProductImageView.setOnTouchListener(mTouchListener);
        mTitleEditText.setOnTouchListener(mTouchListener);
        mAuthorEditText.setOnTouchListener(mTouchListener);
        mPageNumberEditText.setOnTouchListener(mTouchListener);
        mPriceEditText.setOnTouchListener(mTouchListener);
        mSupplierEmail.setOnTouchListener(mTouchListener);
        mQuantityEditText.setOnTouchListener(mTouchListener);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                Toast.makeText(this, getString(R.string.toast_error_with_image), Toast.LENGTH_SHORT).show();
                return;
            }
            mSelectedImage = data.getData();
            mProductImageView.setImageURI(mSelectedImage);

        }
    }


    private void saveBook() {
        //reading data from the input field
        String image = null;
        if (mCurrentBookUri == null) {
            if (mSelectedImage != null) image = mSelectedImage.toString();
        } else image = mSelectedImage.toString();
        String titleString = mTitleEditText.getText().toString().trim();
        String authorString = mAuthorEditText.getText().toString().trim();
        String pagesString = mPageNumberEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();
        String supplierString = mSupplierEmail.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();


        //Check to make sure that all fields in the editor are blank

        if (mCurrentBookUri == null &&
                TextUtils.isEmpty(image) && TextUtils.isEmpty(titleString) && TextUtils.isEmpty(authorString) &&
                TextUtils.isEmpty(pagesString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString)) {
            return;
        }

        //ContentValues object where column names are the keys,
        //and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_IMAGE, image);
        values.put(BookContract.BookEntry.COLUMN_BOOK_TITLE, titleString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_AUTHOR, authorString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_PAGES, pagesString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL, supplierString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantityString);


        //Find out if this is a new book or an edit of an existing book
        if (mCurrentBookUri == null) {
            //this is a new book, so insert into provider
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
            //Show a toast, this will depend on if the new book was successfully inserted
            if (newUri == null) {
                Toast.makeText(this, "Error with saving book",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book Saved",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentBookUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error with Updating Book",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Book Updated", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the book.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (mCurrentBookUri == null) {
            MenuItem menuitem = menu.findItem(R.id.action_delete);
            menuitem.setVisible(false);
        }
        return true;
    }

    /**
     * Prompt the user to confirm that they want to delete this book.
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
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
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //determining what item is clicked in the menu
        switch (item.getItemId()) {
            case R.id.action_save:
                //save book to database
                saveBook();
                //exit
                finish();
                return true;
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mBookHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book hasn't changed, continue with handling back button press
        if (!mBookHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_IMAGE,
                BookContract.BookEntry.COLUMN_BOOK_TITLE,
                BookContract.BookEntry.COLUMN_BOOK_AUTHOR,
                BookContract.BookEntry.COLUMN_BOOK_PAGES,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY
                };

        return new CursorLoader(this,
                mCurrentBookUri,
                projection,
                null,
                null,
                null);
    }





    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            //find the columns of book attributes
            int imageColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_IMAGE);
            int titleColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE);
            int authorColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_AUTHOR);
            int pagesColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PAGES);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER_EMAIL);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);

            //Extract from column using index
            String image = cursor.getString(imageColumnIndex);
            String title = cursor.getString(titleColumnIndex);
            String author = cursor.getString(authorColumnIndex);
            Integer pages = cursor.getInt(pagesColumnIndex);
            Float price = cursor.getFloat(priceColumnIndex);
            String email = cursor.getString(supplierColumnIndex);
            Integer quantity = cursor.getInt(quantityColumnIndex);

            //Show data to user
            Uri uri = Uri.parse(image);
            mProductImageView.setImageURI(uri);
            mSelectedImage = uri;
            mTitleEditText.setText(title);
            mAuthorEditText.setText(author);
            mPageNumberEditText.setText(String.valueOf(pages));
            mPriceEditText.setText(String.valueOf(price));
            mSupplierEmail.setText(email);
            mQuantityEditText.setText(String.valueOf(quantity));

        }
   }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductImageView.setImageURI(null);
        mTitleEditText.setText("");
        mAuthorEditText.setText("");
        mPageNumberEditText.setText("");
        mPriceEditText.setText("");
        mSupplierEmail.setText("");
        mQuantityEditText.setText("");

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
