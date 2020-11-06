package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@NoArgsConstructor
public class SystemConfig {

    @JsonProperty
    private String keyValue;

    @JsonProperty
    private String keyName;

    @JsonProperty
    private String keyDescription;

    public SystemConfig(String keyValue, String keyName, String keyDescription) {
        this.keyValue = keyValue;
        this.keyName = keyName;
        this.keyDescription = keyDescription;
    }

}
