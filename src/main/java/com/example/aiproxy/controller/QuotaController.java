package com.example.aiproxy.controller;

import com.example.aiproxy.dto.*;
import com.example.aiproxy.model.*;
import com.example.aiproxy.repository.*;
import com.example.aiproxy.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.time.*;
import java.util.*;

@RestController
@RequestMapping("/api/quota")
public class QuotaController {
    private final UserService users;
    private final UserRepository userRepository;
    private final UsageRecordRepository usage;
    public QuotaController(UserService users, UserRepository userRepository, UsageRecordRepository usage) { this.users=users; this.userRepository=userRepository; this.usage=usage; }
    @GetMapping("/status")
    public QuotaStatusResponse status(@RequestHeader("X-User-Id") String userId) {
        AppUser user=users.getOrCreate(userId);
        long limit=user.getPlan().getMonthlyTokenLimit();
        return new QuotaStatusResponse(user.getPlan(), user.getTokensUsed(), limit==Long.MAX_VALUE ? limit : Math.max(0, limit-user.getTokensUsed()), YearMonth.now().plusMonths(1).atDay(1));
    }
    @GetMapping("/history")
    public List<DailyUsageResponse> history(@RequestHeader("X-User-Id") String userId) {
        users.getOrCreate(userId);
        LocalDate first=LocalDate.now().minusDays(6);
        Map<LocalDate,Long> totals=new HashMap<>();
        for (Object[] row : usage.dailyUsage(userId, first)) totals.put((LocalDate) row[0], ((Number) row[1]).longValue());
        List<DailyUsageResponse> result=new ArrayList<>();
        for (int i=0;i<7;i++) { LocalDate date=first.plusDays(i); result.add(new DailyUsageResponse(date, totals.getOrDefault(date, 0L))); }
        return result;
    }
    @PostMapping("/upgrade")
    public Map<String,Object> upgrade(@RequestHeader("X-User-Id") String userId) {
        AppUser user=users.getOrCreate(userId);
        if (user.getPlan()==Plan.FREE) { user.setPlan(Plan.PRO); userRepository.save(user); }
        else if (user.getPlan()!=Plan.PRO) throw new IllegalArgumentException("Only FREE users can upgrade to PRO.");
        return Map.of("plan", user.getPlan(), "message", "Payment successful; plan upgraded to PRO.");
    }
}
