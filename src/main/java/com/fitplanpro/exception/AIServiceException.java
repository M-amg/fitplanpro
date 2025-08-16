package com.fitplanpro.exception;

public class AIServiceException extends RuntimeException {
    public AIServiceException(String s) {
    }
    public AIServiceException(String s, Exception fallbackEx) {
    }
}
