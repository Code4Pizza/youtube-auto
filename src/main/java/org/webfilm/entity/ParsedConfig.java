package org.webfilm.entity;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ParsedConfig {

    // 15s
    private static final int DEFAULT_CRAWLER_TIME = 15000;

    private int crawlerTime;

    private int videosLimit;

    private String[] youtubeApiKey;

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
                    if (config.getConfigValue().split(",").length > 0) {
                        youtubeApiKey = config.getConfigValue().split(",");
                    } else {
                        youtubeApiKey = new String[]{config.getConfigValue()};
                    }
                    break;
            }
        }
    }

    public int getCrawlerTime() {
        int time = crawlerTime == 0 ? DEFAULT_CRAWLER_TIME : crawlerTime * 1000;
        System.out.println("Crawler time = " + time);
        return time;
    }

    public int getVideosLimit() {
        return videosLimit;
    }

    public String[] getYoutubeApiKey() {
        return youtubeApiKey;
    }

    @Override
    public String toString() {
        return "ParsedConfig{" +
                "crawlerTime=" + crawlerTime +
                ", videosLimit=" + videosLimit +
                ", youtubeApiKey='" + Arrays.toString(youtubeApiKey) + '\'' +
                '}';
    }
}
