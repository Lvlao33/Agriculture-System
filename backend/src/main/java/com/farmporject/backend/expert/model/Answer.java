package com.farmporject.backend.expert.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", nullable = false)
    private Expert expert;

    @Column(name = "content", length = 5000, nullable = false)
    private String content;

    @Column(nullable = false)
    private Integer viewCount = 0;

    @Column(nullable = false)
    private Integer likeCount = 0;

    @Column(name = "create_time")
    private LocalDateTime createTime;

    // 默认构造函数
    public Answer() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public Expert getExpert() {
        return expert;
    }

    public void setExpert(Expert expert) {
        this.expert = expert;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Answer))
            return false;
        Answer answer = (Answer) o;
        return Objects.equals(id, answer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Answer{" +
                "id=" + id +
                ", question=" + question +
                ", expert=" + expert +
                ", content='" + content + '\'' +
                ", viewCount=" + viewCount +
                ", likeCount=" + likeCount +
                ", createTime=" + createTime +
                '}';
    }
}