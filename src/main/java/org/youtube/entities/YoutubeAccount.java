package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class YoutubeAccount {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private String email;

    @JsonProperty
    private String password;

    @JsonProperty
    private String backupEMail;

    public YoutubeAccount(Integer id, String email, String password, String backupEMail) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.backupEMail = backupEMail;
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

    public String getBackupEMail() {
        return backupEMail;
    }

    public void setBackupEMail(String backupEMail) {
        this.backupEMail = backupEMail;
    }
}
