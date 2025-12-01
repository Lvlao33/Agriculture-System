package com.farmporject.backend.expert.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ExpertDTO {
    private Long id;
    private Long userId;
    private String name;
    private String title;
    private String avatar;
    private String description;
    private Integer experienceYears;
    private String contactInfo;
    private Boolean isAvailable = true;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private List<String> specialties;

    // 默认构造方法
    public ExpertDTO() {
    }

    // 全参构造函数
    public ExpertDTO(Long id, Long userId, String name, String title, String avatar,
            String description, List<String> specialties, Integer experienceYears, String contactInfo,
            Boolean isAvailable, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.title = title;
        this.avatar = avatar;
        this.description = description;
        this.specialties = specialties;
        this.experienceYears = experienceYears;
        this.contactInfo = contactInfo;
        this.isAvailable = isAvailable;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }
    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSpecialties() {
        return specialties;
    }

    public void setSpecialties(List<String> specialties) {
        this.specialties = specialties;
    }

    public Integer getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(Integer experienceYears) {
        this.experienceYears = experienceYears;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }

    public Boolean getIsAvailable() {
        return isAvailable;
    }

    public void setIsAvailable(Boolean isAvailable) {
        this.isAvailable = isAvailable;
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

}
