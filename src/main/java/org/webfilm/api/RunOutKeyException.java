package org.webfilm.api;

public class RunOutKeyException extends Exception {

    public RunOutKeyException() {
        super("Run out of api key");
    }
}
