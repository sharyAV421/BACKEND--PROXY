package com.example.aiproxy.repository;
import com.example.aiproxy.model.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
    @Query("select u.usageDate, sum(u.tokens) from UsageRecord u where u.userId = :userId and u.usageDate >= :fromDate group by u.usageDate order by u.usageDate")
    List<Object[]> dailyUsage(@Param("userId") String userId, @Param("fromDate") LocalDate fromDate);
}
