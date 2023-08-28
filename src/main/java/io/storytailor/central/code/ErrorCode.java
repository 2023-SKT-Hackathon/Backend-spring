package io.storytailor.central.code;

public enum ErrorCode {
    UNKNOWN_ERROR("C001"),
    NOT_EXIST_ENTITY("C002"),
    SQL_ERROR("C003"),
    MEMORY_QUEUE_INPUT_ERROR("C004"),
    REQUEST_MESSAGE_PARSING_ERROR("C005"),
    RESPONSE_MESSAGE_PARSING_ERROR("C006"),
    INVALID_ENTITY_TYPE("C007"),
    INVALID_ACCEPT_TYPE("C008"),
    INVALID_PARAMETER("C009"),
    INVALID_AUTHORIZATION("C0010"),

    LENGTH_REQUIRED("C0011"),
    ALREADY_EXISTS("C0012"),
    NOT_EXIST_ID("C013"),
    NOT_EXIST_ENTITY_ATTR("C014"),
    LOAD_ENTITY_SCHEMA_ERROR("C015"),
    CREATE_ENTITY_TABLE_ERROR("C016"),
    OPERATION_NOT_SUPPORTED("C017"),

    BAD_REQUEST("C099"),
    NOT_SUPPORTED_METHOD("C100");

    private String code;

    private ErrorCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ErrorCode parseCode(String code) {
        for (ErrorCode errorCode : values()) {
            if (errorCode.getCode().equals(code)) {
                return errorCode;
            }
        }
        return null;
    }
}
