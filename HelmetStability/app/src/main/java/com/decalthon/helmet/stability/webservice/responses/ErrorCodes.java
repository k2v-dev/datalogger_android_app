package com.decalthon.helmet.stability.webservice.responses;

public enum ErrorCodes {

    MISSING_REQUIRED_FIELD(1001),
    RECORD_ALREADY_EXISTS(1002),
    INTERNAL_SERVER_ERROR(1003),
    NO_RECORD_FOUND(1004),
    NO_USER_FOUND(1005),
    AUTHENTICATION_FAILED(1006),
    COULD_NOT_WRITE_FILE(1007),
    COULD_NOT_WRITE_DIR(1008),
    OTP_NOT_MATCHING(1009),
    COULD_NOT_UPDATE_RECORD(1010),
    COULD_NOT_DELETE_RECORD(1011),
    EMAIL_ADDRESS_NOT_VERIFIED(1012);

    private int code;

    ErrorCodes(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
