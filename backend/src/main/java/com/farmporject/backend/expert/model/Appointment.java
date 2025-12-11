package com.farmporject.backend.expert.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

import com.farmporject.backend.user.model.User;

@Entity
@Table(name = "appointments")
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = true)
    private Expert expert;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String userName;
    private String userContact;

    @Column(nullable = false)
    private LocalDateTime appointmentStartTime;

    @Column(nullable = false)
    private LocalDateTime appointmentEndTime;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    private AppointmentStatus status = AppointmentStatus.PENDING;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    public enum AppointmentStatus {
        PENDING, CONFIRMED, COMPLETED, CANCELLED
    }

    // 默认构造函数
    public Appointment() {
    }

    // 全参构造函数
    public Appointment(Long id, Expert expert, User user, String userName,
            String userContact, LocalDateTime appointmentStartTime, LocalDateTime appointmentEndTime,
            String description,
            AppointmentStatus status, LocalDateTime createTime, LocalDateTime updateTime) {
        this.id = id;
        this.expert = expert;
        this.user = user;

        this.userName = userName;
        this.userContact = userContact;
        this.appointmentStartTime = appointmentStartTime;
        this.appointmentEndTime = appointmentEndTime;
        this.description = description;
        this.status = status;
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

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDateTime getAppointmentStartTime() {
        return appointmentStartTime;
    }

    public void setAppointmentStartTime(LocalDateTime appointmentStartTime) {
        this.appointmentStartTime = appointmentStartTime;
    }

    public LocalDateTime getAppointmentEndTime() {
        return appointmentEndTime;
    }

    public void setAppointmentEndTime(LocalDateTime appointmentEndTime) {
        this.appointmentEndTime = appointmentEndTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public AppointmentStatus getStatus() {
        return status;
    }

    public void setStatus(AppointmentStatus status) {
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
        if (!(o instanceof Appointment))
            return false;
        Appointment that = (Appointment) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", expert=" + expert +
                ", user=" + user +
                ", userName='" + userName + '\'' +
                ", userContact='" + userContact + '\'' +
                ", appointmentStartTime=" + appointmentStartTime +
                ", appointmentEndTime=" + appointmentEndTime +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}