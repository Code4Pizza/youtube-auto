package org.webfilm.api;

public class RetryException extends Exception {
    public RetryException(String message) {
        super(message);
    }
}