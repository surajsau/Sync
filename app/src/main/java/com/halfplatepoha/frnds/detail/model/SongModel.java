package com.halfplatepoha.frnds.detail.model;

import com.halfplatepoha.frnds.db.models.Song;
import com.halfplatepoha.frnds.detail.IDetailsConstants;

/**
 * Created by surajkumarsau on 14/02/17.
 */

public class SongModel {
    private String trackUrl;
    private String trackShareUrl;
    private String trackName;
    private long trackTimeStamp;
    private String trackUser;
    private String trackImageUrl;
    private String frndName;

    public String getTrackUrl() {
        return trackUrl;
    }

    public void setTrackUrl(String trackUrl) {
        this.trackUrl = trackUrl;
    }

    public String getTrackName() {
        return trackName;
    }

    public void setTrackName(String trackName) {
        this.trackName = trackName;
    }

    public long getTrackTimeStamp() {
        return trackTimeStamp;
    }

    public void setTrackTimeStamp(long trackTimeStamp) {
        this.trackTimeStamp = trackTimeStamp;
    }

    public String getTrackUser() {
        return trackUser;
    }

    public void setTrackUser(String trackUser) {
        this.trackUser = trackUser;
    }

    public String getTrackImageUrl() {
        return trackImageUrl;
    }

    public void setTrackImageUrl(String trackImageUrl) {
        this.trackImageUrl = trackImageUrl;
    }

    public String getFrndName() {
        return frndName;
    }

    public void setFrndName(String frndName) {
        this.frndName = frndName;
    }

    public String getTrackShareUrl() {
        return trackShareUrl;
    }

    public void setTrackShareUrl(String trackShareUrl) {
        this.trackShareUrl = trackShareUrl;
    }
}