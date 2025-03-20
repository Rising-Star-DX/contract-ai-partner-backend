package com.partner.contract.common.enums;

import com.partner.contract.global.exception.error.ApplicationException;
import com.partner.contract.global.exception.error.ErrorCode;

import java.util.Arrays;

public enum FileType {
    PDF,
    DOCX,
    DOC,
    JPEG,
    JPG,
    PNG,
    TXT
    ;

    public static FileType fromContentType(String contentType) {
        if (contentType == null || !contentType.contains("/")) {
            throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
        }

        String extension = contentType.split("/")[1].toUpperCase();

        try {
            return FileType.valueOf(extension);
        } catch (IllegalArgumentException e) {
            throw new ApplicationException(ErrorCode.FILE_TYPE_ERROR);
        }
    }
}
