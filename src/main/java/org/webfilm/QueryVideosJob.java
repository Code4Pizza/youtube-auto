package org.webfilm;

import org.webfilm.api.ApiService;
import org.webfilm.api.RetryException;
import org.webfilm.api.RunOutKeyException;
import org.webfilm.entity.Channel;
import org.webfilm.entity.ParsedConfig;
import org.webfilm.storage.WebFilmDatabase;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.webfilm.util.DateUtil.isChannelUpToDate;

public class QueryVideosJob {

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
            executor.execute(new QueryVideoJob(jobCountDown, apiService, database, channel));
        }
        try {
            jobCountDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Query videos job finished");
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
        database.bulkUpdateChannelInfo(infos);
    }

    public static void main(String[] args) {
        QueryVideosJob job = QueryVideosJob.getInstance();
        while (true) {
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
            try {
                job.run();
            } catch (RetryException e) {
                e.printStackTrace();
                // Apply new api key, re-run job immediately
                continue;
            } catch (RunOutKeyException e) {
                e.printStackTrace();
                try {
                    // Waiting 1 day to reset quota
                    TimeUnit.DAYS.sleep(1);
                } catch (InterruptedException ignored) {

                }
            }
            try {
                // Waiting 30 mis to repeat job
                TimeUnit.MINUTES.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
