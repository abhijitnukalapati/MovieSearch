package com.diaby.moviesearch.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by abhijitnukalapati on 11/14/16.
 */
public class MConfiguration extends MBase {

    public Images images;

    @SerializedName("change_keys")
    public List<String> changeKeys = new ArrayList<String>();

    public static class Images {

        @SerializedName("base_url")
        public String baseUrl;

        @SerializedName("secure_base_url")
        public String secureBaseUrl;

        @SerializedName("backdrop_sizes")
        public List<String> backdropSizes = new ArrayList<String>();

        @SerializedName("logo_sizes")
        public List<String> logoSizes = new ArrayList<String>();

        @SerializedName("poster_sizes")
        public List<String> posterSizes = new ArrayList<String>();

        @SerializedName("profile_sizes")
        public List<String> profileSizes = new ArrayList<String>();

        @SerializedName("still_sizes")
        public List<String> stillSizes = new ArrayList<String>();

        public String getBaseUrl() {
            return baseUrl;
        }

        public void setBaseUrl(String baseUrl) {
            this.baseUrl = baseUrl;
        }

        public String getSecureBaseUrl() {
            return secureBaseUrl;
        }

        public void setSecureBaseUrl(String secureBaseUrl) {
            this.secureBaseUrl = secureBaseUrl;
        }

        public List<String> getBackdropSizes() {
            return backdropSizes;
        }

        public void setBackdropSizes(List<String> backdropSizes) {
            this.backdropSizes = backdropSizes;
        }

        public List<String> getLogoSizes() {
            return logoSizes;
        }

        public void setLogoSizes(List<String> logoSizes) {
            this.logoSizes = logoSizes;
        }

        public List<String> getPosterSizes() {
            return posterSizes;
        }

        public void setPosterSizes(List<String> posterSizes) {
            this.posterSizes = posterSizes;
        }

        public List<String> getProfileSizes() {
            return profileSizes;
        }

        public void setProfileSizes(List<String> profileSizes) {
            this.profileSizes = profileSizes;
        }

        public List<String> getStillSizes() {
            return stillSizes;
        }

        public void setStillSizes(List<String> stillSizes) {
            this.stillSizes = stillSizes;
        }
    }

    public Images getImages() {
        return images;
    }

    public void setImages(Images images) {
        this.images = images;
    }

    public List<String> getChangeKeys() {
        return changeKeys;
    }

    public void setChangeKeys(List<String> changeKeys) {
        this.changeKeys = changeKeys;
    }
}
