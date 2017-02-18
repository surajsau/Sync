package com.halfplatepoha.frnds.fcm.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Debug;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.halfplatepoha.frnds.FrndsLog;
import com.halfplatepoha.frnds.FrndsPreference;
import com.halfplatepoha.frnds.IConstants;
import com.halfplatepoha.frnds.IPrefConstants;
import com.halfplatepoha.frnds.R;
import com.halfplatepoha.frnds.db.ChatDAO;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;
import com.halfplatepoha.frnds.fcm.IFCMConstants;
import com.halfplatepoha.frnds.fcm.NotificationModel;
import com.halfplatepoha.frnds.models.response.TrackDetails;
import com.halfplatepoha.frnds.network.BaseSubscriber;
import com.halfplatepoha.frnds.network.clients.SoundCloudClient;
import com.halfplatepoha.frnds.network.servicegenerators.ClientGenerator;

import java.io.IOException;
import java.util.Map;

import io.realm.Realm;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by surajkumarsau on 26/08/16.
 */
public class NotificationService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        FrndsLog.e("From: " + remoteMessage.getFrom());
        FrndsLog.e("ChatMessage payload: " + remoteMessage.getData());
        FrndsLog.e("ChatMessage body: " +  remoteMessage.getNotification().getBody());

        Map<String, String> data = remoteMessage.getData();
        NotificationModel model = new NotificationModel();
        model.setFriendId(data.get(IFCMConstants.NOTIF_FRND_ID));
        model.setFriendName(data.get(IFCMConstants.NOTIF_FRIEND_NAME));
        model.setType(data.get(IFCMConstants.NOTIF_TYPE));
        model.setMessage(data.get(IFCMConstants.NOTIF_MESSAGE));
        model.setTrackId(data.get(IFCMConstants.NOTIF_TRACK_ID));
        model.setTrackName(data.get(IFCMConstants.NOTIF_TRACK_NAME));
        model.setTrackUrl(data.get(IFCMConstants.NOTIF_TRACK_URL));
        model.setTimestamp(data.get(IFCMConstants.NOTIF_TIMESTAMP));
        model.setTrackImageUrl(data.get(IFCMConstants.NOTIF_TRACK_IMAGE_URL));
        model.setTrackShareUrl(data.get(IFCMConstants.NOTIF_TRACK_SHARE_URL));
        constructNotification(model);

    }

    private void constructNotification(final NotificationModel model) {
        Intent chatIntent = new Intent(IConstants.CHAT_BROADCAST);
        chatIntent.putExtra(IConstants.FRND_ID, model.getFriendId());
        chatIntent.putExtra(IConstants.FRND_TIME_STAMP, model.getTimestamp());

        switch (model.getType()){
            case IFCMConstants.SONG:{
                String notificationMessage = getMessageFromModel(model);

                chatIntent.putExtra(IConstants.FRND_MESSAGE, notificationMessage);
                chatIntent.putExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MUSIC);
                chatIntent.putExtra(IConstants.FRND_TRACK_ID, model.getTrackId());
                chatIntent.putExtra(IConstants.FRND_TRACK_URL, model.getTrackUrl());
                chatIntent.putExtra(IConstants.FRND_TRACK_TITLE, model.getTrackName());
                chatIntent.putExtra(IConstants.FRND_TRACK_SHARE_URL, model.getTrackShareUrl());
                chatIntent.putExtra(IConstants.FRND_TRACK_IMAGE_URL, model.getTrackImageUrl());
            }
            break;

            case IFCMConstants.MESSAGE:{
                chatIntent.putExtra(IConstants.FRND_MESSAGE, model.getMessage());
                chatIntent.putExtra(IConstants.FRND_MESSAGE_TYPE, IDbConstants.TYPE_MESSAGE);
            }
            break;
        }

        int screenType = FrndsPreference.getIntFromPref(IPrefConstants.SCREEN_TYPE, IConstants.SCREEN_NONE);

        switch (screenType) {
            case IConstants.SCREEN_CHAT:
            case IConstants.SCREEN_LISTING:
                sendBroadcast(chatIntent);
                break;
        }
    }

    private String getMessageFromModel(NotificationModel model) {
        if(model == null)
            return null;

        return String.format(getString(R.string.music_chat_message), model.getFriendName(), model.getTrackName());
    }
}
