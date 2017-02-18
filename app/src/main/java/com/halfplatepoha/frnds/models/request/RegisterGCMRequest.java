package com.halfplatepoha.frnds.models.request;

/**
 * Created by surajkumarsau on 28/08/16.
 */
public class RegisterGCMRequest {
    private String fbId;
    private String deviceId;

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
}
