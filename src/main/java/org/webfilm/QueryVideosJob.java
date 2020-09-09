package org.webfilm;

import org.webfilm.api.ApiService;
import org.webfilm.entity.Channel;
import org.webfilm.entity.ParsedConfig;
import org.webfilm.storage.WebFilmDatabase;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    public void run() {
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
        for (Channel channel : channels) {
            executor.execute(new QueryVideoJob(jobCountDown, apiService, database, channel));
        }
        try {
            jobCountDown.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Query videos job finished");
    }

    private void updateChannelInfo(List<Channel> channels) throws IOException {
        StringBuilder listQueryChannelId = new StringBuilder();
        for (int i = 0; i < channels.size(); i++) {
            listQueryChannelId.append(channels.get(i).getYoutubeId());
            if (i != channels.size() - 1) {
                listQueryChannelId.append(",");
            }
        }
        List<Channel> infos = apiService.getChannelInfos(listQueryChannelId.toString());
        for (Channel info : infos) {
            boolean success = database.updateChannelInfo(info);
            if (!success) {
                System.out.println("Failed to update info channel " + info.toString());
            }
        }
    }

    public static void main(String[] args) {
        QueryVideosJob job = QueryVideosJob.getInstance();
        while (true) {
            try {
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                System.out.println("$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$");
                job.run();
                // Waiting for amount of config time to start again
                Thread.sleep(job.configs.getCrawlerTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
