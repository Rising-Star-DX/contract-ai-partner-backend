package com.partner.contract.common.service;

import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.utils.CustomMultipartFile;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

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

        if (!isLibreOfficeAvailable()) {
            throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
        }

        File tempInputFile = null;
        File outputPdfFile = null;

        try {
            Path tempDir = Files.createTempDirectory("libreoffice-tmp");

            Path inputFilePath = tempDir.resolve(fileName);

            tempInputFile = inputFilePath.toFile();
            file.transferTo(tempInputFile);

            File outputDir = new File("/tmp");
            String[] command = {
                    libreOfficePath,
                    "--headless",
                    "--convert-to", "pdf",
                    tempInputFile.getAbsolutePath(),
                    "--outdir", outputDir.getAbsolutePath()
            };

            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            if (process.waitFor() != 0) {
                throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
            }

            String outputFileName =  fileName.split("\\.")[0] + ".pdf";
            outputPdfFile = new File(outputDir, outputFileName);

            if (!outputPdfFile.exists()) {
                throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
            }

            byte[] pdfBytes = Files.readAllBytes(outputPdfFile.toPath());

            return new CustomMultipartFile(pdfBytes, outputFileName, "application/pdf");

        } catch (IOException | InterruptedException e) {
            throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
        } finally {
            if (tempInputFile != null && tempInputFile.exists()) tempInputFile.delete();
            if (outputPdfFile != null && outputPdfFile.exists()) outputPdfFile.delete();
        }
    }

    private boolean isLibreOfficeAvailable() {
        File libreOffice = new File(libreOfficePath);
        return libreOffice.exists() && libreOffice.canExecute();
    }

    public MultipartFile convertStringToTxt(String name, List<String> contents) {
        try {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < contents.size(); i++) {
                sb.append(contents.get(i));
                if (i < contents.size() - 1) {
                    sb.append("\f");
                }
            }

            byte[] contentBytes = sb.toString().getBytes("UTF-8");

            return new MockMultipartFile(
                    "file",
                    name + ".txt",
                    "text/plain",
                    new ByteArrayInputStream(contentBytes)
            );

        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
        }
    }
}

