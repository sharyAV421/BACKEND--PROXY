package com.example.aiproxy.exception;
public class QuotaExceededException extends RuntimeException {
    public QuotaExceededException() { super("Monthly token quota exhausted."); }
}
