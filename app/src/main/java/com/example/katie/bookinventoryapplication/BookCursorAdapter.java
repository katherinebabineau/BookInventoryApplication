package com.example.katie.bookinventoryapplication;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.katie.bookinventoryapplication.data.BookContract;

/**
 * Created by Katie on 9/14/2017.
 */

public class BookCursorAdapter extends CursorAdapter {

    //constructor
    public BookCursorAdapter(Context context, Cursor c){
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String title = cursor.getString(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_TITLE));
        float price = cursor.getFloat(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE));
        final int quantity = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY));

        TextView titleTextView = view.findViewById(R.id.Title);
        TextView priceTextView = view.findViewById(R.id.Price);
        TextView quantityTextView = view.findViewById(R.id.Quantity);

        titleTextView.setText(title);
        priceTextView.setText(context.getString(R.string.all_inventory_price_text, price));
        quantityTextView.setText(quantity);

    }
}
