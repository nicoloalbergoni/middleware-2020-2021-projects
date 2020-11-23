package com.faultTolerance.counter;

public class StopException extends Exception{

    public StopException(String message) {
        super(message);
    }
}
