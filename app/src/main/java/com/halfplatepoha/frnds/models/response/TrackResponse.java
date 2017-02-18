package com.halfplatepoha.frnds.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by surajkumarsau on 24/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackResponse {

    private String stream_url;
    private long duration;
    private String artwork_url;

    public String getStream_url() {
        return stream_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getArtwork_url() {
        return artwork_url;
    }

    public void setArtwork_url(String artwork_url) {
        this.artwork_url = artwork_url;
    }
}
