package com.halfplatepoha.frnds.models.fb;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;

/**
 * Created by surajkumarsau on 24/09/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class InstalledFrnds {

    private ArrayList<Frnd> data;

    public ArrayList<Frnd> getData() {
        return data;
    }

    public void setData(ArrayList<Frnd> data) {
        this.data = data;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Frnd {
        private String id;
        private String name;
        private boolean installed;
        private Picture picture;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public boolean isInstalled() {
            return installed;
        }

        public void setInstalled(boolean installed) {
            this.installed = installed;
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
            public static class Data {
                private String url;

                public String getUrl() {
                    return url;
                }

                public void setUrl(String url) {
                    this.url = url;
                }
            }
        }
    }
}
