package org.webfilm.api;

import com.google.gson.*;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.webfilm.entity.Channel;
import org.webfilm.entity.Comment;
import org.webfilm.entity.ParsedConfig;
import org.webfilm.entity.Video;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ApiService {

    public static final String BASE_URL = "https://www.googleapis.com/youtube/v3";
    public static final String PATH_LIST_VIDEO = "/search";
    public static final String PATH_VIDEO_DETAILS = "/videos";
    public static final String PATH_CHANNEL_INFO = "/channels";
    public static final String PATH_COMMENTS = "/commentThreads";

    private final ParsedConfig parsedConfig;
    private final Gson gson;

    private final AtomicInteger availableApiKeyIndex = new AtomicInteger(0);

    public ApiService(ParsedConfig parsedConfig) {
        this.parsedConfig = parsedConfig;
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<Video> videoJsonDeserializer = ApiService::deserializeVideo;
        JsonDeserializer<Channel> channelJsonDeserializer = ApiService::deserializeChannel;
        JsonDeserializer<Comment> commentJsonDeserializer = ApiService::deserializeComment;
        gsonBuilder.registerTypeAdapter(Video.class, videoJsonDeserializer);
        gsonBuilder.registerTypeAdapter(Channel.class, channelJsonDeserializer);
        gsonBuilder.registerTypeAdapter(Comment.class, commentJsonDeserializer);
        this.gson = gsonBuilder.create();
    }

    private static Video deserializeVideo(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject itemObject = json.getAsJsonObject();

        String youtubeId = itemObject.get("id").getAsString();
        String url = String.format("https://www.youtube.com/embed/%s", youtubeId);

        String name = "";
        String publishedAt = "";
        String description = "";
        int duration = 0;
        String bgImage = "";
        int views = 0;
        Timestamp publishedTime = null;

        try {
            JsonObject snippetObject = itemObject.getAsJsonObject("snippet");
            name = snippetObject.get("title").getAsString();
            publishedAt = snippetObject.get("publishedAt").getAsString();
            // format to timestamp
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
            publishedTime = new Timestamp(format.parse(publishedAt).getTime());

            description = snippetObject.get("description").getAsString();
            JsonObject thumbnailObject = snippetObject.getAsJsonObject("thumbnails");
            if (thumbnailObject.has("maxres")) {
                bgImage = thumbnailObject.getAsJsonObject("maxres").get("url").getAsString();
            } else if (thumbnailObject.has("high")) {
                bgImage = thumbnailObject.getAsJsonObject("high").get("url").getAsString();
            } else if (thumbnailObject.has("medium")) {
                bgImage = thumbnailObject.getAsJsonObject("medium").get("url").getAsString();
            } else if (thumbnailObject.has("default")) {
                bgImage = thumbnailObject.getAsJsonObject("default").get("url").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to fetch snippet " + youtubeId);
        }

        try {
            JsonObject detailObject = itemObject.getAsJsonObject("contentDetails");
            // "PT1H1M13S"
            String formattedDuration = detailObject.get("duration").getAsString();
            duration = (int) Period.parse(formattedDuration, ISOPeriodFormat.standard())
                    .toStandardDuration().getMillis() / 1000;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to fetch contentDetails of " + youtubeId);
        }

        try {
            JsonObject statisticsObject = itemObject.getAsJsonObject("statistics");
            views = Integer.parseInt(statisticsObject.get("viewCount").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to fetch statistics of " + youtubeId);
        }

        return new Video(name, description, publishedTime, duration, bgImage, url, views, youtubeId);
    }

    private static Channel deserializeChannel(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject itemObject = json.getAsJsonObject();

        String youtubeId = itemObject.get("id").getAsString();
        String youtubeUrl = String.format("https://www.youtube.com/channel/%s", youtubeId);
        String name = "";
        String description = "";
        String avatar = "";
        int subscribers = 0;

        try {
            JsonObject snippetObject = itemObject.getAsJsonObject("snippet");
            name = snippetObject.get("title").getAsString();
            description = snippetObject.get("description").getAsString();
            JsonObject thumbnailObject = snippetObject.getAsJsonObject("thumbnails");
            if (thumbnailObject.has("maxres")) {
                avatar = thumbnailObject.getAsJsonObject("maxres").get("url").getAsString();
            } else if (thumbnailObject.has("high")) {
                avatar = thumbnailObject.getAsJsonObject("high").get("url").getAsString();
            } else if (thumbnailObject.has("medium")) {
                avatar = thumbnailObject.getAsJsonObject("medium").get("url").getAsString();
            } else if (thumbnailObject.has("default")) {
                avatar = thumbnailObject.getAsJsonObject("default").get("url").getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to fetch snippet " + youtubeId);
        }

        try {
            JsonObject statisticsObject = itemObject.getAsJsonObject("statistics");
            subscribers = Integer.parseInt(statisticsObject.get("subscriberCount").getAsString());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to fetch statistics of " + youtubeId);
        }

        return new Channel(name, youtubeUrl, youtubeId, description, avatar, subscribers);
    }

    public static Comment deserializeComment(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject itemObject = json.getAsJsonObject();

        String videoId = "";
        String commentId = "";
        String textDisplay = "";
        String textOriginal = "";
        String authorDisplayName = "";
        String authorProfileImage = "";
        String authorChannelId = "";
        int likeCount = 0;
        String publishedAt = "";
        String updatedAt = "";

        try {
            JsonObject snippetObject = itemObject.getAsJsonObject("snippet");
            videoId = snippetObject.get("videoId").getAsString();
            JsonObject topLevelComment = snippetObject.getAsJsonObject("topLevelComment");
            commentId = topLevelComment.get("id").getAsString();
            JsonObject snippet = topLevelComment.getAsJsonObject("snippet");
            textDisplay = snippet.get("textDisplay").getAsString();
            textOriginal = snippet.get("textOriginal").getAsString();
            authorDisplayName = snippet.get("authorDisplayName").getAsString();
            authorProfileImage = snippet.get("authorProfileImageUrl").getAsString();
            authorChannelId = snippet.get("authorChannelId").getAsJsonObject().get("value").getAsString();
            likeCount = snippet.get("likeCount").getAsInt();
            publishedAt = snippet.get("publishedAt").getAsString();
            updatedAt = snippet.get("updatedAt").getAsString();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Failed to parse comment");
        }

        return new Comment(videoId, commentId, textDisplay, textOriginal, authorDisplayName, authorProfileImage, authorChannelId, likeCount, publishedAt, updatedAt);
    }

    public String makeServiceRequest(String path, @Nonnull Map<String, String> headers) throws IOException, RetryException, RunOutKeyException {
        return makeServiceRequest(path, "GET", headers);
    }

    public String makeServiceRequest(String path, String method, @Nonnull Map<String, String> headers) throws IOException, RetryException, RunOutKeyException {
        try {
            StringBuilder query = new StringBuilder();
            String[] apiKeys = parsedConfig.getYoutubeApiKey();
            if (apiKeys.length < availableApiKeyIndex.get() + 1) {
                throw new RunOutKeyException();
            }
            query.append(String.format("key=%s", apiKeys[availableApiKeyIndex.get()]));
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                query.append("&");
                query.append(String.format("%s=%s", entry.getKey(), entry.getValue()));
            }

            String fullUrl = BASE_URL + path + "?" + query;
            System.out.println("Full url : " + fullUrl);

            URL url = new URL(fullUrl);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod(method);
            con.setConnectTimeout(30000);
            con.setReadTimeout(30000);
            con.setRequestProperty("Content-Type", "application/json");

            int status = con.getResponseCode();

            Reader streamReader;

            if (status == 403) {
                // Api key exceed limit
                availableApiKeyIndex.incrementAndGet();
                throw new RetryException("Api key exceed limit");
            }

            if (status > 299) {
                streamReader = new InputStreamReader(con.getErrorStream());
            } else {
                streamReader = new InputStreamReader(con.getInputStream());
            }

            BufferedReader in = new BufferedReader(streamReader);
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }
            in.close();
            return content.toString();
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Failed to request " + path + " - " + headers);
        }
    }

    public List<Channel> getChannelInfos(String listQueryChannelId) throws IOException, RetryException, RunOutKeyException {
        Map<String, String> query = new HashMap<>();
        query.put("id", listQueryChannelId);
        query.put("part", "snippet,statistics");
        String response = makeServiceRequest(PATH_CHANNEL_INFO, query);
        JsonObject responseJson = gson.fromJson(response, JsonObject.class);
        if (!responseJson.has("items")) {
            throw new IOException(responseJson.toString());
        }
        JsonArray items = responseJson.getAsJsonArray("items");
        List<Channel> channels = new ArrayList<>();
        for (JsonElement item : items) {
            Channel channel = gson.fromJson(item, Channel.class);
            channels.add(channel);
        }
        return channels;
    }

    public List<Video> getVideosFromChannel(String channelId) throws IOException, RetryException, RunOutKeyException {
        Map<String, String> query = new HashMap<>();
        String response;
        JsonObject responseJson;
        JsonArray items;

        query.put("channelId", channelId);
        query.put("part", "id");
        query.put("maxResults", String.valueOf(parsedConfig.getVideosLimit()));
        response = makeServiceRequest(PATH_LIST_VIDEO, query);
        responseJson = gson.fromJson(response, JsonObject.class);
        if (!responseJson.has("items")) {
            throw new IOException(responseJson.toString());
        }
        StringBuilder listQueryVideoId = new StringBuilder();
        items = responseJson.getAsJsonArray("items");
        for (JsonElement item : items) {
            String videoId = null;
            try {
                JsonObject idObject = item.getAsJsonObject().getAsJsonObject("id");
                videoId = idObject.get("videoId").getAsString();
            } catch (Exception e) {
                // item contains playlist id
                // System.out.println("Item don't have video id " + item.toString());
            }
            if (videoId == null) {
                continue;
            }
            listQueryVideoId.append(videoId);
            if (!items.get(items.size() - 1).equals(item)) {
                listQueryVideoId.append(",");
            }
        }
        query.clear();
        query.put("id", listQueryVideoId.toString());
        query.put("part", "snippet,statistics,contentDetails");
        response = makeServiceRequest(PATH_VIDEO_DETAILS, query);
        responseJson = gson.fromJson(response, JsonObject.class);
        if (!responseJson.has("items")) {
            throw new IOException(responseJson.toString());
        }
        List<Video> videos = new ArrayList<>();
        items = responseJson.getAsJsonArray("items");
        for (JsonElement item : items) {
            Video video = gson.fromJson(item, Video.class);
            videos.add(video);
        }
        return videos;
    }

    public Set<Comment> getCommentsFromVideo(String videoId, @Nullable String nextPageToken, int count) throws RetryException, RunOutKeyException, IOException {
        Map<String, String> query = new HashMap<>();
        String response;
        JsonObject responseJson;
        JsonArray items;

        query.put("videoId", videoId);
        query.put("part", "snippet");
        query.put("maxResults", String.valueOf(parsedConfig.getCommentsLimit()));
        query.put("order", "relevance");

        if (nextPageToken != null) {
            query.put("pageToken", nextPageToken);
        }

        response = makeServiceRequest(PATH_COMMENTS, query);
        responseJson = gson.fromJson(response, JsonObject.class);
        if (!responseJson.has("items")) {
            throw new IOException(responseJson.toString());
        }
        Set<Comment> comments = new HashSet<>();
        items = responseJson.getAsJsonArray("items");
        for (JsonElement item : items) {
            Comment comment = gson.fromJson(item, Comment.class);
            comments.add(comment);
        }
        if (!responseJson.has("nextPageToken")) {
            if (count > 0) {
                System.out.println("Query all out of comments");
            }
            return comments;
        }
        nextPageToken = responseJson.get("nextPageToken").getAsString();
        if (count > 0) {
            System.out.println("Query next page " + count);
            comments.addAll(getCommentsFromVideo(videoId, nextPageToken, --count));
        }
        return comments;
    }
}
