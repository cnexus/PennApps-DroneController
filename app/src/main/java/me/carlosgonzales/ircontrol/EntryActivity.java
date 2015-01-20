
package me.carlosgonzales.ircontrol;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class EntryActivity extends Activity {
    private static int TEMP = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the BackgroundService to receive and handle Myo events.
        //Intent intent = new Intent(this, ScanActivity.class);
        //startActivity(intent);
        startService(new Intent(this, PoseMonitorService.class));
        // Close this activity since BackgroundService will run in the background.
        finish();
    }
}
