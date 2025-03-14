package com.partner.contract.agreement.common.utils;

import com.partner.contract.agreement.common.enums.AiStatus;
import com.partner.contract.agreement.common.enums.FileStatus;

public class DocumentStatusUtil {
    public static String determineStatus(FileStatus fileStatus, AiStatus aiStatus) {
        if (fileStatus == FileStatus.UPLOADING && aiStatus == null) {
            return "UPLOADING";
        } else if (fileStatus == FileStatus.FAILED && aiStatus == null) {
            return "FILE-FAILED";
        } else if (fileStatus == FileStatus.SUCCESS && aiStatus == AiStatus.ANALYZING) {
            return "ANALYZING";
        } else if (fileStatus == FileStatus.SUCCESS && aiStatus == AiStatus.FAILED) {
            return "AI-FAILED";
        } else if (fileStatus == FileStatus.SUCCESS && aiStatus == AiStatus.SUCCESS) {
            return "SUCCESS";
        }
        return null;
    }
}
