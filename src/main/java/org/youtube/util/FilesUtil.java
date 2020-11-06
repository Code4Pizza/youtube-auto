package org.youtube.util;

import org.webfilm.entity.Video;
import org.youtube.entities.YoutubeAccount;
import org.youtube.entities.YoutubeChannel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FilesUtil {
    public static List<YoutubeAccount> getAccountsFromFile(String fileName) {
        List<YoutubeAccount> accounts = new ArrayList<>();
        String path = System.getProperty("user.dir");

        try {
            File file = new File(path + "/src/main/resources/" + fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                String[] accText = line.split("\t");
                YoutubeAccount account = new YoutubeAccount();
                account.setEmail(accText[0]);
                account.setPassword(accText[1]);
                account.setBackupEmail(accText[2]);
                accounts.add(account);
                line = br.readLine();
            }
        } catch (IOException e) {
            LogUtil.severe(e.getMessage());
        }
        return accounts;

    }


    public static List<String> getChannelsFromFile(String fileName) {
        List<String> channelIds = new ArrayList<>();
        String path = System.getProperty("user.dir");

        try {
            File file = new File(path + "/src/main/resources/" + fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                String channelId = line.trim();
                channelIds.add(channelId);
                line = br.readLine();
            }
        } catch (IOException e) {
            LogUtil.severe(e.getMessage());
        }
        return channelIds;
    }

    public static void main(String[] args) {

    }

    public static List<Video> getVideoOfChannelFromFile(String fileName) {
        List<Video> videos = new ArrayList<>();
        String path = System.getProperty("user.dir");

        try {
            File file = new File(path + "/src/main/resources/" + fileName);
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                line = line.trim();
                String[] videoTxt = line.split("\t");
                Video v = new Video();
                v.setChannelUID(videoTxt[0]);
                v.setYoutubeId(videoTxt[1]);
                videos.add(v);
                line = br.readLine();
            }
        } catch (IOException e) {
            LogUtil.severe(e.getMessage());
        }
        return videos;
    }
}
