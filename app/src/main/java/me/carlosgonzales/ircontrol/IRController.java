package me.carlosgonzales.ircontrol;

import android.content.Context;
import android.hardware.ConsumerIrManager;

import com.thalmic.myo.Pose;

/**
 * Created by Carlos on 1/17/2015.
 */

/**
 * Class responsible for sending IR signals.
 * The specific set of on/off pulse lengths for the various drone actions were found using an Arduino and an IR Receiver
 */
public class IRController {
    private static IRController sInstance;
    private static int FREQ = 38000;
    private Context mContext;
    private ConsumerIrManager mManager;

    private IRController(Context c){
        mManager = c.getSystemService(Context.CONSUMER_IR_SERVICE);
        mContext = c;
    }

    public static IRController getInstance(Context c){
        if(sInstance == null)
            sInstance = new IRController(c);

        return sInstance;
    }

    /**
     * Transmits the signal to the IR device using the Android IR API
     * @param pattern pattern of on/off IR pulses to send in milliseconds
     */
    private void sendSignal(int[] pattern){
        if(!mManager.hasIrEmitter())
            return;

        // Hacky fix to send the right pulse rate with the Android IR API
        double scaleDownFactor = 1000000.0 / FREQ;
        for (int i = 0; i < pattern.length; i++){
            pattern[i] = (int)(pattern[i] / scaleDownFactor);
        }

        mManager.transmit(FREQ, pattern);
    }

    public void processPose(Pose p){
        if(p = Pose.FINGERS_SPREAD){
            up();
        }else if(p == POSE.FIST){
            down();
        }else if(p == POSE.WAVE_IN){
            turn();
        }
    }

    private void up(){
        int[] patt = {280,260,280,260,280,
                260,280,260,280,
                260,280,260,280,
                280,260,280,260};
        sendSignal(patt);
    }

    private void down(){
        int[] patt = {220, 140, 240, 140,
                220, 140, 240, 140,
                220, 140, 240, 140,
                220, 140, 240, 140};
        sendSignal(patt);
    }

    private void turn(){
        int[] patt = {370, 190, 180, 260,
                370, 190, 180, 260,
                370, 190, 180, 260,
                370, 190, 180, 260};
        sendSignal(patt);
    }

}
