package com.example.aiproxy.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "usage_records", indexes = @Index(name = "idx_usage_user_date", columnList = "userId, usageDate"))
public class UsageRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String userId;
    @Column(nullable = false) private LocalDate usageDate;
    @Column(nullable = false) private LocalDateTime createdAt;
    @Column(nullable = false) private long tokens;
    protected UsageRecord() {}
    public UsageRecord(String userId, long tokens) { this.userId=userId; this.tokens=tokens; this.usageDate=LocalDate.now(); this.createdAt=LocalDateTime.now(); }
    public String getUserId() { return userId; }
    public LocalDate getUsageDate() { return usageDate; }
    public long getTokens() { return tokens; }
}
