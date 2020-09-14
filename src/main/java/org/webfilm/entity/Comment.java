package org.webfilm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

import java.beans.ConstructorProperties;

public class Comment {

    @JsonProperty
    private int id;

    @JsonProperty("video_id")
    private String videoId;

    @JsonProperty("comment_id")
    private String commentId;

    @JsonProperty("text_display")
    private String textDisplay;

    @JsonProperty("text_original")
    private String textOriginal;

    @JsonProperty("author_display_name")
    private String authorDisplayName;

    @JsonProperty("author_profile_image")
    private String authorProfileImage;

    @JsonProperty("author_channel_id")
    private String authorChannelId;

    @JsonProperty("like_count")
    private int likeCount;

    @JsonProperty("published_at")
    private String publishedAt;

    @JsonProperty("updated_at")
    private String updatedAt;

    @ConstructorProperties({"id, video_id, commentId", "text_display", "text_original", "author_display_name",
            "author_profile_image", "author_channel_id", "like_count", "published_at", "updated_at"})
    public Comment(@ColumnName("id") int id,
                   @ColumnName("video_id") String videoId,
                   @ColumnName("comment_id") String commentId,
                   @ColumnName("text_display") String textDisplay,
                   @ColumnName("text_original") String textOriginal,
                   @ColumnName("author_display_name") String authorDisplayName,
                   @ColumnName("author_profile_image") String authorProfileImage,
                   @ColumnName("author_channel_id") String authorChannelId,
                   @ColumnName("like_count") int likeCount,
                   @ColumnName("published_at") String publishedAt,
                   @ColumnName("updated_at") String updatedAt) {
        this.id = id;
        this.videoId = videoId;
        this.commentId = commentId;
        this.textDisplay = textDisplay;
        this.textOriginal = textOriginal;
        this.authorDisplayName = authorDisplayName;
        this.authorProfileImage = authorProfileImage;
        this.authorChannelId = authorChannelId;
        this.likeCount = likeCount;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
    }

    public Comment(String videoId, String commentId, String textDisplay, String textOriginal,
                   String authorDisplayName, String authorProfileImage, String authorChannelId,
                   int likeCount, String publishedAt, String updatedAt) {
        this.videoId = videoId;
        this.commentId = commentId;
        this.textDisplay = textDisplay;
        this.textOriginal = textOriginal;
        this.authorDisplayName = authorDisplayName;
        this.authorProfileImage = authorProfileImage;
        this.authorChannelId = authorChannelId;
        this.likeCount = likeCount;
        this.publishedAt = publishedAt;
        this.updatedAt = updatedAt;
    }

    public int getId() {
        return id;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getTextDisplay() {
        return textDisplay;
    }

    public String getTextOriginal() {
        return textOriginal;
    }

    public String getAuthorDisplayName() {
        return authorDisplayName;
    }

    public String getAuthorProfileImage() {
        return authorProfileImage;
    }

    public String getAuthorChannelId() {
        return authorChannelId;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

}
