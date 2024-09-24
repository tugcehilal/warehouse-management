package com.fulfilment.application.monolith.exceptions;

import lombok.Getter;

@Getter
public class WarehouseException extends RuntimeException {
    private final ErrorRule errorRule;

    public WarehouseException(ErrorRule errorRule) {
        super(errorRule.getDescription());
        this.errorRule = errorRule;
    }

    public WarehouseException(ErrorRule errorRule, String message) {
        super(message);
        this.errorRule = errorRule;
    }
}