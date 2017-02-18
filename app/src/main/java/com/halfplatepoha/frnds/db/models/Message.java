package com.halfplatepoha.frnds.db.models;

import com.halfplatepoha.frnds.db.*;
import com.halfplatepoha.frnds.db.IDbConstants;
import com.halfplatepoha.frnds.detail.IDetailsConstants;

import io.realm.RealmObject;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;

/**
 * Created by surajkumarsau on 21/09/16.
 */
public class Message extends RealmObject {

    private String      msgBody;
    private int         userType;
    private long        msgTimestamp;
    private int         msgType;
    private String      frndId;
    private String      msgTrackUrl;

    public String getMsgBody() {
        return msgBody;
    }

    public void setMsgBody(String msgBody) {
        this.msgBody = msgBody;
    }

    public @IDetailsConstants.UserType int getUserType() {
        return userType;
    }

    public void setUserType(@IDetailsConstants.UserType int userType) {
        this.userType = userType;
    }

    public long getMsgTimestamp() {
        return msgTimestamp;
    }

    public void setMsgTimestamp(long msgTimestamp) {
        this.msgTimestamp = msgTimestamp;
    }

    public String getFrndId() {
        return frndId;
    }

    public void setFrndId(String frndId) {
        this.frndId = frndId;
    }

    public @IDbConstants.MessageType int getMsgType() {
        return msgType;
    }

    public void setMsgType(@IDbConstants.MessageType int msgType) {
        this.msgType = msgType;
    }

    public String getMsgTrackUrl() {
        return msgTrackUrl;
    }

    public void setMsgTrackUrl(String msgTrackUrl) {
        this.msgTrackUrl = msgTrackUrl;
    }

    public static class Builder {
        private String      msgBody;
        private int         userType;
        private long        msgTimestamp;
        private int         msgType;
        private String      msgTrackUrl;
        private String      frndId;

        public Builder setMsgBody(String msgBody) {
            this.msgBody = msgBody;
            return this;
        }

        public Builder setUserType(@IDetailsConstants.UserType int userType) {
            this.userType = userType;
            return this;
        }

        public Builder setMsgTimestamp(long msgTimestamp) {
            this.msgTimestamp = msgTimestamp;
            return this;
        }

        public Builder setMsgType(@IDbConstants.MessageType int msgType) {
            this.msgType = msgType;
            return this;
        }

        public Builder setMsgTrackUrl(String msgTrackUrl) {
            this.msgTrackUrl = msgTrackUrl;
            return this;
        }

        public Builder setFrndId(String frndId) {
            this.frndId = frndId;
            return this;
        }

        public Message build() {
            Message message = new Message();
            message.setMsgType(msgType);
            message.setUserType(userType);
            message.setMsgBody(msgBody);
            message.setMsgTimestamp(msgTimestamp);
            message.setMsgTrackUrl(msgTrackUrl);
            message.setFrndId(frndId);

            return message;
        }
    }
}
