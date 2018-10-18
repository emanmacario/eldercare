package au.edu.unimelb.eldercare.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.VibrationEffect;
import android.os.Vibrator;

public class AudioPlayer {

    private static final String TAG = "AudioPlayer";

    private Context mContext;
    private MediaPlayer mPlayer;
    private Vibrator mVibrator;

    public AudioPlayer(Context context) {
        this.mContext = context;
    }

    public void playRingtone() {
        AudioManager audioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        assert(audioManager != null);

        // Check if silent mode or vibrate is on
        switch (audioManager.getRingerMode()) {
            case AudioManager.RINGER_MODE_SILENT:
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                mVibrator = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                long[] vibratePattern = {0, 400, 1000, 600, 2000, 800, 3000, 1000};
                mVibrator.vibrate(VibrationEffect.createWaveform(vibratePattern, 2));
                break;
            case AudioManager.RINGER_MODE_NORMAL:
                Uri ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                mPlayer = MediaPlayer.create(mContext, ringtoneUri);
                mPlayer.start();
        }
    }

    public void stopRingtone() {
        if (mPlayer != null) {
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
        if (mVibrator != null) {
            mVibrator.cancel();
            mVibrator = null;
        }
    }
}
