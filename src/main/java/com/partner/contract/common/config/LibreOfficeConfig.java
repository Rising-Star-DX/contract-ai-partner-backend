package com.partner.contract.common.config;

import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import jakarta.annotation.PreDestroy;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibreOfficeConfig {

    private OfficeManager officeManager;

    @Bean
    public OfficeManager officeManager() {
//        officeManager = LocalOfficeManager.builder()
//                .officeHome(libreofficeHome)
//                .portNumbers(libreofficePort)
//                .build();
        officeManager = LocalOfficeManager.builder().build();

        try {
            if(isLibreOfficeInstalled()) {
                if (officeManager != null && !officeManager.isRunning()) {
                    officeManager.start();
                }
            }
        } catch (OfficeException e) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }
        return officeManager;
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
            // "libreoffice --version" 명령어를 실행하여 설치 여부를 확인
            Process process = new ProcessBuilder("libreoffice", "--version").start();
            int exitCode = process.waitFor();
            System.out.println(exitCode);
            return exitCode == 0; // 명령어 실행 성공 여부 (0이면 설치됨)
        } catch (Exception e) {
            return false; // 예외가 발생하면 설치되지 않은 것으로 간주
        }
    }
}
