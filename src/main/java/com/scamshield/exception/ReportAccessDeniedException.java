package com.scamshield.exception;

public class ReportAccessDeniedException extends RuntimeException {

    public ReportAccessDeniedException(String message) {
        super(message);
    }
}