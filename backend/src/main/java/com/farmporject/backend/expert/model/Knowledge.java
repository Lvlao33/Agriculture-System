package com.farmporject.backend.expert.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "knowledge")
public class Knowledge {
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "knowledge_id")
private Long knowledgeId; 

    @Column(nullable = false)
    private String title;

    @Column(length = 5000, nullable = false)
    private String content;

    @Column(length = 1000)
    private String summary;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "knowledge_categories", joinColumns = @JoinColumn(name = "knowledge_id"))
    @Column(name = "category")
    private List<String> categories;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "knowledge_tags", joinColumns = @JoinColumn(name = "knowledge_id"))
    @Column(name = "tag")
    private List<String> tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private Expert author;

    private String source;

    // ����ͼƬ����ѡ��
    @Column(name = "pic_path", length = 500)
    private String picPath;

    // ����ⲿ���ӣ���ѡ��
    @Column(name = "url", length = 500)
    private String url;

    @Column(name = "view_count", nullable = false)
    private Integer viewCount = 0;

    @Column(name = "like_count", nullable = false)
    private Integer likeCount = 0;

    @Column(name = "is_published", nullable = false)
    private Boolean isPublished = true;

    @Column(name = "create_time", nullable = false)
    private LocalDateTime createTime;

    @Column(name = "update_time", nullable = false)
    private LocalDateTime updateTime;

    // 榛樿鏋勯€犲嚱�??
    public Knowledge() {
    }

    // 鍏ㄥ弬鏋勯€犲嚱�??
    public Knowledge(Long knowledgeId, String title, String content, String summary,
            List<String> categories, List<String> tags, Expert author, String source,
            String picPath, String url,
            Integer viewCount, Integer likeCount, Boolean isPublished,
            LocalDateTime createTime, LocalDateTime updateTime) {
        this.knowledgeId = knowledgeId;
        this.title = title;
        this.content = content;
        this.summary = summary;
        this.categories = categories;
        this.tags = tags;
        this.author = author;
        this.source = source;
        this.picPath = picPath;
        this.url = url;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.isPublished = isPublished;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    // Getters and Setters
    public Long getKnowledgeId() {
        return knowledgeId;
    }

    public void setKnowledgeId(Long knowledgeId) {
        this.knowledgeId = knowledgeId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public Expert getAuthor() {
        return author;
    }

    public void setAuthor(Expert author) {
        this.author = author;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getPicPath() {
        return picPath;
    }

    public void setPicPath(String picPath) {
        this.picPath = picPath;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
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

    public Boolean getIsPublished() {
        return isPublished;
    }

    public void setIsPublished(Boolean isPublished) {
        this.isPublished = isPublished;
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
        if (!(o instanceof Knowledge))
            return false;
        Knowledge knowledge = (Knowledge) o;
        return Objects.equals(knowledgeId, knowledge.knowledgeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(knowledgeId);
    }

    @Override
    public String toString() {
        return "Knowledge{" +
                "knowledgeId=" + knowledgeId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", viewCount=" + viewCount +
                ", isPublished=" + isPublished +
                '}';
    }
}