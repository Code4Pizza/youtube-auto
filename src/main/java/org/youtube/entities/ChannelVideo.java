package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class ChannelVideo {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private Integer channelId;

    @JsonProperty
    private String videoUrl;

    @JsonProperty
    private Integer duration;

    @JsonProperty
    private String title;


    public ChannelVideo(Integer id, Integer channelId, String videoUrl, Integer duration, String title) {
        this.id = id;
        this.channelId = channelId;
        this.videoUrl = videoUrl;
        this.duration = duration;
        this.title = title;
    }
}
