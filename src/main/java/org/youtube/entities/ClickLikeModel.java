package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@NoArgsConstructor
public class ClickLikeModel {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String videoUrl;

    @JsonProperty
    private Date clickTime;


    public ClickLikeModel(Integer id, String userId, String videoUrl, Date clickTime) {
        this.id = id;
        this.userId = userId;
        this.videoUrl = videoUrl;
        this.clickTime = clickTime;
    }
}
