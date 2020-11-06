package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@NoArgsConstructor
public class YoutubeChannel {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private String email;

    @JsonProperty
    private String channelUrl;

    @JsonProperty
    private String channelId;

    @JsonProperty
    private String channelName;

    public YoutubeChannel(Integer id, String email, String channelName) {
        this.id = id;
        this.email = email;
        this.channelName = channelName;
    }

}
