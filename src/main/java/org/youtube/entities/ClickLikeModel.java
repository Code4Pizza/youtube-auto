package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class ClickLikeModel {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String videoUrl;

    @JsonProperty
    private Date clickTime;


    public ClickLikeModel() {
    }

    public ClickLikeModel(Integer id, String userId, String videoUrl, Date clickTime) {
        this.id = id;
        this.userId = userId;
        this.videoUrl = videoUrl;
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
}
