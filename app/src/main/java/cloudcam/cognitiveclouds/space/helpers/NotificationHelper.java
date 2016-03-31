package cloudcam.cognitiveclouds.space.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.lang.ref.WeakReference;

import cloudcam.cognitiveclouds.space.Imgurmodel.ImageResponse;
import cloudcam.cognitiveclouds.space.R;


public class NotificationHelper {
    public final static String TAG = NotificationHelper.class.getSimpleName();

    private WeakReference<Context> mContext;


    public NotificationHelper(Context context) {
        this.mContext = new WeakReference<>(context);
    }



    public void createUploadingNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_upload);
        mBuilder.setContentTitle(mContext.get().getString(R.string.notification_progress));
        mBuilder.setProgress(0,0,true);


        mBuilder.setColor(mContext.get().getResources().getColor(R.color.primary));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), mBuilder.build());

    }

    public void createUploadedNotification(ImageResponse response) {

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_menu_gallery);
        mBuilder.setContentTitle(mContext.get().getString(R.string.notifaction_success));

        String string = response.data.link;
        mBuilder.setContentText(string);

        mBuilder.setColor(mContext.get().getResources().getColor(R.color.primary));


        Intent resultIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(string));
        PendingIntent intent = PendingIntent.getActivity(mContext.get(), 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(intent);
        mBuilder.setAutoCancel(true);

        Intent shareIntent = new Intent(Intent.ACTION_SEND, Uri.parse(string));
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, string);
       // shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


        PendingIntent pIntent = PendingIntent.getActivity(mContext.get(), 0, shareIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.addAction(new NotificationCompat.Action(R.drawable.abc_ic_menu_share_mtrl_alpha,
                mContext.get().getString(R.string.notification_share_link), pIntent));

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), mBuilder.build());
    }

    public void createFailedUploadNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(mContext.get());
        mBuilder.setSmallIcon(android.R.drawable.ic_dialog_alert);
        mBuilder.setContentTitle(mContext.get().getString(R.string.notification_fail));


        mBuilder.setColor(mContext.get().getResources().getColor(R.color.primary));

        mBuilder.setAutoCancel(true);

        NotificationManager mNotificationManager =
                (NotificationManager) mContext.get().getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationManager.notify(mContext.get().getString(R.string.app_name).hashCode(), mBuilder.build());
    }
}
