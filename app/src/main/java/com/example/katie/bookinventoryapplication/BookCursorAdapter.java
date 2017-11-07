package com.example.katie.bookinventoryapplication;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.katie.bookinventoryapplication.data.BookContract;

/**
 * Created by Katie on 9/14/2017.
 */

public class BookCursorAdapter extends CursorAdapter {
    private static final String TAG = "MyActivity";


    //constructor
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE));
        float price = cursor.getFloat(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY));
        final int bookId = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID));
        Log.i(TAG, "bookId: " + bookId);

        TextView titleTextView = view.findViewById(R.id.Title);
        TextView priceTextView = view.findViewById(R.id.Price);
        TextView quantityTextView = view.findViewById(R.id.Quantity);

        titleTextView.setText(title);
        priceTextView.setText(context.getString(R.string.all_inventory_price_text, price));
        quantityTextView.setText(String.valueOf(quantity));

        Button saleButton = view.findViewById(R.id.saleButton);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (quantity > 0) {
                    int newQuantity = quantity - 1;
                    Uri bookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, bookId);

                    ContentValues values = new ContentValues();
                    values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, newQuantity);
                    context.getContentResolver().update(bookUri, values, null, null);
                    Toast.makeText(context, context.getString(R.string.saleToast), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, context.getString(R.string.stock_unavailable), Toast.LENGTH_LONG).show();
                }
            }
        });

    }
}
