package com.partner.contract.common.config;

import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import jakarta.annotation.PreDestroy;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("ec2")
public class LibreOfficeConfig {

    private OfficeManager officeManager;

    @Bean
    public OfficeManager officeManager() {
        officeManager = LocalOfficeManager.builder().build();
        try {
            if (!officeManager.isRunning()) {
                officeManager.start();
            }
        } catch (OfficeException e) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }
        return officeManager;
    }

    @PreDestroy
    public void destroy() {
        try {
            if (officeManager.isRunning()) {
                officeManager.stop();
            }
        } catch (OfficeException e) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }
    }
}
