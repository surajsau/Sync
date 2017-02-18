package com.halfplatepoha.frnds.home;

import android.support.annotation.DrawableRes;
import android.support.annotation.IntDef;

import com.halfplatepoha.frnds.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by surajkumarsau on 08/09/16.
 */
public interface IFrndsConstants {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_PLAYING, STATUS_TEXTING})
    @interface UserStatus{}

    int STATUS_PLAYING = 1;
    int STATUS_TEXTING = 2;

    String FRIEND_AVATAR_TRANSITION = "friend_avatar_transition";
    String FRIEND_NAME_TRANSITION = "friend_name_transition";

    String HOME_FRAGMENT_TAG = "home_fragment_tag";

    String DETAIL_DIALOG_TAG = "detail_dialog_tag";

    int FRIEND_LIST_REQUEST = 201;

    String FRIEND_IMAGE_URL = "friend_image_url";
    String FRIEND_NAME = "friend_name";
    String FRIEND_ID = "friend_id";

    @DrawableRes int[] tabDrawables = {R.drawable.chat, R.drawable.soundcloud, R.drawable.playlist};
    @DrawableRes int[] tabSelectedDrawables = {R.drawable.chat_selected, R.drawable.soundcloud_selected, R.drawable.playlist_selected};
}
