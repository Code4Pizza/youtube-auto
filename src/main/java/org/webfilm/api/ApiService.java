package org.webfilm.api;

import com.google.gson.*;
import org.joda.time.Period;
import org.joda.time.format.ISOPeriodFormat;
import org.webfilm.entity.Channel;
import org.webfilm.entity.ParsedConfig;
import org.webfilm.entity.Video;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiService {

    public static final String BASE_URL = "https://www.googleapis.com/youtube/v3";
    public static final String PATH_LIST_VIDEO = "/search";
    public static final String PATH_VIDEO_DETAILS = "/videos";
    public static final String PATH_CHANNEL_INFO = "/channels";

    private final ParsedConfig parsedConfig;
    private final Gson gson;

    public ApiService(ParsedConfig parsedConfig) {
        this.parsedConfig = parsedConfig;
        GsonBuilder gsonBuilder = new GsonBuilder();
        JsonDeserializer<Video> videoJsonDeserializer = ApiService::deserializeVideo;
        JsonDeserializer<Channel> channelJsonDeserializer = ApiService::deserializeChannel;
        gsonBuilder.registerTypeAdapter(Video.class, videoJsonDeserializer);
        gsonBuilder.registerTypeAdapter(Channel.class, channelJsonDeserializer);
        this.gson = gsonBuilder.create();
    }

    public ParsedConfig getParsedConfig() {
        return parsedConfig;
    }

    private static Video deserializeVideo(JsonElement json, Type type, JsonDeserializationContext context) {
        JsonObject itemObject = json.getAsJsonObject();

        String youtubeId = itemObject.get("id").getAsString();
        String url = String.format("https://www.youtube.com/embed/%s", youtubeId);

        String name = "";
        String description = "";
        int duration = 0;
        String bgImage = "";
        int views = 0;

        try {
            JsonObject snippetObject = itemObject.getAsJsonObject("snippet");
            name = snippetObject.get("title").getAsString();
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
            duration = (int) Period.parse(formattedDuration, ISOPeriodFormat.standard()).toStandardDuration().getMillis();
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

        return new Video(name, description, duration, bgImage, url, views, youtubeId);
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

    public String makeServiceRequest(String path, @Nonnull Map<String, String> headers) throws IOException {
        return makeServiceRequest(path, "GET", headers);
    }

    public String makeServiceRequest(String path, String method, @Nonnull Map<String, String> headers) throws IOException {
        try {
            StringBuilder query = new StringBuilder();
            query.append(String.format("key=%s", parsedConfig.getYoutubeApiKey()));
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

    public List<Channel> getChannelInfos(String listQueryChannelId) throws IOException {
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

    public List<Video> getVideosFromChannel(String channelId) throws IOException {
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
                System.out.println("Item dont have video id " + item.toString());
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
}
