package com.example.aiproxy.proxy;

import com.example.aiproxy.dto.*;
import com.example.aiproxy.exception.QuotaExceededException;
import com.example.aiproxy.model.AppUser;
import com.example.aiproxy.repository.UsageRecordRepository;
import com.example.aiproxy.repository.UserRepository;
import com.example.aiproxy.service.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Qualifier;

@Service
public class QuotaProxy implements AIGenerationService {
    private final AIGenerationService next;
    private final UserService userService;
    private final UserRepository users;
    private final UsageRecordRepository usage;
    private final TokenEstimator estimator;
    public QuotaProxy(@Qualifier("mockAIGenerationService") AIGenerationService next, UserService userService, UserRepository users, UsageRecordRepository usage, TokenEstimator estimator) { this.next=next; this.userService=userService; this.users=users; this.usage=usage; this.estimator=estimator; }
    @Override @Transactional
    public GenerationResponse generate(GenerationRequest request) {
        userService.getOrCreate(request.userId());
        AppUser user=users.findByIdForUpdate(request.userId()).orElseThrow(() -> new IllegalStateException("User disappeared during quota check."));
        long promptTokens=estimator.estimate(request.prompt());
        if (user.getPlan().getMonthlyTokenLimit() != Long.MAX_VALUE && user.getTokensUsed()+promptTokens > user.getPlan().getMonthlyTokenLimit()) throw new QuotaExceededException();
        GenerationResponse generated=next.generate(request);
        long consumed=promptTokens+estimator.estimate(generated.text());
        if (user.getPlan().getMonthlyTokenLimit() != Long.MAX_VALUE && user.getTokensUsed()+consumed > user.getPlan().getMonthlyTokenLimit()) throw new QuotaExceededException();
        user.setTokensUsed(user.getTokensUsed()+consumed);
        users.save(user);
        usage.save(new com.example.aiproxy.model.UsageRecord(user.getId(), consumed));
        return new GenerationResponse(generated.text(), consumed);
    }
}
