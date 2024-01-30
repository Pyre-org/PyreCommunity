package com.pyre.community.exception.customexception;

public class VerifyEmailFailException extends RuntimeException {
    public VerifyEmailFailException(String message) {
        super(message);
    }
}
