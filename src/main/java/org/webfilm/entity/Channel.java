package org.webfilm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.beans.ConstructorProperties;

public class Channel {

    @JsonProperty
    private int id;

    @JsonProperty
    private String name;

    @JsonProperty("youtube_url")
    private String youtubeUrl;

    @JsonProperty
    private String youtubeId;

    @JsonProperty
    private String description;

    @JsonProperty
    private String avatar;

    @JsonProperty
    private int subscribers;

    public Channel() {
    }

    @ConstructorProperties({"id, name, youtube_id"})
    public Channel(@ColumnName("id") int id, @ColumnName("name") String name, @ColumnName("youtube_id") String youtubeId) {
        this.id = id;
        this.name = name;
        this.youtubeId = youtubeId;
    }

    public Channel(String name, String youtubeUrl, @ColumnName("youtube_id") String youtubeId, String description, String avatar, int subscribers) {
        this.name = name;
        this.youtubeUrl = youtubeUrl;
        this.youtubeId = youtubeId;
        this.description = description;
        this.avatar = avatar;
        this.subscribers = subscribers;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getYoutubeUrl() {
        return youtubeUrl;
    }

    public String getYoutubeId() {
        return youtubeId;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatar() {
        return avatar;
    }

    public int getSubscribers() {
        return subscribers;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setYoutubeUrl(String youtubeUrl) {
        this.youtubeUrl = youtubeUrl;
    }

    public void setYoutubeId(String youtubeId) {
        this.youtubeId = youtubeId;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setSubscribers(int subscribers) {
        this.subscribers = subscribers;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", youtubeId='" + youtubeId + '\'' +
                '}';
    }
}
