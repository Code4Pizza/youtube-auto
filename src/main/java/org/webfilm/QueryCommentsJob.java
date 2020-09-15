package org.webfilm;

import org.webfilm.api.ApiService;
import org.webfilm.api.RetryException;
import org.webfilm.api.RunOutKeyException;
import org.webfilm.entity.Comment;
import org.webfilm.entity.ParsedConfig;
import org.webfilm.entity.Video;
import org.webfilm.storage.WebFilmDatabase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

public class QueryCommentsJob implements Runnable {

    private final CountDownLatch jobCountDown;
    private final ApiService apiService;
    private final WebFilmDatabase database;
    private final Video video;
    private final ParsedConfig configs;

    public QueryCommentsJob(CountDownLatch jobCountDown, ApiService apiService, WebFilmDatabase database, Video video, ParsedConfig configs) {
        this.jobCountDown = jobCountDown;
        this.apiService = apiService;
        this.database = database;
        this.video = video;
        this.configs = configs;
    }

    @Override
    public void run() {
        List<Comment> listNeedUpdate = new ArrayList<>();
        List<Comment> listNeedInsert = new ArrayList<>();
        try {
            Set<Comment> remoteComments = apiService.getCommentsFromVideo(video.getYoutubeId(), null, configs.getPageCountQuery());
            for (Comment remoteComment : remoteComments) {
                Comment comment = database.getCommentById(video.getYoutubeId(), remoteComment.getCommentId());
                if (comment == null) {
                    listNeedInsert.add(remoteComment);
                } else {
                    listNeedUpdate.add(remoteComment);
                }
            }
            int[] inserted = database.bulkInsertComments(video.getYoutubeId(), listNeedInsert);
            int[] updated = database.bulkUpdateComments(listNeedUpdate);
            System.out.println("Comment inserted " + inserted.length);
            System.out.println("Comment updated " + updated.length);
        } catch (RetryException e) {
            System.out.println("Api key exceed limit, wait for re-run job");
        } catch (RunOutKeyException ignored) {
        } catch (IOException e) {
            System.out.println("==============> Video comments " + video.getName() + " return error " + e.getMessage());
        } finally {
            jobCountDown.countDown();
        }
    }
}
