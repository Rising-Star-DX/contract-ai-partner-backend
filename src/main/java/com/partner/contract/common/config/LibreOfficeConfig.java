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

//    @Value("${libreoffice.home}")
//    private String libreofficeHome;
//
//    @Value("${libreoffice.port}")
//    private Integer libreofficePort;

    private OfficeManager officeManager;

    @Bean
    public OfficeManager officeManager() {
//        officeManager = LocalOfficeManager.builder()
//                .officeHome(libreofficeHome)
//                .portNumbers(libreofficePort)
//                .build();
        officeManager = LocalOfficeManager.builder()
                .install()
                .build();
        try {
            officeManager.start();
        } catch (OfficeException e) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }
        return officeManager;
    }

    @PreDestroy
    public void destroy() {
        try {
            if(officeManager != null) {
                officeManager.stop();
            }
        } catch (OfficeException e) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }
    }
}
