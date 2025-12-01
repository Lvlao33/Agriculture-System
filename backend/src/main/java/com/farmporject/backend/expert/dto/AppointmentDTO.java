package com.farmporject.backend.expert.dto;

import java.time.LocalDateTime;

public class AppointmentDTO {
    private Long userId;
    private String userName;
    private String userContact;
    private LocalDateTime appointmentTime;
    private String description;
    private Long expertId;

    public AppointmentDTO() {
    }

    public AppointmentDTO(String userName, String userContact, LocalDateTime appointmentTime, String description,
            Long expertId, Long userId) {
        this.userName = userName;
        this.userContact = userContact;
        this.appointmentTime = appointmentTime;
        this.description = description;
        this.expertId = expertId;
        this.userId = userId;
    }

    // setters and getters
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

    public LocalDateTime getAppointmentTime() {
        return appointmentTime;
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        this.appointmentTime = appointmentTime;
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
}
