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
    CATEGORY_ALREADY_EXISTS_ERROR(HttpStatus.BAD_REQUEST, "C002", "이미 존재하는 카테고리 이름입니다."),
    CATEGORY_DOCUMENT_ALREADY_EXISTS_ERROR(HttpStatus.CONFLICT, "C003", "해당 카테고리의 문서가 1개 이상 존재하여 삭제할 수 없습니다."),

    // STANDARD
    STANDARD_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "ST001", "해당 ID에 대응되는 기준문서가 없습니다."),

    // AGREEMENT
    AGREEMENT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "A001", "해당 ID에 대응되는 계약서가 없습니다."),

    // File
    S3_FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FI001", "S3 파일 업로드 중 에러가 발생했습니다."),
    FILE_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FI002", "파일 처리 중 에러가 발생했습니다."),
    FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FI003", "S3 업로드 중이거나 AI 분석이 진행 중인 파일은 삭제할 수 없습니다."),
    FILE_TYPE_ERROR(HttpStatus.BAD_REQUEST, "FI004", "지원하지 않는 파일 확장자이거나 파일 확장자를 가져올 수 없습니다."),
    S3_FILE_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FI005", "S3 파일 삭제 중 에러가 발생했습니다."),
    S3_FILE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "FI006", "S3에 현재 URL에 대응되는 파일이 존재하지 않습니다."),
    S3_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "S3C001", "S3 통신 중 문제가 발생했습니다."),

    // Analysis
    MISSING_FILE_FOR_ANALYSIS(HttpStatus.BAD_REQUEST, "AN001", "AI 분석을 수행하려면 파일이 필요합니다."),
    AI_ANALYSIS_NOT_STARTED(HttpStatus.BAD_REQUEST, "AN002", "AI 분석이 아직 시작되지 않았습니다."),
    AI_ANALYSIS_ALREADY_COMPLETED(HttpStatus.BAD_REQUEST, "AN003", "이미 AI 분석이 완료된 문서입니다."),
    AI_ANALYSIS_ALREADY_STARTED(HttpStatus.BAD_REQUEST, "AN004", "이미 AI 분석이 진행 중인 문서입니다."),
    AI_ANALYSIS_POSITION_EMPTY_ERROR(HttpStatus.BAD_REQUEST, "AN005", "위배 문구의 위치 정보가 비어있습니다."),
    NO_ANALYSIS_STANDARD_DOCUMENT(HttpStatus.BAD_REQUEST, "AN006", "동일 카테고리 내에 학습된 기준 문서가 없습니다."),

    // Flask
    FLASK_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FL001", "Flask에서 반환된 데이터 형식이 올바르지 않습니다."),
    FLASK_SERVER_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FL002", "Flask API 요청 중 문제가 발생했습니다."),
    FLASK_RESPONSE_NULL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FL003", "Flask에서 응답한 data가 null입니다."),
    FLASK_ANALYSIS_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FL004", "Flask에서 AI 분석에 실패했습니다."),
    FLASK_DELETE_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "FL005", "Flask에서 데이터 삭제에 실패했습니다."),

    // LibreOffice
    OFFICE_CONNECTION_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "OF001", "LibreOffice 통신 중 문제가 발생했습니다.")
    ;

    private final HttpStatus status;
    private final String code;
    private final String message;
}
