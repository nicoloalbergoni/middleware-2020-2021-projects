package com.faultToleranceCounter;

public class DataMessage {

	private Counter.Operation operation;
	
	public Counter.Operation getCode() {
		return operation;
	}

	public DataMessage(Counter.Operation operation) {
		this.operation = operation;
	}
}
