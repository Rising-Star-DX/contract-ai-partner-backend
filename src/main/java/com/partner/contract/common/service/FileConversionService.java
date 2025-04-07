package com.partner.contract.common.service;

import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.utils.CustomMultipartFile;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
            // 임시 디렉토리 경로 가져오기
            Path tempDir = Files.createTempDirectory("libreoffice-temp");

            // 원래 파일명을 유지한 파일 경로 만들기
            Path inputFilePath = tempDir.resolve(fileName);

            // 파일로 저장
            tempInputFile = inputFilePath.toFile();
            file.transferTo(tempInputFile); // MultipartFile -> File

            // 2. CLI 실행
            File outputDir = new File("/tmp");
            String[] command = {
                    libreOfficePath,
                    "--headless",
                    "--convert-to", "pdf",
                    tempInputFile.getAbsolutePath(),
                    "--outdir", outputDir.getAbsolutePath()
            };

//            log.info("실행 명령어: {}", String.join(" ", command));
            ProcessBuilder pb = new ProcessBuilder(command);
//            pb.environment().put("PATH", "/opt/libreoffice25.2/program:" + System.getenv("PATH"));
//            pb.environment().put("LD_LIBRARY_PATH", "/opt/libreoffice25.2/lib:" + System.getenv("LD_LIBRARY_PATH"));
            Process process = pb.start();

            if (process.waitFor() != 0) {
                throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
            }

            // 3. 변환된 PDF 파일 로딩
            String outputFileName =  fileName.split("\\.")[0] + ".pdf";
            outputPdfFile = new File(outputDir, outputFileName);

            if (!outputPdfFile.exists()) {
                throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
            }

            byte[] pdfBytes = Files.readAllBytes(outputPdfFile.toPath());

            // 4. 반환
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
}

