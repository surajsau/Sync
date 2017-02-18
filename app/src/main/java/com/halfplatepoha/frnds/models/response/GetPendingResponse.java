package com.halfplatepoha.frnds.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by surajkumarsau on 14/02/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class GetPendingResponse implements Serializable {

    private ArrayList<PendingMessage> pendingMessages;
    private ArrayList<PendingSong> pendingSongs;

    public ArrayList<PendingMessage> getPendingMessages() {
        return pendingMessages;
    }

    public void setPendingMessages(ArrayList<PendingMessage> pendingMessages) {
        this.pendingMessages = pendingMessages;
    }

    public ArrayList<PendingSong> getPendingSongs() {
        return pendingSongs;
    }

    public void setPendingSongs(ArrayList<PendingSong> pendingSongs) {
        this.pendingSongs = pendingSongs;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PendingMessage implements Serializable {
        private String fbId;
        private ArrayList<Message> messages;

        public String getFbId() {
            return fbId;
        }

        public void setFbId(String fbId) {
            this.fbId = fbId;
        }

        public ArrayList<Message> getMessages() {
            return messages;
        }

        public void setMessages(ArrayList<Message> messages) {
            this.messages = messages;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Message implements Serializable {
            private String message;
            private long timestamp;

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PendingSong implements Serializable {
        private String fbId;
        private ArrayList<Track> tracks;

        public String getFbId() {
            return fbId;
        }

        public void setFbId(String fbId) {
            this.fbId = fbId;
        }

        public ArrayList<Track> getTracks() {
            return tracks;
        }

        public void setTracks(ArrayList<Track> tracks) {
            this.tracks = tracks;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Track implements Serializable {
            private String trackId;
            private long timestamp;

            public String getTrackId() {
                return trackId;
            }

            public void setTrackId(String trackId) {
                this.trackId = trackId;
            }

            public long getTimestamp() {
                return timestamp;
            }

            public void setTimestamp(long timestamp) {
                this.timestamp = timestamp;
            }
        }
    }
}
