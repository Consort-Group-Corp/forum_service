package uz.consortgroup.forum_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ComplaintReason;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ComplaintStatus;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "forum_complaint", schema = "forum_schema")
public class ForumComplaint {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "forum_id", nullable = false)
    private Forum forum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private ForumTopic topic;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id")
    private ForumComment comment;

    @Column(name = "reporter_id", nullable = false)
    private UUID reporterId;

    @Column(name = "offender_id", nullable = false)
    private UUID offenderId;

    @Column(name = "reason", nullable = false, length = 120)
    @Enumerated(EnumType.STRING)
    private ComplaintReason reason;

    @Column(name = "status", nullable = false, length = 32)
    @Enumerated(EnumType.STRING)
    private ComplaintStatus status;

    @Column(name = "message_snapshot", columnDefinition = "text")
    private String messageSnapshot;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "resolved_at")
    private Instant resolvedAt;

    @Column(name = "resolved_by")
    private UUID resolvedBy;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
