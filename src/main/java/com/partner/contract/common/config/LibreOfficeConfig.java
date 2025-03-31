package com.partner.contract.common.config;

import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@Slf4j
public class LibreOfficeConfig {

    @Value("${libreoffice.path}")
    private String libreOfficePath;

    private OfficeManager officeManager;

    @Bean
    public Optional<OfficeManager> officeManager() {
        if (isLibreOfficeInstalled()) {
            officeManager = LocalOfficeManager.builder().build();
            try {
                if (officeManager != null && !officeManager.isRunning()) {
                    officeManager.start();
                }
            } catch (OfficeException e) {
                throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
            }
        }
        return Optional.ofNullable(officeManager);
    }

    @PreDestroy
    public void destroy() {
        try {
            if(isLibreOfficeInstalled()) {
                if (officeManager != null && officeManager.isRunning()) {
                    officeManager.stop();
                }
            }
        } catch (OfficeException e) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }
    }

    private boolean isLibreOfficeInstalled() {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder(libreOfficePath, "--version");
            processBuilder.environment().put("PATH", "/opt/libreoffice25.2/program:" + System.getenv("PATH"));
            log.info("현재 PATH 환경변수: {}", System.getenv("PATH"));
            Process process = processBuilder.start();
            int exitCode = process.waitFor();
            return exitCode == 0;
        } catch (Exception e) {
            log.error("LibreOffice 명령어 실행 요청 중 문제가 발생했습니다. {}", e.getMessage(), e);
            return false;
        }
    }
}
