package org.youtube.google;

public class GoogleException extends Exception {

    public GoogleException(String message) {
        super(message);
    }

    @SuppressWarnings("serial")
    public static class GoogleSignInNotFoundException extends GoogleException {

        public GoogleSignInNotFoundException() {
            super("Failed to find and click button sign in");
        }
    }

    @SuppressWarnings("serial")
    public static class GoogleInputLoginNotFoundException extends GoogleException {

        public GoogleInputLoginNotFoundException() {
            super("Button next step to login not found");
        }
    }

    @SuppressWarnings("serial")
    public static class GoogleSignOutNotFoundException extends GoogleException {

        public GoogleSignOutNotFoundException() {
            super("Failed to find and click button sign out");
        }
    }

    public static class GoogleDetectInsecureBrowserException extends GoogleException {

        public GoogleDetectInsecureBrowserException() {
            super("This browser or app may not be secure");
        }
    }
}
