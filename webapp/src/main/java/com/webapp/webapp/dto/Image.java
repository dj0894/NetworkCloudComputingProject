package com.webapp.webapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
import java.util.UUID;

public class Image {
    private String fileName;
    private String s3ObjectName;
    private String fileId;
    private Date createdDate;
    private String userId;


    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @JsonProperty("s3_object_name")
    public String getS3ObjectName() {
        return s3ObjectName;
    }

    public void setS3ObjectName(String s3ObjectName) {
        this.s3ObjectName = s3ObjectName;
    }

    @JsonProperty("file_id")
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @JsonProperty("created_date")
    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    @JsonProperty("user_id")
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}

