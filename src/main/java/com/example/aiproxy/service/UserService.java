package com.example.aiproxy.service;

import com.example.aiproxy.model.*;
import com.example.aiproxy.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.YearMonth;

@Service
public class UserService {
    private final UserRepository users;
    public UserService(UserRepository users) { this.users=users; }
    @Transactional
    public AppUser getOrCreate(String id) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("X-User-Id header is required.");
        AppUser user=users.findById(id).orElseGet(() -> users.save(new AppUser(id)));
        resetIfNewMonth(user);
        return user;
    }
    public void resetIfNewMonth(AppUser user) {
        String current=YearMonth.now().toString();
        if (!current.equals(user.getQuotaMonth())) { user.setQuotaMonth(current); user.setTokensUsed(0); users.save(user); }
    }
}
