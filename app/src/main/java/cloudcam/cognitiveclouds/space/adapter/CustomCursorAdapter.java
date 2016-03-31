package cloudcam.cognitiveclouds.space.adapter;

/**
 * Created by ROOK on 3/30/2016.
 */

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import cloudcam.cognitiveclouds.space.R;

public class CustomCursorAdapter extends CursorAdapter {

    public CustomCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // when the view will be created for first time,
        // we need to tell the adapters, how each item will look
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View retView = inflater.inflate(R.layout.single_row_item, parent, false);

        return retView;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // here we are setting our data
        // that means, take the data from the cursor and put it in views

        Log.d(" "," CURSOR: " + cursor);

        TextView textViewId = (TextView) view.findViewById(R.id.tv_id);
        textViewId.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(0))));

        TextView textViewName = (TextView) view.findViewById(R.id.tv_name);
        textViewName.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));

        TextView textViewPIN = (TextView) view.findViewById(R.id.tv_pin);
        textViewPIN.setText(cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
    }
}
