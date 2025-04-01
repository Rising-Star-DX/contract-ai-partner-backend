package com.partner.contract.common.service;

import com.partner.contract.common.enums.FileType;
import com.partner.contract.common.utils.CustomMultipartFile;
import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xlsx4j.sml.Workbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileConversionService {
//    @Nullable
//    private final OfficeManager officeManager;

    public MultipartFile convertFileToPdf(MultipartFile file, FileType fileType) {
        //log.info("convertFileToPdf 실행 - officeManager 존재 여부: {}", officeManager != null);
        try(InputStream inputStream = file.getInputStream();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                String fileName = file.getOriginalFilename();
                if(fileName == null) {
                    throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
                }

//                if(officeManager != null) {
//                    try {
//                        log.info("officeManager 시작");
//                        if (!officeManager.isRunning()) {
//                            officeManager.start();
//                        }
//                        DocumentConverter converter = LocalConverter.make(officeManager);
//                        converter.convert(inputStream)
//                                .to(outputStream)
//                                .as(DefaultDocumentFormatRegistry.PDF) // 출력형식 PDF로 지정
//                                .execute();
//                    } catch (OfficeException e) {
//                        throw new ApplicationException(ErrorCode.OFFICE_CONNECTION_ERROR);
//                    }
//                }
            if (fileType == FileType.DOC || fileType == FileType.DOCX) {
                convertWordToPdf(inputStream, outputStream);
            } else if (fileType == FileType.XLS || fileType == FileType.XLSX) {
                convertExcelToPdf(inputStream, outputStream);
            } else {
                throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
            }
                return new CustomMultipartFile(outputStream.toByteArray(), fileName.split("\\.")[0] + ".pdf", "application/pdf");
            } catch (IOException e) {
                throw new ApplicationException(ErrorCode.FILE_PROCESSING_ERROR);
            }
    }

    private void convertWordToPdf(InputStream inputStream, ByteArrayOutputStream outputStream) {
        try {
           // WordprocessingMLPackage wordMLPackage= WordprocessingMLPackage.load(inputStream);
            //PdfConverter.getInstance().convert(wordMLPackage, outputStream);
          //  Docx4J.toPDF(wordMLPackage, outputStream);
            XWPFDocument xwpfDocument = new XWPFDocument(inputStream);
            PdfOptions options = PdfOptions.create();
            PdfConverter.getInstance().convert(xwpfDocument, outputStream, options);
        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.FILE_CONVERSION_ERROR);
        }
    }

    private void convertExcelToPdf(InputStream inputStream, ByteArrayOutputStream outputStream) {
        try {
            Workbook workbook = (Workbook) WorkbookFactory.create(inputStream);
            ByteArrayOutputStream htmlOutputStream = new ByteArrayOutputStream();

        } catch (IOException e) {
            throw new ApplicationException(ErrorCode.FILE_CONVERSION_ERROR);
        }

    }
}
