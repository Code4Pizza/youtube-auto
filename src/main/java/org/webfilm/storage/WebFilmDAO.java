package org.webfilm.storage;

import org.jdbi.v3.sqlobject.config.RegisterConstructorMapper;
import org.jdbi.v3.sqlobject.customizer.Bind;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.GetGeneratedKeys;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;
import org.webfilm.entity.Channel;
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

    @SqlUpdate("UPDATE videos SET `name`=:name, description=:description, duration=:duration, url=:url, views=:views, bg_image=:bg_image WHERE youtube_id=:youtube_id")
    void updateVideo(@Bind("name") String name, @Bind("description") String description, @Bind("duration") int duration,
                    @Bind("url") String url, @Bind("views") int views, @Bind("bg_image") String bgImage, @Bind("youtube_id") String youtubeId);

    @SqlUpdate("INSERT INTO videos(`name`, description, duration, url, views, bg_image, youtube_id) " +
            "VALUES(:name, :description, :duration, :url, :views, :bgImage, :youtubeId)")
    @GetGeneratedKeys
    int insertVideo(@BindBean Video video);

    @SqlUpdate("INSERT INTO video_channel_mapping(video_id, channel_id) VALUES(:video_id, :channel_id)")
    int insertVideoChannelMapping(@Bind("video_id") int videoId, @Bind("channel_id") int channelId);

    @SqlUpdate("UPDATE channels SET `name`=:name, description=:description, avatar=:avatar, subscribers=:subscribers WHERE youtube_id=:youtube_id")
    int updateChannelInfo(@Bind("name") String name, @Bind("description") String description, @Bind("avatar") String avatar,
                          @Bind("subscribers") int subscribers, @Bind("youtube_id") String youtubeId);

    @SqlUpdate("UPDATE channels SET updated_time=:updated_time WHERE youtube_id=:youtube_id")
    int updateChannelUpdatedTime(@Bind("updated_time") String updatedTime, @Bind("youtube_id") String youtubeId);

}
