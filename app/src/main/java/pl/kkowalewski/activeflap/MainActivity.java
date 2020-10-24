package pl.kkowalewski.activeflap;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /*------------------------ FIELDS REGION ------------------------*/
    public static final float PROXIMITY_THRESHOLD = 1.0f;
    public static final int RESULT_ENABLE = 1;
    public static final String ADD_ADMIN_PRIVILEGES = "Add Admin Privileges";
    public static final String REMOVE_ADMIN_PRIVILEGES = "Remove Admin Privileges";
    public static final String START_SERVICE = "Start Service";
    public static final String STOP_SERVICE = "Stop Service";
    public static final String SYSTEM_SCREEN_OFF_TIMEOUT = "Screen Off Timeout Not Found";
    public static final String EXTRA_KEY_COMPONENT_NAME = "componentName";

    private Button adminButtonEnable;
    private Button adminButtonDisable;
    private Button startServiceButton;
    private Button stopServiceButton;

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
        devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this, Admin.class);
        activeFlapServiceIntent = new Intent(MainActivity.this, ActiveFlapService.class);
        activeFlapServiceIntent.putExtra(EXTRA_KEY_COMPONENT_NAME, componentName);
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
