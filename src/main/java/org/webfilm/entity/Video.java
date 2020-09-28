package org.webfilm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.beans.ConstructorProperties;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

public class Video {

    @JsonProperty
    private int id;

    @JsonProperty
    private String name;

    @JsonProperty
    @ColumnName("url_preview")
    private String urlPreview;

    @JsonProperty
    @ColumnName("publish_time")
    private Timestamp publishTime;

    @JsonProperty
    private List<String> tags;

    @JsonProperty
    private String description;

    @JsonProperty
    private int duration;

    @JsonProperty("bg_image")
    private String bgImage;

    @JsonProperty
    private String url;

    @JsonProperty
    private int views;

    @JsonProperty("youtube_id")
    private String youtubeId;

    @JsonProperty("channel_id")
    private int channelId;

    public Video() {
    }

    public Video(String name, String description,
                 Timestamp publishTime,
                 int duration, String bgImage, String url, int views,
                 String youtubeId) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.bgImage = bgImage;
        this.url = url;
        this.views = views;
        this.youtubeId = youtubeId;
        this.publishTime = publishTime;
    }

    @ConstructorProperties({"id, name, description, duration, bg_image, url, views, youtube_id, channel_id, url_preview"})
    public Video(@ColumnName(("id")) int id,
                 @ColumnName(("name")) String name,
                 @ColumnName(("description")) String description,
                 @ColumnName(("duration")) int duration,
                 @ColumnName(("bg_image")) String bgImage,
                 @ColumnName(("url")) String url,
                 @ColumnName(("views")) int views,
                 @ColumnName(("youtube_id")) String youtubeId,
                 @ColumnName(("channel_id")) int channelId,
                 @ColumnName(("url_preview")) String urlPreview) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.bgImage = bgImage;
        this.url = url;
        this.views = views;
        this.youtubeId = youtubeId;
        this.channelId = channelId;
        this.urlPreview = urlPreview;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPublishTime(Timestamp publishTime) {
        this.publishTime = publishTime;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setBgImage(String bgImage) {
        this.bgImage = bgImage;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getDuration() {
        return duration;
    }

    @ColumnName("bg_image")
    public String getBgImage() {
        return bgImage;
    }

    public String getUrl() {
        return url;
    }

    public int getViews() {
        return views;
    }

    @ColumnName("youtube_id")
    public String getYoutubeId() {
        return youtubeId;
    }

    @ColumnName("channel_id")
    public int getChannelId() {
        return channelId;
    }

    public void setChannelId(int channelId) {
        this.channelId = channelId;
    }

    public Timestamp getPublishTime() {
        return publishTime;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public String getUrlPreview() {
        return urlPreview;
    }

    public void setUrlPreview(String urlPreview) {
        this.urlPreview = urlPreview;
    }

    @Override
    public String toString() {
        return "Video{" +
                ", name='" + name + '\'' +
                ", duration=" + duration +
                ", views=" + views +
                ", youtubeId='" + youtubeId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return youtubeId.equals(video.youtubeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(youtubeId);
    }
}
