package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class ClickAdsModel {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String videoUrl;

    @JsonProperty
    private String adsUrl;

    @JsonProperty
    private Date clickTime;


    public ClickAdsModel() {
    }

    public ClickAdsModel(Integer id, String userId,
                         String videoUrl, String adsUrl, Date clickTime) {
        this.id = id;
        this.userId = userId;
        this.videoUrl = videoUrl;
        this.adsUrl = adsUrl;
        this.clickTime = clickTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Date getClickTime() {
        return clickTime;
    }

    public void setClickTime(Date clickTime) {
        this.clickTime = clickTime;
    }

    public String getAdsUrl() {
        return adsUrl;
    }

    public void setAdsUrl(String adsUrl) {
        this.adsUrl = adsUrl;
    }
}
