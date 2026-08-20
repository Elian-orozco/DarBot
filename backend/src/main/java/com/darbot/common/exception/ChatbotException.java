package com.darbot.common.exception;

import org.springframework.http.HttpStatus;

public class ChatbotException extends ApiException {

    public ChatbotException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public ChatbotException(String message, Throwable cause) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
        initCause(cause);
    }
}
