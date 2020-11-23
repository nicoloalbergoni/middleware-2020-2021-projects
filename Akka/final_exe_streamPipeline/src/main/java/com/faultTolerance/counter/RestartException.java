package com.faultTolerance.counter;

public class RestartException extends Exception{
    public RestartException(String message) {
        super(message);
    }
}
