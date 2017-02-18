package com.halfplatepoha.frnds.detail.model;

import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.db.models.Message;
import com.halfplatepoha.frnds.detail.IDetailsConstants;

/**
 * Created by surajkumarsau on 14/02/17.
 */

public class MessageModel {
    private String message;
    private long messageTimeStamp;
    private @IDetailsConstants.UserType int userType;
    private @IDbConstants.MessageType int messageType;
    private String messageTrackUrl;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getMessageTimeStamp() {
        return messageTimeStamp;
    }

    public void setMessageTimeStamp(long messageTimeStamp) {
        this.messageTimeStamp = messageTimeStamp;
    }

    public @IDetailsConstants.UserType int getUserType() {
        return userType;
    }

    public void setUserType(@IDetailsConstants.UserType int userType) {
        this.userType = userType;
    }

    public @IDbConstants.MessageType int getMessageType() {
        return messageType;
    }

    public void setMessageType(@IDbConstants.MessageType int messageType) {
        this.messageType = messageType;
    }

    public String getMessageTrackUrl() {
        return messageTrackUrl;
    }

    public void setMessageTrackUrl(String messageTrackUrl) {
        this.messageTrackUrl = messageTrackUrl;
    }
}
