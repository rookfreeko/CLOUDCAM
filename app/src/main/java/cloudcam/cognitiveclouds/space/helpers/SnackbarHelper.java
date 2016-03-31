package cloudcam.cognitiveclouds.space.helpers;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import cloudcam.cognitiveclouds.space.adapter.CustomCursorAdapter;

/**
 * Created by ROOK on 3/31/2016.
 */
public class SnackbarHelper {

    //snackbar for opening selelcted link
    public void snackbarOnclick(CoordinatorLayout coordinatorLayout, final Cursor cursor, final Context applicationContext, long id){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Open link "+id+" in Browser...?", Snackbar.LENGTH_LONG)
                .setAction("OPEN", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.setData(Uri.parse(cursor.getString(cursor.getColumnIndex("_pin"))));
                        applicationContext.startActivity(i);
                    }
                });
        snackbar.show();
    }

    //snackbar for deleting the item
    public void snackbarOnLongclick(final CoordinatorLayout coordinatorLayout, final long id, final DatabaseHelper databaseHelper, final Context applicationContext, final ListView listView){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Delete item "+id+" ...?", Snackbar.LENGTH_LONG)
                .setAction("DELETE", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        databaseHelper.remove(id);
                        Snackbar snackbar1 = Snackbar.make(coordinatorLayout,"Item " + id + " deleted..", Snackbar.LENGTH_SHORT);
                        snackbar1.show();
                        DatabaseHelper databaseHelper = new DatabaseHelper(applicationContext);
                        CustomCursorAdapter customAdapter = new CustomCursorAdapter(applicationContext, databaseHelper.getAllData());
                        listView.setAdapter(customAdapter);
                    }
                });
        snackbar.show();
    }

    //sncakbar to notify information
    public void snackbarNotify(CoordinatorLayout coordinatorLayout){
        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, "Long Press on item to DELETE..", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
