package org.youtube.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Date;


@JsonInclude(JsonInclude.Include.NON_DEFAULT)

public class Banner {

    @JsonProperty
    private Integer id;

    @JsonProperty
    private Integer organizationId;

    @JsonProperty
    private String title;

    @JsonProperty
    private Date createdTime;

    @JsonProperty
    private Date startTime;

    @JsonProperty
    private Date endTime;

    @JsonProperty
    private Integer type;

    public Banner() {
    }

    public Banner(String title, Date createdTime,
                  Date startTime, Date endTime, Integer type) {
        this.title = title;
        this.createdTime = createdTime;
        this.startTime = startTime;
        this.endTime = endTime;
        this.type = type;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }
}
