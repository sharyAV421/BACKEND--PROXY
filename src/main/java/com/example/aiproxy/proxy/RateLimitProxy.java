package com.example.aiproxy.proxy;

import com.example.aiproxy.dto.*;
import com.example.aiproxy.exception.RateLimitException;
import com.example.aiproxy.model.AppUser;
import com.example.aiproxy.model.Plan;
import com.example.aiproxy.service.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Service
public class RateLimitProxy implements AIGenerationService {
    private final AIGenerationService next;
    private final UserService users;
    private final Clock clock;
    private final ConcurrentHashMap<String, AtomicInteger> requests = new ConcurrentHashMap<>();
    public RateLimitProxy(@Qualifier("quotaProxy") AIGenerationService next, UserService users, Clock clock) { this.next=next; this.users=users; this.clock=clock; }
    @Override public GenerationResponse generate(GenerationRequest request) {
        AppUser user=users.getOrCreate(request.userId());
        if (user.getPlan() == Plan.ENTERPRISE) return next.generate(request);
        AtomicInteger count=requests.computeIfAbsent(user.getId(), ignored -> new AtomicInteger());
        if (count.incrementAndGet() > user.getPlan().getRequestsPerMinute()) {
            count.decrementAndGet();
            LocalDateTime now=LocalDateTime.now(clock);
            long remainingMillis=Duration.between(now, now.truncatedTo(ChronoUnit.MINUTES).plusMinutes(1)).toMillis();
            throw new RateLimitException(Math.max(1, (remainingMillis+999)/1000));
        }
        return next.generate(request);
    }
    @Scheduled(cron="0 * * * * *")
    public void resetCounters() { requests.clear(); }
}
