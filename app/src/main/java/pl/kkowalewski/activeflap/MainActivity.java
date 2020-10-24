package pl.kkowalewski.activeflap;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    /*------------------------ FIELDS REGION ------------------------*/
    public static final float PROXIMITY_THRESHOLD = 1.0f;
    public static final int RESULT_ENABLE = 1;
    public static final String ADD_ADMIN_PRIVILEGES = "Add Admin Privileges";
    public static final String REMOVE_ADMIN_PRIVILEGES = "Remove Admin Privileges";
    public static final String START_SERVICE = "Start Service";
    public static final String STOP_SERVICE = "Stop Service";

    private Button adminButtonEnable;
    private Button adminButtonDisable;
    private Button startServiceButton;
    private Button stopServiceButton;

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private DevicePolicyManager devicePolicyManager;
    private ComponentName componentName;
    private Intent activeFlapServiceIntent;

    /*------------------------ METHODS REGION ------------------------*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fieldSetup();
        buttonSetup();
        onClickSetup();

        sensorManager.registerListener(this, proximitySensor,
                SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY
                && devicePolicyManager.isAdminActive(componentName)) {
            final float distance = event.values[0];

            if (distance <= PROXIMITY_THRESHOLD) {
                devicePolicyManager.lockNow();
            } else {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    ADD_ADMIN_PRIVILEGES, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void buttonSetup() {
        adminButtonEnable = findViewById(R.id.adminButtonEnable);
        adminButtonDisable = findViewById(R.id.adminButtonDisable);
        startServiceButton = findViewById(R.id.startServiceButton);
        stopServiceButton = findViewById(R.id.stopServiceButton);

        adminButtonEnable.setText(ADD_ADMIN_PRIVILEGES);
        adminButtonDisable.setText(REMOVE_ADMIN_PRIVILEGES);
        startServiceButton.setText(START_SERVICE);
        stopServiceButton.setText(STOP_SERVICE);

        boolean isAdmin = devicePolicyManager.isAdminActive(componentName);
        adminButtonEnable.setVisibility(isAdmin ? View.GONE : View.VISIBLE);
        adminButtonDisable.setVisibility(isAdmin ? View.VISIBLE : View.GONE);
    }

    private void fieldSetup() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, Admin.class);
        activeFlapServiceIntent = new Intent(MainActivity.this, ActiveFlapService.class);
    }

    private void onClickSetup() {
        adminButtonEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
                startActivityForResult(intent, RESULT_ENABLE);
                adminButtonDisable.setVisibility(View.VISIBLE);
                adminButtonEnable.setVisibility(View.GONE);
            }
        });

        adminButtonDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                devicePolicyManager.removeActiveAdmin(componentName);
                adminButtonDisable.setVisibility(View.GONE);
                adminButtonEnable.setVisibility(View.VISIBLE);
            }
        });

        startServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(activeFlapServiceIntent);
            }
        });

        stopServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(activeFlapServiceIntent);
            }
        });
    }
}
