package com.halfplatepoha.frnds.mediaplayer;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.detail.activity.SongDetailActivity;

import java.io.IOException;

/**
 * Created by surajkumarsau on 09/09/16.
 */
public class PlayerService extends Service implements MediaPlayer.OnPreparedListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener{

    private MediaPlayer mPlayer;
    public static final String ACTION_PLAY = "com.halfplatepoha.frnds.PLAY";
    public static final String ACTION_STOP = "com.halfplatepoha.frnds.STOP";
    private String mStreamUrl;

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mPlayer.start();
        Intent songStatusIntent = new Intent(IConstants.SONG_STATUS_BROADCAST);
        songStatusIntent.putExtra(IDetailsConstants.CURRENT_SONG_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_PLAYING);
        sendBroadcast(songStatusIntent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        FrndsLog.e(intent.getAction());
        FrndsLog.e(intent.getStringExtra(IDetailsConstants.SERVICE_STREAM_URL));
        try {
            if (intent.getAction().equals(ACTION_PLAY)) {
                if(mPlayer == null) {
                    mPlayer = new MediaPlayer();
                    mPlayer.setOnPreparedListener(this);
                    mPlayer.setOnErrorListener(this);
                    mPlayer.setOnCompletionListener(this);
                }

                mStreamUrl = intent.getStringExtra(IDetailsConstants.SERVICE_STREAM_URL);
                String title = intent.getStringExtra(IDetailsConstants.NOTIFICATION_SERVICE_TRACK_TITLE);
                String frndId = intent.getStringExtra(IDetailsConstants.FRND_ID);

                if(!TextUtils.isEmpty(mStreamUrl)) {
                    mStreamUrl = mStreamUrl.concat("?" + IConstants.API_KEY_PARAM + "=" + IConstants.API_KEY_VALUE);

                    mPlayer.reset();
                    mPlayer.setDataSource(mStreamUrl);
                    mPlayer.prepareAsync();

                    Intent stopPlay = new Intent(this, PlayerService.class);
                    stopPlay.setAction(PlayerService.ACTION_STOP);



                    NotificationCompat.Builder playNotificationBuilder = new NotificationCompat.Builder(this)
                            .setContentTitle("Sync.")
                            .setStyle(new NotificationCompat.BigTextStyle().bigText("You\'re listening to " + title))
                            .setSmallIcon(R.mipmap.ic_launcher);

                    if(!TextUtils.isEmpty(frndId)) {
                        Intent detailIntent = new Intent(this, SongDetailActivity.class);
                        detailIntent.putExtra(IDetailsConstants.FRND_ID, frndId);
                        PendingIntent songDetailsIntent = PendingIntent.getActivity(this, IConstants.NOTIFICATION_PLAY_PENDING_INTENT_REQUEST,
                                detailIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT);

                        playNotificationBuilder.addAction(R.drawable.profile_icon, "Profile", songDetailsIntent);
                    }

                    PendingIntent stopPlayService = PendingIntent.getService(this, IConstants.NOTIFICATION_STOP_PLAYING_INTENT_REQUEST,
                            stopPlay, PendingIntent.FLAG_UPDATE_CURRENT);
                    playNotificationBuilder.addAction(R.drawable.stop, "Stop", stopPlayService);

                    startForeground(IConstants.PLAY_NOTIFICATION_ID, playNotificationBuilder.build());
                }
            } else if(intent.getAction().equals(ACTION_STOP)) {
                if(mPlayer != null) {
                    mPlayer.stop();
                    mPlayer.release();
                }
                Intent songStatusIntent = new Intent(IConstants.SONG_STATUS_BROADCAST);
                songStatusIntent.putExtra(IDetailsConstants.CURRENT_SONG_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_STOP);
                sendBroadcast(songStatusIntent);
                stopSelf();
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        try{
            mediaPlayer.reset();
            mediaPlayer.setDataSource(mStreamUrl);
            mediaPlayer.prepareAsync();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        mediaPlayer.stop();
        mediaPlayer.release();

        Intent songStatusIntent = new Intent(IConstants.SONG_STATUS_BROADCAST);
        songStatusIntent.putExtra(IDetailsConstants.CURRENT_SONG_STATUS, IDetailsConstants.CURRENT_SONG_STATUS_STOP);
        sendBroadcast(songStatusIntent);
        stopSelf();
    }
}
