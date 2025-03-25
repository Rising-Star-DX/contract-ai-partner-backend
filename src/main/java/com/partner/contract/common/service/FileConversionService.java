package com.partner.contract.common.service;

import com.partner.contract.common.enums.FileType;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;

@Service
@RequiredArgsConstructor
public class FileConversionService {
    private final OfficeManager officeManager;

    public byte[] convertFileToPdf(MultipartFile file) {

        try(InputStream inputStream = file.getInputStream()) {
            System.out.println(file.getOriginalFilename());
            System.out.println("-----"  + file.getOriginalFilename());
            System.out.println("-----"  + FileType.fromContentType(file.getOriginalFilename()));
            /*File outputFile = new File(file.getOriginalFilename().replace(file.getContentType(), ".pdf"));
            officeManager.start();
            DocumentConverter converter = LocalConverter.make(officeManager);
            converter.convert(inputStream).to(outputFile).execute();
            officeManager.stop();

            return outputFile;*/
            // 임시 파일로 변환 (PDF로 저장)
            File tempPdfFile = File.createTempFile("converted_", ".pdf");

            officeManager.start();
            try {
                DocumentConverter converter = LocalConverter.make(officeManager);
                // 변환 실행
                converter.convert(inputStream).to(tempPdfFile).execute();
            } catch (OfficeException e) {
                // LibreOffice 관련 오류 처리
                System.out.println(e.getMessage());
                throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
            }
            officeManager.stop();

            // 변환된 파일을 byte[]로 읽어서 반환
            try (InputStream pdfInputStream = file.getInputStream()) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = pdfInputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                }

                // PDF 파일의 byte[] 반환
                return byteArrayOutputStream.toByteArray();
            }
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (OfficeException e) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }
    }
}
