package com.social.app.exception;


import com.social.app.general.enums.ResponseCodeAndMessage;

public class GeneralException extends RuntimeException {

    public GeneralException(String responseCode, String responseMessage) {
        super(responseCode, new Throwable(responseMessage));
    }

    public GeneralException(ResponseCodeAndMessage codeAndMessage) {
        super(codeAndMessage.responseCode, new Throwable(codeAndMessage.responseMessage));
    }
}
