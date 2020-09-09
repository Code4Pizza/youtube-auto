package org.webfilm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

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

    public Video(int id, String name, String description, int duration, String bgImage, String url, int views, String youtubeId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.duration = duration;
        this.bgImage = bgImage;
        this.url = url;
        this.views = views;
        this.youtubeId = youtubeId;
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
}
