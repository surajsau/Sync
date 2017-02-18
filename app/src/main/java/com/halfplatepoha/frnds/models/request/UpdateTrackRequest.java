package com.halfplatepoha.frnds.models.request;

/**
 * Created by surajkumarsau on 28/08/16.
 */
public class UpdateTrackRequest {
    private String trackId;
    private String fbId;
    private String to;

    public String getTrackId() {
        return trackId;
    }

    public void setTrackId(String trackId) {
        this.trackId = trackId;
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
