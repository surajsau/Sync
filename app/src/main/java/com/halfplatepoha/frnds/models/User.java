package com.halfplatepoha.frnds.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.halfplatepoha.frnds.home.IFrndsConstants;

/**
 * Created by surajkumarsau on 08/09/16.
 */
public class User {

    private String name;
    private String fbId;
    private String imageUrl;
    private String lastMessage;
    private @IFrndsConstants.UserStatus int status;

    public User(){}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(@IFrndsConstants.UserStatus int status) {
        this.status = status;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
