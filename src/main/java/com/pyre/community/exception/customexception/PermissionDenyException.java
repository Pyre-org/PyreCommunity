package com.pyre.community.exception.customexception;

public class PermissionDenyException extends RuntimeException{
    public PermissionDenyException(String message) {
        super(message);
    }
}
