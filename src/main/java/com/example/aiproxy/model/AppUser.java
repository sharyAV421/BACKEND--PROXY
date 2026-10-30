package com.example.aiproxy.model;

import jakarta.persistence.*;
import java.time.YearMonth;

@Entity
@Table(name = "app_users")
public class AppUser {
    @Id private String id;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Plan plan = Plan.FREE;
    @Column(nullable = false) private long tokensUsed;
    @Column(nullable = false) private String quotaMonth = YearMonth.now().toString();
    protected AppUser() {}
    public AppUser(String id) { this.id = id; }
    public String getId() { return id; }
    public Plan getPlan() { return plan; }
    public void setPlan(Plan plan) { this.plan = plan; }
    public long getTokensUsed() { return tokensUsed; }
    public void setTokensUsed(long tokensUsed) { this.tokensUsed = tokensUsed; }
    public String getQuotaMonth() { return quotaMonth; }
    public void setQuotaMonth(String quotaMonth) { this.quotaMonth = quotaMonth; }
}
