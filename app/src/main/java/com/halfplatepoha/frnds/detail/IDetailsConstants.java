package com.halfplatepoha.frnds.detail;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static com.halfplatepoha.frnds.detail.IDetailsConstants.CURRENT_SONG_STATUS_PLAYING;
import static com.halfplatepoha.frnds.detail.IDetailsConstants.CURRENT_SONG_STATUS_STOP;

/**
 * Created by surajkumarsau on 07/09/16.
 */
public interface IDetailsConstants {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_ME, TYPE_FRND})
    @interface UserType {}

    int TYPE_ME = 1;
    int TYPE_FRND = 2;


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({CURRENT_SONG_STATUS_PLAYING, CURRENT_SONG_STATUS_STOP})
    @interface CurrentSongStatusType {}

    int CURRENT_SONG_STATUS_PLAYING = 3;
    int CURRENT_SONG_STATUS_STOP = 4;

    String CURRENT_SONG_STATUS = "current_song_status";

    String SOURCE_TYPE = "source_type";
    String SOURCE_FAB = "source_fab";
    String SOURCE_NOTIFICATION = "source_notification";

    int SONG_DETAILS_REQUEST = 101;

    String TRACK_ID = "track_id";
    String TRACK_URL = "track_url";
    String TRACK_IMAGE_URL = "track_image_url";
    String TRACK_SHARE_URL = "track_share_url";
    String TRACK_TITLE = "track_title";
    String TRACK_ARTIST = "track_artist";
    String FRND_ID = "friendId";

    String LATEST_IMAGE_URL = "latest_image_url";
    String LATEST_IMAGE_TRACK = "latest_image_track";
    String LATEST_FRIEND_NAME = "latest_friend_name";
    String LATEST_MESSAGE = "latest_message";
    String LATEST_CLICKED_POSITION = "latest_clicked_position";
    String LATEST_MESSAGE_TYPE = "latest_message_type";
    String LATEST_USER_TYPE = "latest_user_type";
    String LATEST_MESSAGE_TIMESTAMP = "latest_message_time_stamp";

    String SERVICE_STREAM_URL = "service_stream_url";
    String NOTIFICATION_SERVICE_TRACK_TITLE = "notification_track_title";

    String SONG_SHARE_TAG = "song_share";

    String IMG_LARGE_SUFFIX = "large";
    String IMG_500_X_500_SUFFIX = "t500x500";
    String IMG_300_X_300_SUFFIX = "t300x300";
    String IMG_BADGE = "badge";
    String STRING_HTTPS = "https";
    String STRING_HTTP = "http";
}
