package com.halfplatepoha.frnds.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by surajkumarsau on 25/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrackDetails {
    private long id;
    private String stream_url;
    private String title;
    private String artwork_url;
    private User user;
    private String permalink_url;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getStream_url() {
        return stream_url;
    }

    public void setStream_url(String stream_url) {
        this.stream_url = stream_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtwork_url() {
        return artwork_url;
    }

    public void setArtwork_url(String artwork_url) {
        this.artwork_url = artwork_url;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getPermalink_url() {
        return permalink_url;
    }

    public void setPermalink_url(String permalink_url) {
        this.permalink_url = permalink_url;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class User {
        private String username;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}