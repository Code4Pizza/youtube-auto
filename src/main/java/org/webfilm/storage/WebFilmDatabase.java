package org.webfilm.storage;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.SharedMetricRegistries;
import com.codahale.metrics.Timer;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;
import org.webfilm.entity.Channel;
import org.webfilm.entity.Comment;
import org.webfilm.entity.ParsedConfig;
import org.webfilm.entity.Video;
import org.youtube.configuration.CircuitBreakerConfiguration;
import org.youtube.storage.FaultTolerantDatabase;
import org.youtube.util.Constants;

import java.util.ArrayList;
import java.util.List;

import static com.codahale.metrics.MetricRegistry.name;

public class WebFilmDatabase {

    public static final String SCHEMA = "web_film";
    public static final String HOST_PORT = "35.221.250.42:3306";
    public static final String BASE_URL = String.format("jdbc:mysql://%s/%s?autoReconnect=true&useSSL=false&useUnicode=yes&characterEncoding=UTF-8", HOST_PORT, SCHEMA);

    public static final String USER_NAME = "film_account";
    public static final String PASSWORD = "123456aA@";
    public static final String DB_NAME = "web_film";

    private final MetricRegistry metricRegistry = SharedMetricRegistries.getOrCreate(Constants.METRICS_NAME);

    private final Timer defaultTimer = metricRegistry.timer(name(WebFilmDatabase.class, "timers"));

    private static WebFilmDatabase INSTANCE;

    public static WebFilmDatabase getInstance() {
        if (INSTANCE == null) {
            Jdbi jdbi = Jdbi.create(BASE_URL, USER_NAME, PASSWORD).installPlugin(new SqlObjectPlugin());
            FaultTolerantDatabase accDatabase = new FaultTolerantDatabase(DB_NAME, jdbi, new CircuitBreakerConfiguration());
            INSTANCE = new WebFilmDatabase(accDatabase);
        }
        return INSTANCE;
    }

    private final FaultTolerantDatabase database;

    private WebFilmDatabase(FaultTolerantDatabase database) {
        this.database = database;
    }

    public ParsedConfig getSystemConfigs() {
        return new ParsedConfig(database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).getSystemConfigs();
            }
        })));
    }

    public List<Channel> getChannels() {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).getChannels();
            }
        }));
    }

    public List<Video> getVideos(int channelId) {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).getVideos(channelId);
            }
        }));
    }

    public Video isVideoExisted(int channelId, String youtubeId) {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).getVideoByYoutubeId(channelId, youtubeId);
            }
        }));
    }

    public int insertVideo(Video video) {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).insertVideo(video);
            }
        }));
    }

    public boolean updateVideo(Video video) {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).updateVideo(
                        video.getName(),
                        video.getDescription(),
                        video.getDuration(),
                        video.getUrl(),
                        video.getViews(),
                        video.getBgImage(),
                        video.getYoutubeId(),
                        video.getChannelId()
                ) > 0;
            }
        }));
    }

    public boolean insertVideoChannelMapping(int videoId, int channelId) {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).insertVideoChannelMapping(videoId, channelId) > 0;
            }
        }));
    }

    public boolean updateVideoChannelMapping(int videoId, int channelId) {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).updateVideoChannelMapping(videoId, channelId) > 0;
            }
        }));
    }

    public boolean updateChannelInfo(Channel channel) {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).updateChannelInfo(
                        channel.getName(),
                        channel.getDescription(),
                        channel.getAvatar(),
                        channel.getSubscribers(),
                        channel.getYoutubeId()) > 0;
            }
        }));
    }

    public void bulkUpdateChannelInfo(List<Channel> channels) {
        List<String> names = new ArrayList<>();
        List<String> descriptions = new ArrayList<>();
        List<String> avatars = new ArrayList<>();
        List<Integer> subs = new ArrayList<>();
        List<String> youtubeIds = new ArrayList<>();

        for (Channel channel : channels) {
            names.add(channel.getName());
            descriptions.add(channel.getDescription());
            avatars.add(channel.getAvatar());
            subs.add(channel.getSubscribers());
            youtubeIds.add(channel.getYoutubeId());
        }

        database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                handle.attach(WebFilmDAO.class).bulkUpdateChannelInfo(names, descriptions, avatars, subs, youtubeIds);
                return null;
            }
        }));
    }

    public void updateChannelUpdatedTime(String updatedTime, String youtubeId) {
        database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).updateChannelUpdatedTime(updatedTime, youtubeId);
            }
        }));
    }

    public int[] bulkInsertComments(String videoId, List<Comment> comments) {
        List<String> commentIds = new ArrayList<>();
        List<String> textDisplays = new ArrayList<>();
        List<String> textOriginals = new ArrayList<>();
        List<String> authorDisplayNames = new ArrayList<>();
        List<String> authorProfileImages = new ArrayList<>();
        List<String> authorChannelIds = new ArrayList<>();
        List<Integer> likeCounts = new ArrayList<>();
        List<String> publishedAts = new ArrayList<>();
        List<String> updatedAt = new ArrayList<>();

        for (Comment comment : comments) {
            commentIds.add(comment.getCommentId());
            textDisplays.add(comment.getTextDisplay());
            textOriginals.add(comment.getTextOriginal());
            authorDisplayNames.add(comment.getAuthorDisplayName());
            authorProfileImages.add(comment.getAuthorProfileImage());
            authorChannelIds.add(comment.getAuthorChannelId());
            likeCounts.add(comment.getLikeCount());
            publishedAts.add(comment.getPublishedAt());
            updatedAt.add(comment.getUpdatedAt());
        }

        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).bulkInsertComments(videoId, commentIds, textDisplays,
                        textOriginals, authorDisplayNames, authorProfileImages, authorChannelIds, likeCounts, publishedAts, updatedAt);
            }
        }));
    }

    public int[] bulkUpdateComments(List<Comment> comments) {
        List<String> commentIds = new ArrayList<>();
        List<String> textDisplays = new ArrayList<>();
        List<String> textOriginals = new ArrayList<>();
        List<String> authorDisplayNames = new ArrayList<>();
        List<String> authorProfileImages = new ArrayList<>();
        List<Integer> likeCounts = new ArrayList<>();
        List<String> publishedAts = new ArrayList<>();

        for (Comment comment : comments) {
            commentIds.add(comment.getCommentId());
            textDisplays.add(comment.getTextDisplay());
            textOriginals.add(comment.getTextOriginal());
            authorDisplayNames.add(comment.getAuthorDisplayName());
            authorProfileImages.add(comment.getAuthorProfileImage());
            likeCounts.add(comment.getLikeCount());
            publishedAts.add(comment.getPublishedAt());
        }

        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).bulkUpdateComments(textDisplays,
                        textOriginals, authorDisplayNames, authorProfileImages, likeCounts, publishedAts, commentIds);
            }
        }));
    }

    public void deleteComments(String videoId) {
        database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                handle.attach(WebFilmDAO.class).deleteAllComments(videoId);
                return null;
            }
        }));
    }

    public void deleteVideoById(String videoId) {
        database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                handle.attach(WebFilmDAO.class).deleteVideoById(videoId);
                handle.attach(WebFilmDAO.class).deleteVideoCatMapping(videoId);
                handle.attach(WebFilmDAO.class).deleteVideoChannelMapping(videoId);
                handle.attach(WebFilmDAO.class).deleteVideoFilmMapping(videoId);
                handle.attach(WebFilmDAO.class).deleteVideoMenuMapping(videoId);
                return null;
            }
        }));
    }

    public Comment getCommentById(String videoId, String commentId) {
        return database.with(jdbi -> jdbi.withHandle(handle -> {
            try (Timer.Context ignored = defaultTimer.time()) {
                return handle.attach(WebFilmDAO.class).getCommentById(videoId, commentId);
            }
        }));
    }
}
