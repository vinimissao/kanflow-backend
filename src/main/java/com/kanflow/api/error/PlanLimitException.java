package com.kanflow.api.error;

public class PlanLimitException extends RuntimeException {

    public PlanLimitException(String message) {
        super(message);
    }
}
