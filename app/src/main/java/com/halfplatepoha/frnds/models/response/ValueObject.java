package com.halfplatepoha.frnds.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by surajkumarsau on 28/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ValueObject {
    private boolean successful;
    private String message;
    private String error;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
