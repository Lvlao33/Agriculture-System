package com.farmporject.backend.expert.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class AppointmentDTO {

    private Long id;
    private Long expertId;
    private String expertName;
    private Long userId;
    private String userName;
    private String userContact;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    private String description;
    private String status; // 使用 String 来表示状态，以便后期扩展
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public AppointmentDTO() {
    }

    public AppointmentDTO(String userName, String userContact, LocalDateTime startTime, LocalDateTime endTime,
            String description,
            Long expertId, Long userId, String status, LocalDateTime createTime, LocalDateTime updateTime,
            String expertName, Long id) {
        this.userName = userName;
        this.userContact = userContact;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.expertId = expertId;
        this.userId = userId;
        this.status = status;
        this.createTime = createTime;
        this.updateTime = updateTime;
        this.expertName = expertName;
        this.id = id;
    }

    // setters and getters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserContact() {
        return userContact;
    }

    public void setUserContact(String userContact) {
        this.userContact = userContact;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getExpertId() {
        return expertId;
    }

    public void setExpertId(Long expertId) {
        this.expertId = expertId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }
}
