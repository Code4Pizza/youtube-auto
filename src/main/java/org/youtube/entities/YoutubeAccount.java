package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)
@Data
@NoArgsConstructor
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


    public YoutubeAccount(Integer id, String email, String password, String backupEmail, int enable) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.backupEmail = backupEmail;
        this.enable = enable;
    }


    public boolean isFake() {
        return enable == 1;
    }

    public static YoutubeAccount createFakeAccount() {
        YoutubeAccount youtubeAccount = new YoutubeAccount();
        youtubeAccount.email = "";
        youtubeAccount.password = "";
        youtubeAccount.backupEmail = "";
        youtubeAccount.enable = 1;
        return youtubeAccount;
    }
}
