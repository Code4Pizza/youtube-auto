package org.webfilm;

import org.webfilm.api.ApiService;
import org.webfilm.entity.Channel;
import org.webfilm.entity.Video;
import org.webfilm.storage.WebFilmDatabase;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class QueryVideoJob implements Runnable {

    private final CountDownLatch jobCountDown;
    private final ApiService apiService;
    private final WebFilmDatabase database;
    private final Channel channel;

    public QueryVideoJob(CountDownLatch jobCountDown, ApiService apiService, WebFilmDatabase database, Channel channel) {
        this.jobCountDown = jobCountDown;
        this.apiService = apiService;
        this.database = database;
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            System.out.println("==============> Fetching videos from channel " + channel.getYoutubeId());
            List<Video> videos = apiService.getVideosFromChannel(channel.getYoutubeId());
            int countInserted = 0, countUpdated = 0;
            for (Video video : videos) {
                int id = database.insertOrUpdateVideo(video);
                if (id <= 0) {
                    countUpdated++;
                    continue;
                }
                boolean success = database.insertVideoChannelMapping(id, channel.getId());
                if (success) {
                    System.out.println(video.toString());
                    countInserted++;
                }
            }
            System.out.println("==============> Channel " + channel.getName() + "(id:" + channel.getYoutubeId() + ")" +
                    " fetched " + videos.size() + " videos ( inserted: " + countInserted + ", updated: " + countUpdated + ")");
        } catch (IOException e) {
            System.out.println("==============> Channel " + channel.getName() + "(id:" + channel.getYoutubeId() + ")" +
                    " return error " + e.getMessage());
        } finally {
            jobCountDown.countDown();
        }
    }
}
