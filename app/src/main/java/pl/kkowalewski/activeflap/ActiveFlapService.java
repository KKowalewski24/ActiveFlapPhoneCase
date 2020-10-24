package pl.kkowalewski.activeflap;

import android.app.PendingIntent;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static pl.kkowalewski.activeflap.MainActivity.ADD_ADMIN_PRIVILEGES;
import static pl.kkowalewski.activeflap.MainActivity.COMPONENT_NAME;
import static pl.kkowalewski.activeflap.MainActivity.PROXIMITY_THRESHOLD;

public class ActiveFlapService extends Service implements SensorEventListener {

    /*------------------------ FIELDS REGION ------------------------*/
    public static final int NOTIFICATION_ID = 1;
    public static final String NOTIFICATION_CHANNEL_ID = "Channel_Id";
    public static final String WAKE_LOCK_TAG = ":TAG";

    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private SensorManager sensorManager;
    private Sensor proximitySensor;

    /*------------------------ METHODS REGION ------------------------*/
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = (ComponentName) intent.getExtras().get(COMPONENT_NAME);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        sensorManager.registerListener(this, proximitySensor,
                SensorManager.SENSOR_DELAY_FASTEST);

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
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Service is running background")
                .setContentIntent(pendingIntent)
                .build());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY
                && devicePolicyManager.isAdminActive(componentName)) {
            final float distance = event.values[0];

            if (distance <= PROXIMITY_THRESHOLD) {
                devicePolicyManager.lockNow();
            } else {
                PowerManager powerManager = (PowerManager) getApplicationContext()
                        .getSystemService(Context.POWER_SERVICE);
                PowerManager.WakeLock wakeLock = powerManager
                        .newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK
                                | PowerManager.FULL_WAKE_LOCK
                                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                        ), WAKE_LOCK_TAG);
                wakeLock.acquire();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    ADD_ADMIN_PRIVILEGES, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
