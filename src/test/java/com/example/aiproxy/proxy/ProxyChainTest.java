package com.example.aiproxy.proxy;

import com.example.aiproxy.dto.*;
import com.example.aiproxy.exception.*;
import com.example.aiproxy.model.*;
import com.example.aiproxy.repository.*;
import com.example.aiproxy.service.*;
import com.example.aiproxy.controller.QuotaController;
import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProxyChainTest {
    @Test
    void rateLimitDelegatesToQuotaAndRejectsTheEleventhFreeRequest() {
        UserService users=mock(UserService.class);
        AppUser user=new AppUser("u-1");
        when(users.getOrCreate("u-1")).thenReturn(user);
        AtomicInteger calls=new AtomicInteger();
        QuotaProxy quota=mock(QuotaProxy.class);
        when(quota.generate(any())).thenAnswer(invocation -> { calls.incrementAndGet(); return new GenerationResponse("ok", 2); });
        RateLimitProxy rate=new RateLimitProxy(quota, users, Clock.fixed(Instant.parse("2026-10-30T12:34:15Z"), ZoneOffset.UTC));
        GenerationRequest request=new GenerationRequest("prompt", "u-1");
        for (int i=0;i<10;i++) rate.generate(request);
        assertThrows(RateLimitException.class, () -> rate.generate(request));
        assertEquals(10, calls.get());
        RateLimitException rejection=assertThrows(RateLimitException.class, () -> rate.generate(request));
        assertEquals(45, rejection.getRetryAfter());
    }

    @Test
    void simultaneousRequestsCannotExceedPlanLimitAndUsersHaveSeparateCounters() throws Exception {
        UserService users=mock(UserService.class);
        AppUser first=new AppUser("first"); first.setPlan(Plan.PRO);
        AppUser second=new AppUser("second"); second.setPlan(Plan.PRO);
        when(users.getOrCreate("first")).thenReturn(first);
        when(users.getOrCreate("second")).thenReturn(second);
        AtomicInteger delegated=new AtomicInteger();
        RateLimitProxy rate=new RateLimitProxy(request -> { delegated.incrementAndGet(); return new GenerationResponse("ok", 0); }, users, Clock.systemUTC());
        ExecutorService pool=Executors.newFixedThreadPool(120);
        CountDownLatch start=new CountDownLatch(1);
        var jobs=new java.util.ArrayList<Future<Boolean>>();
        for (int i=0;i<120;i++) {
            final String id=i<100 ? "first" : "second";
            jobs.add(pool.submit(() -> { start.await(); try { rate.generate(new GenerationRequest("p", id)); return true; } catch (RateLimitException e) { return false; } }));
        }
        start.countDown();
        int accepted=0;
        for (Future<Boolean> job : jobs) if (job.get()) accepted++;
        pool.shutdownNow();
        assertEquals(80, accepted);
        assertEquals(80, delegated.get());
    }

    @Test
    void quotaRecordsUsageAfterDelegatingAndRejectsWhenExhausted() {
        UserService users=mock(UserService.class);
        UserRepository userRepository=mock(UserRepository.class);
        UsageRecordRepository usage=mock(UsageRecordRepository.class);
        AppUser user=new AppUser("u-2");
        user.setTokensUsed(49_999);
        when(users.getOrCreate("u-2")).thenReturn(user);
        when(userRepository.findByIdForUpdate("u-2")).thenReturn(java.util.Optional.of(user));
        AIGenerationService mockService=request -> new GenerationResponse("ok", 0);
        QuotaProxy proxy=new QuotaProxy(new MockAIGenerationService() { @Override public GenerationResponse generate(GenerationRequest r) { return mockService.generate(r); } }, users, userRepository, usage, new TokenEstimator());
        assertThrows(QuotaExceededException.class, () -> proxy.generate(new GenerationRequest("abcd", "u-2")));
        verifyNoInteractions(usage);
    }

    @Test
    void successfulGenerationUpdatesMonthlyAndDailyUsage() {
        UserService users=mock(UserService.class);
        UserRepository userRepository=mock(UserRepository.class);
        UsageRecordRepository usage=mock(UsageRecordRepository.class);
        AppUser user=new AppUser("u-3");
        when(users.getOrCreate("u-3")).thenReturn(user);
        when(userRepository.findByIdForUpdate("u-3")).thenReturn(java.util.Optional.of(user));
        QuotaProxy proxy=new QuotaProxy(request -> new GenerationResponse("ok", 0), users, userRepository, usage, new TokenEstimator());
        GenerationResponse response=proxy.generate(new GenerationRequest("abcd", "u-3"));
        assertEquals(2, response.tokensConsumed());
        assertEquals(2, user.getTokensUsed());
        verify(userRepository).save(user);
        verify(usage).save(any());
    }

    @Test
    void upgradeChangesFreePlanToPro() {
        UserService users=mock(UserService.class);
        UserRepository userRepository=mock(UserRepository.class);
        AppUser user=new AppUser("u-4");
        when(users.getOrCreate("u-4")).thenReturn(user);
        QuotaController controller=new QuotaController(users, userRepository, mock(UsageRecordRepository.class));
        controller.upgrade("u-4");
        assertEquals(Plan.PRO, user.getPlan());
        verify(userRepository).save(user);
    }
}
