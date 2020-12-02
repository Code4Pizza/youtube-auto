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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

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
//            List<Video> remoteVideos = apiService.getVideosFromChannel(channel.getYoutubeId());
//            if (remoteVideos.size() == 0) {
//                remoteVideos = apiService.getVideoFromChannelPlaylist(channel.getYoutubeId());
//            }

            List<Video> remoteVideos = apiService.getVideoFromChannelPlaylist(channel.getYoutubeId());
            List<Video> currentVideos = database.getVideos(channel.getId());
            Map<String, Boolean> checkMapper = new HashMap<>();

            int countInserted = 0;
            int countUpdated = 0;
            AtomicInteger countDeleted = new AtomicInteger();
            for (Video remoteVideo : remoteVideos) {
                checkMapper.put(remoteVideo.getYoutubeId(), true);
                remoteVideo.setChannelId(channel.getId());
                Video localVideo = database.isVideoExisted(channel.getId(), remoteVideo.getYoutubeId());
                if (localVideo != null) {
                    // Update video and mapping
                    database.updateVideo(remoteVideo);
                    database.deleteVideoTags(localVideo.getId());
                    database.insertVideoTags(localVideo.getId(), remoteVideo.getTags());
                    countUpdated++;
                } else {
                    // Insert video and mapping
                    localVideo = database.isVideoExistedOnlyById(remoteVideo.getYoutubeId());
                    if (localVideo != null) {
                        int id = database.insertVideo(remoteVideo);
                        database.insertVideoChannelMapping(id, channel.getId());
                        if (remoteVideo.getTags() != null && remoteVideo.getTags().size() > 0)
                            database.insertVideoTags(id, remoteVideo.getTags());
                        countInserted++;
                    } else {
                        System.out.println("======Duplicate video : " + remoteVideo.getYoutubeId());
                    }
                }

            }
            currentVideos.forEach(video -> {
                if (checkMapper.get(video.getYoutubeId()) == null) {
                    database.deleteVideoMappingById(video.getYoutubeId());
                    database.deleteVideoById(video.getYoutubeId());
                    countDeleted.getAndIncrement();
                }
            });

            System.out.println("==============> Channel " + channel.getName() + "(id:" + channel.getYoutubeId() + ")" +
                    " fetched " + remoteVideos.size() + " videos ( inserted: " + countInserted
                    + ", updated: " + countUpdated + ", deleted: " + countDeleted.get() + ")");


            // for livestream
            AtomicReference<Integer> countLivestreamInsert = new AtomicReference<>(0);
            AtomicReference<Integer> countLivestreamUpdate = new AtomicReference<>(0);
            System.out.println("===========>Start update livestream");
            List<Video> livestreams = apiService.getLivesFromChannel(channel.getYoutubeId());
            livestreams.forEach(live -> {
                // check and insert live
                if (!database.checkLivestreamByURL(live.getUrl())) {
                    countLivestreamInsert.getAndSet(countLivestreamInsert.get() + 1);
                    database.insertLivestream(live);
                } else {
                    countLivestreamUpdate.getAndSet(countLivestreamUpdate.get() + 1);
                    database.updateLivestream(live);
                }
            });

            System.out.println("==========>Finish livestream with inserted : " + countLivestreamInsert.get()
                    + ", updated: " + countLivestreamUpdate.get());


            String updatedTime = DateUtil.convertStringDate(System.currentTimeMillis());
            database.updateChannelUpdatedTime(updatedTime, channel.getYoutubeId());

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
