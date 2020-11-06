package org.webfilm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.beans.ConstructorProperties;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;
@Data
@NoArgsConstructor
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

    @JsonProperty
    private String channelUID;

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
