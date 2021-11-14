package com.thoughtworks.auctionservice.repository.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.Instant;

@Entity
public class SignedServiceEntity {
    @Id
    @Column
    private String id;
    @Column(name = "belongs_to", nullable = false)
    private String belongsTo;
    @Column(name = "proposal_id", nullable = false)
    private String proposalId;
    @Column(name = "created_at", precision = 3, nullable = false)
    private Instant createdAt;
    @Column(name = "end_at", precision = 3)
    private Instant endAt;
    @Column(name = "end_reason")
    private String endReason;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBelongsTo() {
        return belongsTo;
    }

    public void setBelongsTo(String belongsTo) {
        this.belongsTo = belongsTo;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getEndAt() {
        return endAt;
    }

    public void setEndAt(Instant endAt) {
        this.endAt = endAt;
    }

    public String getEndReason() {
        return endReason;
    }

    public void setEndReason(String endReason) {
        this.endReason = endReason;
    }
}
