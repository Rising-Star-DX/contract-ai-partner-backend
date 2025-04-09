package com.partner.contract.common.enums;

import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;

public enum FileType {
    PDF(false),
    DOCX(true),
    DOC(true),
    XLSX(true),
    XLS(true),
    JPEG(false),
    JPG(false),
    PNG(false),
    TXT(false)
    ;

    private final boolean convertiblePdf;

    FileType(boolean convertiblePdf) {
        this.convertiblePdf = convertiblePdf;
    }

    public boolean isConvertiblePdf() {
        return convertiblePdf;
    }

    public static FileType fromContentType(String contentType) {
        if (contentType == null || !contentType.contains("/")) {
            throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
        }

        String extension = getExtension(contentType.split("/")).toUpperCase();
        try {
            return FileType.valueOf(extension);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
        }
    }

    public static String getExtension(String[] contentType) {
        switch (contentType[0]) {
            case "image":
                return contentType[1].toLowerCase();
            case "text":
                return "txt";
            case "application":
                switch (contentType[1]) {
                    case "pdf":
                        return "pdf";
                    case "msword":
                        return "doc";  // application/msword -> .doc
                    case "vnd.openxmlformats-officedocument.wordprocessingml.document":
                        return "docx";  // Word 파일 (docx 확장자)
                    case "vnd.ms-excel":
                        return "xls";  // .xls (Excel 97-2003)
                    case "vnd.openxmlformats-officedocument.spreadsheetml.sheet":
                        return "xlsx";  // .xlsx (Excel 2007 이상)
                    default:
                        throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
                }
            default:
                throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
        }
    }
}
