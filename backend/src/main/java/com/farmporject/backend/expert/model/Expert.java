package com.farmporject.backend.expert.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

import com.farmporject.backend.user.model.User;

/**
 * 专家实体类
 * 通过组合关系关联User，拥有独立的ID
 */
@Entity
@Table(name = "experts")
public class Expert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String title;

    private String avatar;

    @Column(length = 1000)
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "expert_specialties", joinColumns = @JoinColumn(name = "expert_id"))
    @Column(name = "specialty")
    private List<String> specialties;

    private Integer experienceYears;

    private String contactInfo;

    @Column(nullable = false)
    private Boolean isAvailable = true;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    // 默认构造函数
    public Expert() {
    }

    // 全参构造函数
    public Expert(User user, String name, String title, String avatar, String description,
            List<String> specialties, Integer experienceYears, String contactInfo,
            Boolean isAvailable) {
        this.user = user;
        this.name = name;
        this.title = title;
        this.avatar = avatar;
        this.description = description;
        this.specialties = specialties;
        this.experienceYears = experienceYears;
        this.contactInfo = contactInfo;
        this.isAvailable = isAvailable;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Expert))
            return false;
        Expert expert = (Expert) o;
        return Objects.equals(id, expert.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Expert{" +
                "id=" + id +
                ", userId=" + (user != null ? user.getId() : null) +
                ", name='" + name + '\'' +
                ", title='" + title + '\'' +
                ", isAvailable=" + isAvailable +
                '}';
    }
}