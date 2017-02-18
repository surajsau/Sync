package com.halfplatepoha.frnds.models.fb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by surajkumarsau on 13/02/17.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class UserProfile implements Serializable {

    private String name;
    private Picture picture;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Picture {

        private Data data;

        public Data getData() {
            return data;
        }

        public void setData(Data data) {
            this.data = data;
        }

        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Data implements Serializable {
            private boolean is_silhouette;
            private String url;

            public boolean is_silhouette() {
                return is_silhouette;
            }

            public void setIs_silhouette(boolean is_silhouette) {
                this.is_silhouette = is_silhouette;
            }

            public String getUrl() {
                return url;
            }

            public void setUrl(String url) {
                this.url = url;
            }
        }
    }
}
