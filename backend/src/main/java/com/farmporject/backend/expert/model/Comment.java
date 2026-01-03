package com.farmporject.backend.expert.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Comment Entity
 * Represents a comment on a knowledge article
 */
@Entity
@Table(name = "comment")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Related to Knowledge
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "knowledge_id", nullable = false)
    private Knowledge knowledge;

    @Column(nullable = false, length = 1000)
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(nullable = false)
    private LocalDateTime createTime;

    public Comment() {
    }

    public Comment(Knowledge knowledge, String content) {
        this.knowledge = knowledge;
        this.content = content;
    }

    @PrePersist
    protected void onCreate() {
        this.createTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Knowledge getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(Knowledge knowledge) {
        this.knowledge = knowledge;
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
}
