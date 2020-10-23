package pl.kkowalewski.activeflap;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class ActiveFlapService extends Service {

    /*------------------------ FIELDS REGION ------------------------*/
    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "Channel_Id";

    /*------------------------ METHODS REGION ------------------------*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground();
        return super.onStartCommand(intent, flags, startId);
    }

    private void startForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);

        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, notificationIntent, 0);

        startForeground(NOTIFICATION_ID, new NotificationCompat.Builder(this,
                NOTIFICATION_CHANNEL_ID)
                .setOngoing(true)
                //                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }
}
