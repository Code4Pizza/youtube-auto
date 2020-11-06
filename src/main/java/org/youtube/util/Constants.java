package org.youtube.util;

public class Constants {

    public static final String METRICS_NAME = "youtube-bot";

    public static final String SCHEMA = "yt_bot";
    public static final String HOST_PORT = "35.240.231.255:3306";
    public static final String BASE_URL =
            String.format("jdbc:mysql://%s/%s?autoReconnect=true&useSSL=false", HOST_PORT, SCHEMA);

    public static final String USER_NAME = "arago";
    public static final String PASSWORD = "Qwer!234";
        public static final String DB_NAME = "arago";

    public static final int DEFAULT_DELAY_SECOND = 15;
    public static final int DEFAULT_DELAY_MILLIS = 5000;
}
