package com.halfplatepoha.frnds.models.request;

/**
 * Created by surajkumarsau on 27/09/16.
 */
public class SendMessageRequest {
    private String message;
    private String fbId;
    private String to;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
