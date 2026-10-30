package com.example.aiproxy.model;

public enum Plan {
    FREE(10, 50_000), PRO(60, 500_000), ENTERPRISE(Integer.MAX_VALUE, Long.MAX_VALUE);
    private final int requestsPerMinute;
    private final long monthlyTokenLimit;
    Plan(int requestsPerMinute, long monthlyTokenLimit) { this.requestsPerMinute = requestsPerMinute; this.monthlyTokenLimit = monthlyTokenLimit; }
    public int getRequestsPerMinute() { return requestsPerMinute; }
    public long getMonthlyTokenLimit() { return monthlyTokenLimit; }
}
