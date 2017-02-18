package com.halfplatepoha.frnds.models.request;

/**
 * Created by surajkumarsau on 28/08/16.
 */
public class RegisterRequest {
    private String fbId;
    private String name;

    public String getFbId() {
        return fbId;
    }

    public void setFbId(String fbId) {
        this.fbId = fbId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
