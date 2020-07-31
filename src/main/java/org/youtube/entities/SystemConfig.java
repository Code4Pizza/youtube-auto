package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)

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

    public String getKeyValue() {
        return keyValue;
    }

    public void setKeyValue(String keyValue) {
        this.keyValue = keyValue;
    }

    public String getKeyName() {
        return keyName;
    }

    public void setKeyName(String keyName) {
        this.keyName = keyName;
    }

    public String getKeyDescription() {
        return keyDescription;
    }

    public void setKeyDescription(String keyDescription) {
        this.keyDescription = keyDescription;
    }
}
