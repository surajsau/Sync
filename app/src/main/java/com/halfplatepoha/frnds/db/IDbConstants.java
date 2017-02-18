package com.halfplatepoha.frnds.db;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by surajkumarsau on 21/09/16.
 */
public interface IDbConstants {

    String SONG_ID_KEY = "songId";
    String FRND_ID_KEY = "frndId";
    String MSG_TIME_STAMP_KEY = "msgTimestamp";
    String SONG_TIME_STAMP_KEY = "songTimestamp";
    String USER_FB_ID_KEY = "userFbId";

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({TYPE_MESSAGE, TYPE_MUSIC})
    public @interface MessageType{};

    int TYPE_MUSIC = 1;
    int TYPE_MESSAGE = 2;

    int UPDATE_FRND_LIST_TRANSACTION_ID = 101;
    int UPDATE_MESSAGE_TRANSACTION_ID = 102;
}
