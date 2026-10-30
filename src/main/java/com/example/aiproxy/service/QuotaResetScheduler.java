package com.example.aiproxy.service;

import com.example.aiproxy.model.AppUser;
import com.example.aiproxy.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.YearMonth;

@Component
public class QuotaResetScheduler {
    private final UserRepository users;
    public QuotaResetScheduler(UserRepository users) { this.users=users; }
    @Scheduled(cron="0 0 0 1 * *")
    @Transactional
    public void resetMonthlyQuotas() {
        String month=YearMonth.now().toString();
        var allUsers=users.findAll();
        for (AppUser user : allUsers) { user.setTokensUsed(0); user.setQuotaMonth(month); }
        users.saveAll(allUsers);
    }
}
