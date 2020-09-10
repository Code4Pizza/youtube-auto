package org.webfilm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.beans.ConstructorProperties;
import java.util.Objects;

public class Video {

    @JsonProperty
    private int id;

    @JsonProperty
    private String name;

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

    public Video(String name, String description, int duration, String bgImage, String url, int views, String youtubeId) {
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.bgImage = bgImage;
        this.url = url;
        this.views = views;
        this.youtubeId = youtubeId;
    }

    @ConstructorProperties({"id, name, description, duration, bg_image, url, views, youtube_id, channel_id"})
    public Video(@ColumnName(("id")) int id,
                 @ColumnName(("name")) String name,
                 @ColumnName(("description")) String description,
                 @ColumnName(("duration")) int duration,
                 @ColumnName(("bg_image")) String bgImage,
                 @ColumnName(("url")) String url,
                 @ColumnName(("views")) int views,
                 @ColumnName(("youtube_id")) String youtubeId,
                 @ColumnName(("channel_id")) int channelId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.bgImage = bgImage;
        this.url = url;
        this.views = views;
        this.youtubeId = youtubeId;
        this.channelId = channelId;
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

    @Override
    public String toString() {
        return "Video{" +
//                "id=" + id +
                ", name='" + name + '\'' +
                ", duration=" + duration +
//                ", bgImage='" + bgImage + '\'' +
//                ", url='" + url + '\'' +
                ", views=" + views +
                ", youtubeId='" + youtubeId + '\'' +
//                ", description='" + description + '\'' +
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
