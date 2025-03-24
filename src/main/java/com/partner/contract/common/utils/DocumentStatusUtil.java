package com.partner.contract.common.utils;

import com.partner.contract.common.enums.AiStatus;
import com.partner.contract.common.enums.FileStatus;

public class DocumentStatusUtil {
    public static String determineStatus(FileStatus fileStatus, AiStatus aiStatus) {
        if (fileStatus != FileStatus.FAILED && aiStatus == null) {
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
