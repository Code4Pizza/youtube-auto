package org.webfilm;

import org.webfilm.api.ApiService;
import org.webfilm.entity.Channel;
import org.webfilm.entity.Video;
import org.webfilm.storage.WebFilmDatabase;

import java.io.IOException;
import java.util.List;

public class QueryVideoJob implements Runnable {

    private final ApiService apiService;
    private final WebFilmDatabase database;
    private final Channel channel;

    public QueryVideoJob(ApiService apiService, WebFilmDatabase database, Channel channel) {
        this.apiService = apiService;
        this.database = database;
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            System.out.println("==============> Get videos of channel " + channel.getYoutubeId());
            List<Video> videos = apiService.getVideosFromChannel(channel.getYoutubeId());
            int count = 0;
            for (Video video : videos) {
                int id = database.insertVideos(video);
                if (id <= 0) {
                    continue;
                }
                boolean success = database.insertVideoChannelMapping(id, channel.getId());
                if (success) {
                    System.out.println(video.toString());
                    count++;
                }
            }
            System.out.println("==============> Total videos fetched " + videos.size());
            System.out.println("==============> Inserted " + count + " videos of channel " + channel.getYoutubeId());
        } catch (IOException e) {
            System.out.println("==============> Response error " + e.getMessage());
            System.out.println("==============> Failed to get videos from channel " + channel.getYoutubeId());
        }
    }
}
