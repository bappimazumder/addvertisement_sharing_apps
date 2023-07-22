package com.advertise.config;

public class AdvartiseException extends Exception {

    public AdvartiseException(String message) {
        super(message);
    }

    public AdvartiseException(Throwable throwable) {
        super(throwable);
    }

    public AdvartiseException(String message, Throwable cause) {
        super(message, cause);
    }
}
