package com.partner.contract.common.config;

import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LibreOfficeConfig {

    @Value("${libreoffice.home}")
    private String libreofficeHome;

    @Value("${libreoffice.port}")
    private Integer libreofficePort;

    @Bean
    public OfficeManager officeManager() {
        return LocalOfficeManager.builder()
                .officeHome(libreofficeHome)
                .portNumbers(libreofficePort)
                .build();
    }
}
