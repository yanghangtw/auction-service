package com.thoughtworks.auctionservice.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class RequestProposalEntity {
    @Id
    @Column
    private String id;
    @Column(name = "request_id", nullable = false)
    private String requestId;
    @Column(nullable = false)
    private String content;
    @Column(name = "belongs_to", nullable = false)
    private String belongsTo;
    @Column(name = "created_at", precision = 3, nullable = false)
    private Instant createdAt;
    @Column(name = "expired_at", precision = 3, nullable = false)
    private Instant expiredAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(String belongsTo) {
        this.belongsTo = belongsTo;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }
}
