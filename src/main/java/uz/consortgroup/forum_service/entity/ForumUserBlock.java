package uz.consortgroup.forum_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.consortgroup.core.api.v1.dto.forum.enumeration.ComplaintReason;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "forum_user_block", schema = "forum_schema")
public class ForumUserBlock {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name="forum_id", nullable=false)
    private UUID forumId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "reason", nullable = false)
    @Enumerated(EnumType.STRING)
    private ComplaintReason reason;

    @Column(name = "issued_by", nullable = false)
    private UUID issuedBy;

    @Column(name = "violation_count", nullable = false)
    private Integer violationCount;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    void onCreate() {
        if (createdAt == null) createdAt = Instant.now();
    }
}
