/*
 * Copyright (C) 2014 Thalmic Labs Inc.
 * Distributed under the Myo SDK license agreement. See LICENSE.txt for details.
 */

package me.carlosgonzales.ircontrol;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.thalmic.myo.AbstractDeviceListener;
import com.thalmic.myo.DeviceListener;
import com.thalmic.myo.Hub;
import com.thalmic.myo.Myo;
import com.thalmic.myo.Pose;

/**
 * Service that monitors for gestures (Pose changes) in the attached Myo device.
 * Service then calls the IRController object to process the Pose and perform the appropriate action.
 */
public class PoseMonitorService extends Service {
    private static final String TAG = "PoseMonitorService";
    private static final String MYO_LEFT = "CB:5B:75:F7:F6:87";
    private static final String MYO_RIGHT = "F0:84:90:5A:91:F5";
    private IRController mController = IRController.getInstance(this);

    //Myo0: CB:5B:75:F7:F6:87
    //Myo1: F0:84:90:5A:91:F5

    private Toast mToast;

    private DeviceListener mListener = new AbstractDeviceListener() {
        @Override
        public void onConnect(Myo myo, long time) {
            Log.d(TAG, "Attached to " + myo.getMacAddress() + " with name " + myo.getName());
            Toast.makeText(PoseMonitorService.this, getString(R.string.connected), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onDisconnect(Myo myo, long time) {
            Toast.makeText(PoseMonitorService.this, getString(R.string.disconnected), Toast.LENGTH_SHORT).show();
//            showToast("Restarting...");
//
//            Intent mStartActivity = new Intent(PoseMonitorService.this, EntryActivity.class);
//            int mPendingIntentId = 123456;
//            PendingIntent mPendingIntent = PendingIntent.getActivity(PoseMonitorService.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
//            AlarmManager mgr = (AlarmManager)PoseMonitorService.this.getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
//            stopSelf();
        }

        @Override
        public void onPose(Myo myo, long time, Pose pose) {
            mController.processPose(pose);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Hub hub = Hub.getInstance();
        if (!hub.init(this, getPackageName())) {
            Log.d(TAG, "Could not instantiate Hub.");
            stopSelf();
            return;
        }

        hub.setLockingPolicy(Hub.LockingPolicy.NONE);

        hub.addListener(mListener);
        hub.attachByMacAddress(MYO_LEFT);
        //hub.attachByMacAddress(MYO_RIGHT);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Hub hub = Hub.getInstance();
        hub.removeListener(mListener);
        hub.shutdown();
    }
}
