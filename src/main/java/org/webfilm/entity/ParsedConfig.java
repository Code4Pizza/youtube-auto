package org.webfilm.entity;

import javax.annotation.Nonnull;
import java.util.List;

public class ParsedConfig {

    // 15s
    private static final int DEFAULT_CRAWLER_TIME = 15000;

    private int crawlerTime;

    private int videosLimit;

    private String youtubeApiKey;

    public ParsedConfig(@Nonnull List<Config> configs) {
        for (Config config : configs) {
            switch (config.getConfigKey()) {
                case "crawler_time":
                    crawlerTime = Integer.parseInt(config.getConfigValue());
                    break;
                case "videos_limit":
                    videosLimit = Integer.parseInt(config.getConfigValue());
                    break;
                case "youtube_api_key":
                    youtubeApiKey = config.getConfigValue();
                    break;
            }
        }
    }

    public int getCrawlerTime() {
        return crawlerTime == 0 ? DEFAULT_CRAWLER_TIME : crawlerTime * 1000;
    }

    public int getVideosLimit() {
        return videosLimit;
    }

    public String getYoutubeApiKey() {
        return youtubeApiKey;
    }

    @Override
    public String toString() {
        return "ParsedConfig{" +
                "crawlerTime=" + crawlerTime +
                ", videosLimit=" + videosLimit +
                ", youtubeApiKey='" + youtubeApiKey + '\'' +
                '}';
    }
}
