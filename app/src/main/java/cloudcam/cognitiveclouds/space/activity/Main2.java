package cloudcam.cognitiveclouds.space.activity;

/**
 * Created by ROOK on 3/30/2016.
 */

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import cloudcam.cognitiveclouds.space.R;
import cloudcam.cognitiveclouds.space.adapter.CustomCursorAdapter;
import cloudcam.cognitiveclouds.space.helpers.DatabaseHelper;
import cloudcam.cognitiveclouds.space.helpers.SnackbarHelper;

//to show saved item
public class Main2 extends Activity {


    private DatabaseHelper databaseHelper;
    private ListView listView;
    SnackbarHelper snackbarHelper;


    private static final String TAG = Main2.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main2);
        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);
        final CoordinatorLayout coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        snackbarHelper = new SnackbarHelper();
        snackbarHelper.snackbarNotify(coordinatorLayout);

        databaseHelper = new DatabaseHelper(this);

        listView = (ListView) findViewById(R.id.list_data);
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.startAnimation(animScale);
                Log.d(TAG, "clicked on item: " + position);
                Cursor cursor = databaseHelper.getid(id);
                cursor.moveToFirst();
                //Log.d(TAG, " CURSOR: " + cursor );
                snackbarHelper.snackbarOnclick(coordinatorLayout, cursor,getApplicationContext(),id);
            }
        });


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                snackbarHelper.snackbarOnLongclick(coordinatorLayout, id, databaseHelper, getApplicationContext(),listView);
                Log.d(TAG, "clicked on item: " + position + " id " + id);
                return true;
            }
        });

        refresher();


    }

    // Database query can be a time consuming task ..
    // so its safe to call database query in another thread
    public void refresher(){
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                CustomCursorAdapter customAdapter = new CustomCursorAdapter(getApplicationContext(), databaseHelper.getAllData());
                listView.setAdapter(customAdapter);
            }
        });
    }
}
