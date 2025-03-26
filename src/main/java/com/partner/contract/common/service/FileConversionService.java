package com.partner.contract.common.service;

import com.partner.contract.common.domain.CustomMultipartFile;
import com.partner.contract.common.enums.FileType;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.apache.tomcat.util.http.fileupload.FileItem;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.tomcat.util.http.fileupload.disk.DiskFileItem;
import org.jodconverter.core.DocumentConverter;
import org.jodconverter.core.office.OfficeException;
import org.jodconverter.core.office.OfficeManager;
import org.jodconverter.local.LocalConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.StandardMultipartHttpServletRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
@RequiredArgsConstructor
public class FileConversionService {
    private final OfficeManager officeManager;

    public MultipartFile convertFileToPdf(MultipartFile file) {

        try(InputStream inputStream = file.getInputStream()) {
            System.out.println(file.getOriginalFilename());
            System.out.println("-----"  + file.getOriginalFilename().split("\\.")[0]);
            System.out.println("-----"  + FileType.fromContentType(file.getOriginalFilename()));

            // 임시 파일로 변환 (PDF로 저장)
            File outputFile = File.createTempFile(file.getOriginalFilename().split("\\.")[0], ".pdf");
            System.out.println(outputFile.getName());
            if(!officeManager.isRunning()) {
                officeManager.start();
            }
            DocumentConverter converter = LocalConverter.make(officeManager);
            converter.convert(inputStream).to(outputFile).execute();

//            // 변환된 파일을 byte[]로 읽어서 반환
//            try (InputStream pdfInputStream = file.getInputStream()) {
//                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                byte[] buffer = new byte[1024];
//                int bytesRead;
//                while ((bytesRead = pdfInputStream.read(buffer)) != -1) {
//                    byteArrayOutputStream.write(buffer, 0, bytesRead);
//                }
//
//                // PDF 파일의 byte[] 반환
//                return byteArrayOutputStream.toByteArray();
            return new CustomMultipartFile(outputFile);
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (OfficeException e) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }
    }
}
