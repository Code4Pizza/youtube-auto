package org.webfilm;

import org.webfilm.api.ApiService;
import org.webfilm.api.RetryException;
import org.webfilm.api.RunOutKeyException;
import org.webfilm.entity.Channel;
import org.webfilm.entity.ParsedConfig;
import org.webfilm.storage.WebFilmDatabase;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.webfilm.util.DateUtil.isChannelUpToDate;

public class QueryVideosJob {

    public static final AtomicInteger updateCommentsJobCountdown = new AtomicInteger(1);

    public static QueryVideosJob INSTANCE;

    public static QueryVideosJob getInstance() {
        if (INSTANCE == null) {
            WebFilmDatabase database = WebFilmDatabase.getInstance();
            ParsedConfig configs = database.getSystemConfigs();
            ApiService apiService = new ApiService(configs);
            INSTANCE = new QueryVideosJob(configs, apiService, database);
        }
        return INSTANCE;
    }

    private final ExecutorService executor = Executors.newFixedThreadPool(5);

    private final ParsedConfig configs;
    private final ApiService apiService;
    private final WebFilmDatabase database;

    private QueryVideosJob(ParsedConfig configs, ApiService apiService, WebFilmDatabase database) {
        this.configs = configs;
        this.apiService = apiService;
        this.database = database;
    }

    public void run() throws RetryException, RunOutKeyException {
        System.out.println("Start query videos job");

        List<Channel> channels = database.getChannels();
//                .stream().filter(item -> item.getYoutubeId().equalsIgnoreCase("UCXF4WjTCUQSmGapnNEZzbYw")).collect(Collectors.toList());
        // sub lại ít channel test cho dễ
//         channels = channels.subList(channels.size() - 1, channels.size());
        System.out.println("Total channels fetched " + channels.size());

        try {
            updateChannelInfo(channels);
            System.out.println("Update info success " + channels.toString());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to update info channels");
        }

        CountDownLatch jobCountDown = new CountDownLatch(channels.size());
        int crawlerTime = configs.getCrawlerTime();
        for (Channel channel : channels) {
            if (isChannelUpToDate(crawlerTime, channel.getUpdatedTime())) {
                jobCountDown.countDown();
                // System.out.println("Countdown task " + jobCountDown.getCount());
                System.out.println("Channel " + channel.getName() + "is up to date, ignore videos querying");
                continue;
            }
            executor.execute(new QueryVideoJob(jobCountDown, apiService, database, channel, configs));
        }
        try {
            jobCountDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Query videos job finished ");
    }

    private void updateChannelInfo(List<Channel> channels) throws IOException, RetryException, RunOutKeyException {
        StringBuilder listQueryChannelId = new StringBuilder();
        for (int i = 0; i < channels.size(); i++) {
            listQueryChannelId.append(channels.get(i).getYoutubeId());
            if (i != channels.size() - 1) {
                listQueryChannelId.append(",");
            }
        }
        List<Channel> infos = apiService.getChannelInfos(listQueryChannelId.toString());

        // compare deleted channel
        if (infos.size() < channels.size()) {
            channels.forEach(item -> {
                if (infos.stream().noneMatch(c -> c.getYoutubeId().equals(item.getYoutubeId()))) {
                    System.out.println("Start delete channel : " + item.getName());
                    database.deleteChanel(item);
                }
            });
        }


        database.bulkUpdateChannelInfo(infos);
    }

    public static void main(String[] args) {
        QueryVideosJob job = QueryVideosJob.getInstance();
        new Thread(new UpdateCommentCountDownJob()).start();
        while (true) {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            long startTime = System.currentTimeMillis();
            try {
                job.run();
            } catch (RetryException e) {
                e.printStackTrace();
                // Apply new api key, re-run job immediately
                continue;
            } catch (RunOutKeyException e) {
                e.printStackTrace();
            }
            long endTime = TimeUnit.MILLISECONDS.toMinutes(System.currentTimeMillis() - startTime);
            System.out.println("Job done in " + endTime + " mins");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            try {
                // Waiting 10 mis to repeat job
                TimeUnit.MINUTES.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }

    private static class UpdateCommentCountDownJob implements Runnable {

        @Override
        public void run() {
            try {
                TimeUnit.HOURS.sleep(12);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Increase quote update comments");
            updateCommentsJobCountdown.incrementAndGet();
        }
    }
}
