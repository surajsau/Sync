package com.halfplatepoha.frnds.db.models;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by surajkumarsau on 21/09/16.
 */
public class User extends RealmObject {

    @PrimaryKey
    private String      userFbId;

    private String      userName;
    private RealmList<Song>     userFavSongs;
    private RealmList<Chat>     userChats;

    public String getUserFbId() {
        return userFbId;
    }

    public void setUserFbId(String userFbId) {
        this.userFbId = userFbId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public RealmList<Song> getUserFavSongs() {
        return userFavSongs;
    }

    public void setUserFavSongs(RealmList<Song> userFavSongs) {
        this.userFavSongs = userFavSongs;
    }

    public RealmList<Chat> getUserChats() {
        return userChats;
    }

    public void setUserChats(RealmList<Chat> userChats) {
        this.userChats = userChats;
    }
}
