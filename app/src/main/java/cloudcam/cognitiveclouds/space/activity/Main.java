package cloudcam.cognitiveclouds.space.activity;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import cloudcam.cognitiveclouds.space.Imgurmodel.ImageResponse;
import cloudcam.cognitiveclouds.space.Imgurmodel.ImgurAPI;
import cloudcam.cognitiveclouds.space.R;
import cloudcam.cognitiveclouds.space.helpers.DatabaseHelper;
import cloudcam.cognitiveclouds.space.helpers.NotificationHelper;
import cloudcam.cognitiveclouds.space.helpers.PathHelper;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

/**
 * Created by ROOK on 3/24/2016.
 */
public class Main extends Activity {

    ImageButton cam,gal;
    ImageButton up,clr,dat;
    ImageView prev;
    Uri filePath;
    int flag=0;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        final boolean isM = Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1;
        final Animation animScale = AnimationUtils.loadAnimation(this, R.anim.anim_scale);

        if (isM){
            checkperm();
        }

        clr = (ImageButton) findViewById(R.id.imageButton4);
        cam = (ImageButton) findViewById(R.id.imageButton);
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animScale);
                clickpic();
            }
        });

        gal = (ImageButton) findViewById(R.id.imageButton2);
        gal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animScale);
                galpic();
            }
        });

        dat = (ImageButton) findViewById(R.id.imageButtondat);
        dat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(animScale);
                Intent myIntent = new Intent(getApplicationContext(), Main2.class);
                startActivity(myIntent);
            }
        });

        up = (ImageButton) findViewById(R.id.imageButton3);

    }

    //checks required permissions (ANROID M)
    public void checkperm(){
        int hascameraPermission = checkSelfPermission( Manifest.permission.CAMERA );
        int hasstoragePermission = checkSelfPermission( Manifest.permission.WRITE_EXTERNAL_STORAGE );
        int hasrstoragePermission = checkSelfPermission( Manifest.permission.READ_EXTERNAL_STORAGE );
        int hasinternetPermission = checkSelfPermission( Manifest.permission.INTERNET );
        int hasaccesnetworkPermission = checkSelfPermission( Manifest.permission.ACCESS_NETWORK_STATE );

        List<String> permissions = new ArrayList<String>();
        if( hascameraPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add(Manifest.permission.CAMERA);
        }
        if( hasstoragePermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.WRITE_EXTERNAL_STORAGE );
        }
        if( hasrstoragePermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.READ_EXTERNAL_STORAGE );
        }
        if( hasinternetPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.INTERNET );
        }
        if( hasaccesnetworkPermission != PackageManager.PERMISSION_GRANTED ) {
            permissions.add( Manifest.permission.ACCESS_NETWORK_STATE );
        }

        if( !permissions.isEmpty() ) {
            requestPermissions( permissions.toArray( new String[permissions.size()] ),50 );
        }
    }

    //Permission result
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch ( requestCode ) {
            case 50: {
                for( int i = 0; i < permissions.length; i++ ) {
                    if( grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                        Log.d( "Permissions", "Permission Granted: " + permissions[i] );
                    } else if( grantResults[i] == PackageManager.PERMISSION_DENIED ) {
                        Log.d( "Permissions", "Permission Denied: " + permissions[i] );
                    }
                }
            }
            break;
            default: {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
    }

    //intent to select a image from gallery
    public void galpic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
    }

    //click a new image using camera
    public void clickpic(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        filePath = getOutputMediaFile();
        Log.d("filepath", " " + filePath);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, filePath);
        // start the image capture Intent
        startActivityForResult(intent, 100);
    }

    //result of camera or gallery
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null){
            try {
                filePath = data.getData();
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                Log.d("old "," "+filePath);
                filePath = Uri.parse(PathHelper.getPathFromURI(getApplicationContext(), filePath));
                Log.d("new "," "+filePath);

                preview(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else if (requestCode == 100 && resultCode == RESULT_OK){
            try{
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse(filePath.toString())));
                Log.d("new ", " " + filePath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                preview(bitmap);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    //creating a file for camera image
    public static Uri getOutputMediaFile() {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),"CloudCam");

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdir()) {
                Log.d("CloudCam", " Oops! Failed create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");

        return Uri.fromFile(mediaFile);
    }

    //used to show selected image preview
    public void preview(final Bitmap bitmap){

        int i=1;
        if(bitmap.getWidth() > 2000 || bitmap.getHeight() > 2000){
            i=2;
        }
        //Setting the Bitmap to ImageView
        prev = (ImageView) findViewById(R.id.imageView);
        //scaleBitmap(bitmap,bitmap.getWidth()/3,bitmap.getHeight()/3);
        prev.setImageBitmap(scaleBitmap(bitmap,bitmap.getWidth()/i,bitmap.getHeight()/i));
        clr.setVisibility(View.VISIBLE);
        clr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                up.setVisibility(View.INVISIBLE);
                clr.setVisibility(View.GONE);
                prev.setImageDrawable(null);
                filePath = null;
            }
        });

        if(flag==0) {
            up.setVisibility(View.VISIBLE);
        }
        up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filePath!=null){
                        up.setVisibility(View.INVISIBLE);
                        UploadToImgurTask();
                }else {
                    Toast.makeText(getApplicationContext(), "No file Selected...",Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    //initialize rest
    public RestAdapter buildRestAdapter() {
        RestAdapter imgurAdapter = new RestAdapter.Builder()
                .setEndpoint(ImgurAPI.server)
                .build();
        return imgurAdapter;
    }

    //scale bitmap incase it is very large
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {

        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();
        float pivotX = 0;
        float pivotY = 0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap, 0, 0, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    //check visiblity of the upload button
    public void upvisiblity(){
        if (filePath!=null) {
            up.setVisibility(View.VISIBLE);
        }
    }


    //upload image to the imgur server
    public void UploadToImgurTask(){
        flag=1;
        final NotificationHelper notificationHelper = new NotificationHelper(getApplicationContext());
        notificationHelper.createUploadingNotification();

        databaseHelper = new DatabaseHelper(this);

        File fileimg = new File(filePath.getPath());

        RestAdapter restAdapter = buildRestAdapter();

        restAdapter.create(ImgurAPI.class).postImage(
                "Client-ID 70a225e48cb701c",
                null,
                null,
                null,
                null,
                new TypedFile("image/*",fileimg),
                new Callback<ImageResponse>(){

                    @Override
                    public void success(ImageResponse imageResponse, Response response) {
                            /*
                            Notify image was uploaded successfully
                            */
                        if (imageResponse.success) {

                            Toast.makeText(getApplicationContext(), "upload succes "+imageResponse.data.link,Toast.LENGTH_LONG).show();
                            flag=0;
                            upvisiblity();
                            notificationHelper.createUploadedNotification(imageResponse);
                            databaseHelper.insertData("    : "+new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()), imageResponse.data.link);
                        }
                    }
                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(getApplicationContext(), "Retrofit error..."+error,Toast.LENGTH_LONG).show();
                        notificationHelper.createFailedUploadNotification();
                        flag=0;
                        upvisiblity();
                    }
        });
    }

}
