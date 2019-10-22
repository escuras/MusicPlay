package com.example.musicplay.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.musicplay.MainActivity;
import com.example.musicplay.domain.Audio;
import com.example.musicplay.file.StorageUtil;

import java.io.IOException;
import java.util.List;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnSeekCompleteListener, MediaPlayer.OnInfoListener,
        AudioManager.OnAudioFocusChangeListener {

    private final IBinder iBinder = new MusicPlayBinder();
    private MediaPlayer mediaPlayer = null;
    public static final String ACTION_PLAY = "com.example.musicplay.ACTION_PLAY";
    public static final String ACTION_PAUSE = "com.example.musicplay.ACTION_PAUSE";
    public static final String ACTION_PREVIOUS = "com.example.musicplay.ACTION_PREVIOUS";
    public static final String ACTION_NEXT = "com.example.musicplay.ACTION_NEXT";
    public static final String ACTION_STOP = "com.example.musicplay.ACTION_STOP";
    private int resumePosition = 0;
    private AudioManager audioManager;
    private boolean ongoingCall = false;
    private PhoneStateListener phoneStateListener;
    private TelephonyManager telephonyManager;
    private List<Audio> audioList;
    private int audioIndex = -1;
    private Audio activeAudio;

    private static final int NOTIFICATION_ID = 101;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            StorageUtil storage = new StorageUtil(getApplicationContext());
            audioList = storage.loadAudio();
            audioIndex = storage.loadAudioIndex();
            if (audioIndex != -1 &&
                    audioList.size() > 0 &&
                    audioIndex < audioList.size()) {
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
                return  super.onStartCommand(intent, flags, startId);
            }
        } catch (NullPointerException e) {
            stopSelf();
        }

        if (requestAudioFocus() == false) {
            stopSelf();
        }
        initializePlayer();
        handleExternalActions(intent);
        return super.onStartCommand(intent, flags, startId);

    }

    private void handleExternalActions(Intent intent){
        registerBecomingNoisyReceiver();
        registerPlayNewAudio();
        registerPause();
        registerPlay();
        callStateListener();
        registerForward();
        registerPrevious();
    }


    public void play() throws IOException {
        mediaPlayer.start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        playMedia();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        stopMedia();
        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            stopMedia();
            mediaPlayer.release();
        }
        if(!removeAudioFocus()) {
            return;
        }
        if (phoneStateListener != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
        unregisterReceiver(becomingNoisyReceiver);
        unregisterReceiver(playNewAudio);
        unregisterReceiver(pauseReceiver);
        unregisterReceiver(forwardReceiver);
        unregisterReceiver(previousReceiver);
        new StorageUtil(getApplicationContext()).clearCachedAudioPlaylist();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch (focusChange) {
            case AudioManager.AUDIOFOCUS_GAIN:
                if (mediaPlayer == null) {
                    initializePlayer();
                } else if (!mediaPlayer.isPlaying()) {
                    mediaPlayer.start();
                }
                mediaPlayer.setVolume(1.0f, 1.0f);
                break;
            case AudioManager.AUDIOFOCUS_LOSS:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.stop();
                }
                mediaPlayer.release();
                mediaPlayer = null;
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
                break;
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.setVolume(0.1f, 0.1f);
                }
                break;
            default:
                break;
        }
    }

    private boolean requestAudioFocus() {
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int result = audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            return true;
        }
        return false;
    }

    private boolean removeAudioFocus() {
        if(audioManager != null) {
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED ==
                    audioManager.abandonAudioFocus(this);
        }
        return false;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        switch (what) {
            case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
                Log.d("MediaPlayer error", "Argumento inválido para leitura");
                break;
            case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
                Log.d("MediaPlayer Error", "Servidor desligado");
                break;
            default:
                Log.d("MediaPlayer Error", "Erro desconhecido");
                break;
        }
        return false;
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
    }

    public class MusicPlayBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

    private void initializePlayer() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnErrorListener(this);
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnSeekCompleteListener(this);
        mediaPlayer.setOnInfoListener(this);
        mediaPlayer.reset();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mediaPlayer.setDataSource(activeAudio.getData());
        } catch (IOException e) {
            this.stopSelf();
        }
        mediaPlayer.prepareAsync();
    }

    private void playMedia() {
        if (mediaPlayer == null) {
            return;
        }
        if (!mediaPlayer.isPlaying()) {
            mediaPlayer.seekTo(resumePosition);
            mediaPlayer.start();
        }
    }

    private void stopMedia() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            resumePosition = 0;
        }
    }

    private void pauseMedia() {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            resumePosition = mediaPlayer.getCurrentPosition();
        }
    }

    private BroadcastReceiver becomingNoisyReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
            Toast.makeText(getBaseContext(), "Pausing music", Toast.LENGTH_SHORT);
        }
    };

    private void registerBecomingNoisyReceiver() {
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(becomingNoisyReceiver, intentFilter);
    }

    private void callStateListener() {
        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                    case TelephonyManager.CALL_STATE_RINGING:
                        if (mediaPlayer != null) {
                            pauseMedia();
                            ongoingCall = true;
                        }
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        if (mediaPlayer != null) {
                            if (ongoingCall) {
                                ongoingCall = false;
                                playMedia();
                            }
                        }
                        break;
                }
            }
        };
        telephonyManager.listen(phoneStateListener,
                PhoneStateListener.LISTEN_CALL_STATE);
    }

    private BroadcastReceiver playNewAudio = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            audioIndex = new StorageUtil(getApplicationContext()).loadAudioIndex();
            if (audioIndex != -1 && audioIndex < audioList.size()) {
                activeAudio = audioList.get(audioIndex);
            } else {
                stopSelf();
            }
            stopMedia();
            mediaPlayer.reset();
            initializePlayer();
        }
    };

    private BroadcastReceiver pauseReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            pauseMedia();
            Toast.makeText(getBaseContext(), "Pause", Toast.LENGTH_SHORT);
        }
    };

    private void registerPlayNewAudio() {
        IntentFilter filter = new IntentFilter(MainActivity.PLAY_NEW_AUDIO);
        registerReceiver(playNewAudio, filter);
    }

    private void registerForward() {
        IntentFilter filter = new IntentFilter(MusicService.ACTION_NEXT);
        registerReceiver(forwardReceiver, filter);
    }

    private void registerPrevious() {
        IntentFilter filter = new IntentFilter(MusicService.ACTION_PREVIOUS);
        registerReceiver(previousReceiver, filter);
    }

    private void registerPlay() {
        IntentFilter filter = new IntentFilter(MusicService.ACTION_PLAY);
        registerReceiver(playReceiver, filter);
    }

    private void registerPause() {
        IntentFilter filter = new IntentFilter(MusicService.ACTION_PAUSE);
        registerReceiver(pauseReceiver, filter);
    }

    private BroadcastReceiver playReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            playMedia();
        }
    };


    private BroadcastReceiver forwardReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (audioIndex == audioList.size() - 1) {
                audioIndex = 0;
                activeAudio = audioList.get(audioIndex);
            } else {
                activeAudio = audioList.get(++audioIndex);
            }
            new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);
            stopMedia();
            initializePlayer();
        }
    };

    private BroadcastReceiver previousReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (audioIndex == 0) {
                audioIndex = audioList.size() - 1;
                activeAudio = audioList.get(audioIndex);
            } else {
                activeAudio = audioList.get(--audioIndex);
            }
            new StorageUtil(getApplicationContext()).storeAudioIndex(audioIndex);
            stopMedia();
            initializePlayer();
        }

    };

}
