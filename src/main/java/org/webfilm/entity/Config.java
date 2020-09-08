package org.webfilm.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jdbi.v3.core.mapper.reflect.ColumnName;

public class Config {

    @JsonProperty("config_key")
    private String configKey;
    @JsonProperty("config_value")
    private String configValue;

    public Config(@ColumnName("config_key") String configKey, @ColumnName("config_value") String configValue) {
        this.configKey = configKey;
        this.configValue = configValue;
    }

    public String getConfigKey() {
        return configKey;
    }

    public String getConfigValue() {
        return configValue;
    }
}
