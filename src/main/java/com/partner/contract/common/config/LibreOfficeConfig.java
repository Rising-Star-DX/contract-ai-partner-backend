package com.partner.contract.common.config;

import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class LibreOfficeConfig {

    @Value("${libreoffice.path}")
    private String libreOfficePath;

    private OfficeManager officeManager;

    @Bean
    public OfficeManager officeManager() {
//        if (isLibreOfficeInstalled()) {
//            officeManager = LocalOfficeManager.builder().build();
//            if (officeManager != null && !officeManager.isRunning()) {
//                log.info("officeManager 시작");
//                //officeManager.start();
//            }
////            try {
////                if (officeManager != null && !officeManager.isRunning()) {
////                    log.info("officeManager 시작");
////                    //officeManager.start();
////                }
////            } catch (OfficeException e) {
////                throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
////            }
//            log.info("officeManager 생성 - officeManager 존재 여부: {}", officeManager != null);
//            return officeManager;
//        }
//        return null;
        return null;
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
        return true;
//        try {
//            ProcessBuilder processBuilder = new ProcessBuilder(libreOfficePath, "--version");
//            processBuilder.environment().put("PATH", "/opt/libreoffice25.2/program:" + System.getenv("PATH"));
//            processBuilder.environment().put("LD_LIBRARY_PATH", "/opt/libreoffice25.2/lib:" + System.getenv("LD_LIBRARY_PATH"));
//            log.info("현재 PATH 환경변수: {}", processBuilder.environment().get("PATH"));
//            log.info("현재 LD_LIBRARY_PATH 환경변수: {}", processBuilder.environment().get("LD_LIBRARY_PATH"));
//            Process process = processBuilder.start();
//            int exitCode = process.waitFor();
//            log.info("LibreOffice 설치 여부: {}", exitCode);
//            return exitCode == 0;
//        } catch (Exception e) {
//            log.error("LibreOffice 명령어 실행 요청 중 문제가 발생했습니다. {}", e.getMessage(), e);
//            return false;
//        }
    }
}
