package com.partner.contract.common.service;

import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.utils.CustomMultipartFile;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.document.DefaultDocumentFormatRegistry;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
public class FileConversionService {
    private final OfficeManager officeManager;

    public MultipartFile convertFileToPdf(MultipartFile file, FileType fileType) {

        try(InputStream inputStream = file.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            String fileName = file.getOriginalFilename();
            if(fileName == null) {
                throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
            }

            if(!officeManager.isRunning()) {
                officeManager.start();
            }

            DocumentConverter converter = LocalConverter.make(officeManager);
            converter.convert(inputStream)
                    .to(outputStream)
                    .as(DefaultDocumentFormatRegistry.PDF) // 출력형식 PDF로 지정
                    .execute();

            return new CustomMultipartFile(outputStream.toByteArray(), fileName.split("\\.")[0] + ".pdf", "application/pdf");
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (OfficeException e) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }
    }
}
