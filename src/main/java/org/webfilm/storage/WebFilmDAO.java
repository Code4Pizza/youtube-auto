package org.webfilm.storage;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.*;
import org.webfilm.entity.Channel;
import org.webfilm.entity.Comment;
import org.webfilm.entity.Config;
import org.webfilm.entity.Video;

import java.util.List;

public interface WebFilmDAO {

    @SqlQuery("SELECT * FROM sys_config")
    @RegisterConstructorMapper(Config.class)
    List<Config> getSystemConfigs();

    @SqlQuery("SELECT * FROM channels")
    @RegisterConstructorMapper(Channel.class)
    List<Channel> getChannels();

    @SqlQuery("SELECT * FROM videos WHERE channel_id=:channel_id")
    @RegisterConstructorMapper(Video.class)
    List<Video> getVideos(@Bind("channel_id") int channelId);

    @SqlQuery("SELECT * FROM videos WHERE channel_id=:channel_id AND youtube_id=:youtube_id")
    @RegisterConstructorMapper(Video.class)
    Video getVideoByYoutubeId(@Bind("channel_id") int channelId, @Bind("youtube_id") String youtubeId);

    @SqlQuery("SELECT * FROM comments WHERE video_id=:video_id AND comment_id=:comment_id")
    @RegisterConstructorMapper(Comment.class)
    Comment getCommentById(@Bind("video_id") String videoId, @Bind("comment_id") String commentId);

    @SqlUpdate("UPDATE videos SET `name`=:name, description=:description, duration=:duration, url=:url, views=:views, bg_image=:bg_image, channel_id=:channel_id " +
            "WHERE youtube_id=:youtube_id")
    int updateVideo(@Bind("name") String name, @Bind("description") String description, @Bind("duration") int duration,
                    @Bind("url") String url, @Bind("views") int views, @Bind("bg_image") String bgImage,
                    @Bind("youtube_id") String youtubeId, @Bind("channel_id") int channelId);

    @SqlUpdate("INSERT INTO videos(`name`, description, duration, url, views, bg_image, youtube_id, channel_id) " +
            "VALUES(:name, :description, :duration, :url, :views, :bgImage, :youtubeId, :channelId)")
    @GetGeneratedKeys
    int insertVideo(@BindBean Video video);

    @SqlUpdate("INSERT INTO video_channel_mapping(video_id, channel_id) VALUES(:video_id, :channel_id)")
    int insertVideoChannelMapping(@Bind("video_id") int videoId, @Bind("channel_id") int channelId);

    @SqlUpdate("UPDATE video_channel_mapping SET video_id=:video_id WHERE channel_id=:channel_id")
    int updateVideoChannelMapping(@Bind("video_id") int videoId, @Bind("channel_id") int channelId);

    @SqlUpdate("UPDATE channels SET `name`=:name, description=:description, avatar=:avatar, subscribers=:subscribers WHERE youtube_id=:youtube_id")
    int updateChannelInfo(@Bind("name") String name, @Bind("description") String description, @Bind("avatar") String avatar,
                          @Bind("subscribers") int subscribers, @Bind("youtube_id") String youtubeId);

    @SqlBatch("UPDATE channels SET `name`=:name, description=:description, avatar=:avatar, subscribers=:subscribers WHERE youtube_id=:youtube_id")
    @BatchChunkSize(1000)
    void bulkUpdateChannelInfo(@Bind("name") List<String> names, @Bind("description") List<String> descriptions, @Bind("avatar") List<String> avatars,
                               @Bind("subscribers") List<Integer> subscribers, @Bind("youtube_id") List<String> youtubeIds);


    @SqlUpdate("UPDATE channels SET updated_time=:updated_time WHERE youtube_id=:youtube_id")
    int updateChannelUpdatedTime(@Bind("updated_time") String updatedTime, @Bind("youtube_id") String youtubeId);

    @SqlBatch("INSERT INTO comments(video_id, comment_id, text_display, text_original, author_display_name, author_profile_image, author_channel_id, like_count, published_at, updated_at) " +
            "VALUES(:video_id, :comment_id, :text_display, :text_original, :author_display_name, :author_profile_image, :author_channel_id, :like_count, :published_at, :updated_at)")
    @GetGeneratedKeys("id")
    @BatchChunkSize(1000)
    int[] bulkInsertComments(@Bind("video_id") String videoId, @Bind("comment_id") List<String> commentIds,
                             @Bind("text_display") List<String> textDisplays, @Bind("text_original") List<String> textOriginals,
                             @Bind("author_display_name") List<String> authorDisplayNames, @Bind("author_profile_image") List<String> authorProfileImages,
                             @Bind("author_channel_id") List<String> authorChannelIds, @Bind("like_count") List<Integer> likeCounts,
                             @Bind("published_at") List<String> publishedAts, @Bind("updated_at") List<String> updatedAts);

    @SqlBatch("UPDATE comments SET text_display=:text_display, text_original=:text_original, author_display_name=:author_display_name, " +
            "author_profile_image=:author_profile_image, like_count=:like_count, updated_at=:updated_at WHERE comment_id=:comment_id ")
    @BatchChunkSize(1000)
    int[] bulkUpdateComments(@Bind("text_display") List<String> textDisplays, @Bind("text_original") List<String> textOriginals,
                             @Bind("author_display_name") List<String> authorDisplayNames, @Bind("author_profile_image") List<String> authorProfileImages,
                             @Bind("like_count") List<Integer> likeCounts, @Bind("updated_at") List<String> updatedAts, @Bind("comment_id") List<String> commentIds);

    @SqlUpdate("DELETE FROM comments WHERE video_id=:video_id")
    void deleteAllComments(@Bind("video_id") String videoId);

}
