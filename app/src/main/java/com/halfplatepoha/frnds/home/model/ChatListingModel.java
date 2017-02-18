package com.halfplatepoha.frnds.home.model;

import com.halfplatepoha.frnds.db.models.Chat;

/**
 * Created by surajkumarsau on 14/02/17.
 */

public class ChatListingModel {

    private String frndName;
    private int frndPosition;
    private String frndId;
    private String lastMessage;
    private String frndImageUrl;
    private boolean isMessageRead;
    private long lastTimestamp;

    public ChatListingModel(Chat chat) {
        this.frndName = chat.getFrndName();
        this.frndId = chat.getFrndId();
        this.frndPosition = chat.getFrndPosition();

        if(chat.getFrndLastMessage() != null) {
            this.lastMessage = chat.getFrndLastMessage().getMsgBody();
            this.lastTimestamp = chat.getFrndLastMessage().getMsgTimestamp();
        }

        this.frndImageUrl = chat.getFrndImageUrl();
        this.isMessageRead = chat.isMsgRead();
    }

    public String getFrndName() {
        return frndName;
    }

    public void setFrndName(String frndName) {
        this.frndName = frndName;
    }

    public int getFrndPosition() {
        return frndPosition;
    }

    public void setFrndPosition(int frndPosition) {
        this.frndPosition = frndPosition;
    }

    public String getFrndId() {
        return frndId;
    }

    public void setFrndId(String frndId) {
        this.frndId = frndId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getFrndImageUrl() {
        return frndImageUrl;
    }

    public void setFrndImageUrl(String frndImageUrl) {
        this.frndImageUrl = frndImageUrl;
    }

    public boolean isMessageRead() {
        return isMessageRead;
    }

    public void setMessageRead(boolean messageRead) {
        isMessageRead = messageRead;
    }

    public long getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(long lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }
}
