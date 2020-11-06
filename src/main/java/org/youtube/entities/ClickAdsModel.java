package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class ClickAdsModel {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private String userId;

    @JsonProperty
    private String videoUrl;

    @JsonProperty
    private String adsUrl;

    @JsonProperty
    private Date clickTime;


    public ClickAdsModel(Integer id, String userId,
                         String videoUrl, String adsUrl, Date clickTime) {
        this.id = id;
        this.userId = userId;
        this.videoUrl = videoUrl;
        this.adsUrl = adsUrl;
        this.clickTime = clickTime;
    }
}
