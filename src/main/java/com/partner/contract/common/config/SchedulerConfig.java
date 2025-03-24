package com.partner.contract.common.config;

import com.partner.contract.agreement.service.AgreementService;
import com.partner.contract.standard.service.StandardService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
public class SchedulerConfig {
    private final StandardService standardService;
    private final AgreementService agreementService;

    @Value("${schedule.use}")
    private boolean useSchedule;

    @Scheduled(cron = "${schedule.cron}")
    public void schedule() {
        if (useSchedule) {
            standardService.updateExpiredAnalysisStatus();
            agreementService.updateExpiredAnalysisStatus();
        }
    }
}
