package com.partner.contract.common.service;

import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.utils.CustomMultipartFile;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.jodconverter.local.office.LocalOfficeManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Service
@Slf4j
public class FileConversionService {

    @Value("${libreoffice.path}")
    private String libreOfficePath;

    public MultipartFile convertFileToPdf(MultipartFile file, FileType fileType) {
        String fileName = file.getOriginalFilename();
        if (fileName == null) {
            throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
        }

        // LibreOffice 설치 여부 확인
        if (!isLibreOfficeAvailable()) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }

        try (InputStream inputStream = file.getInputStream();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // OfficeManager를 매번 생성해서 사용하고 바로 종료
            OfficeManager officeManager = LocalOfficeManager.builder().build();

            try {
                officeManager.start();
                DocumentConverter converter = LocalConverter.make(officeManager);

                converter.convert(inputStream)
                        .to(outputStream)
                        .as(DefaultDocumentFormatRegistry.PDF)
                        .execute();

            } catch (OfficeException e) {
                throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
            } finally {
                try {
                    officeManager.stop();
                } catch (OfficeException e) {
                    log.warn("LibreOffice 종료 실패: {}", e.getMessage());
                }
            }

            return new CustomMultipartFile(outputStream.toByteArray(), fileName.split("\\.")[0] + ".pdf", "application/pdf");
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }

    private boolean isLibreOfficeAvailable() {
        File libreOffice = new File(libreOfficePath);
        return libreOffice.exists() && libreOffice.canExecute();
    }
}

