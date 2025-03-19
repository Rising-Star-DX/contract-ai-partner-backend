package com.partner.contract.global.exception.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Global Issue
    BAD_REQUEST_ERROR(HttpStatus.BAD_REQUEST, "G001", "BAD REQUEST"),
    REQUEST_BODY_MISSING_ERROR(HttpStatus.BAD_REQUEST, "G002", "Request Body가 존재하지 않습니다."),
    REQUEST_PARAMETER_MISSING_ERROR(HttpStatus.BAD_REQUEST, "G003", "필수 요청 파라미터가 존재하지 않습니다."),
    INVALID_TYPE_VALUE_ERROR(HttpStatus.BAD_REQUEST, "G004", "요청한 값의 타입이 올바르지 않습니다."),
    JSON_PARSE_ERROR(HttpStatus.BAD_REQUEST, "G005", "JSON 파싱 에러가 발생했습니다."),
    FORBIDDEN_ERROR(HttpStatus.FORBIDDEN, "G006", "권한이 없습니다."),
    UNAUTHORIZED_ERROR(HttpStatus.UNAUTHORIZED, "G007", "인증되지 않은 사용자입니다."),
    NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "G008", "해당 리소스가 존재하지 않습니다."),
    NULL_POINTER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G009", "NullPointException이 발생했습니다."),
    METHOD_NOT_ALLOWED_ERROR(HttpStatus.METHOD_NOT_ALLOWED, "G010", "허용되지 않은 메소드입니다."),
    DATABASE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G998", "데이터베이스 연결에 실패했습니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "G999", "서버 내부 에러가 발생했습니다."),
    // Category
    CATEGORY_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "C001", "해당 ID에 대응되는 카테고리가 없습니다."),

    // STANDARD
    STANDARD_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "ST001", "해당 ID에 대응되는 문서가 없습니다."),

    // File
    S3_FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FI001", "S3 파일 업로드 중 에러가 발생했습니다."),
    FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FI002", "파일 처리 중 에러가 발생했습니다."),

    // Analysis
    MISSING_FILE_FOR_ANALYSIS(HttpStatus.BAD_REQUEST, "AN001", "AI 분석을 수행하려면 파일이 필요합니다."),

    // Flask
    FLASK_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FL001", "Flask에서 반환된 데이터 형식이 올바르지 않습니다."),
    FLASK_SERVER_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FL002", "Flask API 요청 중 문제가 발생했습니다."),
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
