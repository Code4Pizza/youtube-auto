package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class ChannelVideo {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private Integer channelId;

    @JsonProperty
    private String videoUrl;

    @JsonProperty
    private Integer duration;


    public ChannelVideo(Integer id, Integer channelId, String videoUrl, Integer duration) {
        this.id = id;
        this.channelId = channelId;
        this.videoUrl = videoUrl;
        this.duration = duration;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }
}
