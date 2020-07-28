package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class YoutubeAccount {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private String email;

    @JsonProperty
    private String password;

    @JsonProperty
    private String backupEmail;

    @JsonProperty
    private int enable;

    public YoutubeAccount() {
    }

    public YoutubeAccount(String email, String password, String backupEmail) {
        this.email = email;
        this.password = password;
        this.backupEmail = backupEmail;
    }

    public YoutubeAccount(Integer id, String email, String password, String backupEmail, int enable) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.backupEmail = backupEmail;
        this.enable = enable;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBackupEmail() {
        return backupEmail;
    }

    public void setBackupEmail(String backupEmail) {
        this.backupEmail = backupEmail;
    }

    public int getEnable() {
        return enable;
    }

    public void setEnable(int enable) {
        this.enable = enable;
    }
}
