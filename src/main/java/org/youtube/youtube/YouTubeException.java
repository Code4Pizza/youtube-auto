package org.youtube.youtube;

public class YouTubeException extends Exception {

	public YouTubeException(String message) {
		super(message);
	}

	public static class YouTubeFailedToPlayException extends YouTubeException {
		public YouTubeFailedToPlayException() {
			super("Failed to play video");
		}
	}
}
