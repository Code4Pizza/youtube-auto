package org.webfilm;

import org.webfilm.api.ApiService;
import org.webfilm.api.RetryException;
import org.webfilm.api.RunOutKeyException;
import org.webfilm.entity.Channel;
import org.webfilm.entity.ParsedConfig;
import org.webfilm.entity.Video;
import org.webfilm.storage.WebFilmDatabase;
import org.webfilm.util.DateUtil;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryVideoJob implements Runnable {

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private final CountDownLatch jobCountDown;
    private final ApiService apiService;
    private final WebFilmDatabase database;
    private final Channel channel;
    private final ParsedConfig configs;

    public QueryVideoJob(CountDownLatch jobCountDown, ApiService apiService, WebFilmDatabase database, Channel channel, ParsedConfig configs) {
        this.jobCountDown = jobCountDown;
        this.apiService = apiService;
        this.database = database;
        this.channel = channel;
        this.configs = configs;
    }

    @Override
    public void run() {
        try {
            System.out.println("==============> Fetching videos from channel " + channel.getName());
            List<Video> remoteVideos = apiService.getVideosFromChannel(channel.getYoutubeId());

            int countInserted = 0, countUpdated = 0;
            for (Video remoteVideo : remoteVideos) {
                remoteVideo.setChannelId(channel.getId());
                Video localVideo = database.isVideoExisted(channel.getId(), remoteVideo.getYoutubeId());
                if (localVideo != null) {
                    // Update video and mapping
                    database.updateVideo(remoteVideo);
                    // database.updateVideoChannelMapping(id, channel.getId());
                    countUpdated++;
                } else {
                    // Insert video and mapping
                    int id = database.insertVideo(remoteVideo);
                    database.insertVideoChannelMapping(id, channel.getId());
                    countInserted++;
                }
            }
            String updatedTime = DateUtil.convertStringDate(System.currentTimeMillis());
            database.updateChannelUpdatedTime(updatedTime, channel.getYoutubeId());
            System.out.println("==============> Channel " + channel.getName() + "(id:" + channel.getYoutubeId() + ")" +
                    " fetched " + remoteVideos.size() + " videos ( inserted: " + countInserted + ", updated: " + countUpdated + ")");

            if (QueryVideosJob.updateCommentsJobCountdown.get() == 0) {
                System.out.println("Update comments is out of quote, passed");
                return;
            }

            System.out.println("Run job update comments of videos in channel " + channel.getName());
            List<Video> newsLocalVideos = database.getVideos(channel.getId());
            CountDownLatch commentsJobCountdown = new CountDownLatch(newsLocalVideos.size());
            for (Video localVideo : newsLocalVideos) {
                executor.execute(new QueryCommentsJob(commentsJobCountdown, apiService, database, localVideo, configs));
            }
            try {
                commentsJobCountdown.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("==============> Query comments of channel " + channel.getName() + " finished");
            // Giảm quote update comments, mỗi 12h sẽ lại tăng lên ở UpdateCommentCountDownJob
            QueryVideosJob.updateCommentsJobCountdown.getAndSet(0);

        } catch (IOException e) {
            System.out.println("==============> Channel " + channel.getName() + "(id:" + channel.getYoutubeId() + ")" +
                    " return error " + e.getMessage());
        } catch (RetryException e) {
            System.out.println("Api key exceed limit, wait for re-run job");
        } catch (RunOutKeyException e) {
            //
        } finally {
            jobCountDown.countDown();
            // System.out.println("Countdown task " + jobCountDown.getCount());
        }
    }
}
