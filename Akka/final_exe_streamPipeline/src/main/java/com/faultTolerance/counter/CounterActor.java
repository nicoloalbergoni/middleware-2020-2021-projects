package com.faultTolerance.counter;

import java.util.Optional;

import akka.actor.AbstractActor;
import akka.actor.Props;

public class CounterActor extends AbstractActor {

	private int counter;

	public CounterActor() {
		this.counter = 0;
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder().match(DataMessage.class, this::onMessage).build();
	}

	void onMessage(DataMessage msg) throws Exception {
		if (msg.getCode() == Counter.Operation.INCREMENT_OP) {
			++counter;
			System.out.println("I am executing a NORMAL operation...counter is now incremented " + counter);
		} else if (msg.getCode() == Counter.Operation.DECREMENT_OP) {
			--counter;
			System.out.println("I am executing a NORMAL operation...counter is now decremented " + counter);
		}
		else if (msg.getCode() == Counter.Operation.FAULT_OP) {
			System.out.println("I am emulating a FAULT!");		
			throw new RestartException("Actor fault!");
		} else if (msg.getCode() == Counter.Operation.STOP_OP) {
			System.out.println("I am emulating a STOP!");
			throw new StopException("Stop exception sent");
		} else if (msg.getCode() == Counter.Operation.RESUME_OP) {
			System.out.println("I am emulating a RESUME!");
			throw new ResumeException("Resume exception sent");
		}
	}

	@Override
	public void preRestart(Throwable reason, Optional<Object> message) {
		System.out.print("Preparing to restart...");		
	}
	
	@Override
	public void postRestart(Throwable reason) {
		System.out.println("...now restarted!");	
	}

	@Override
	public void postStop() throws Exception {
		System.out.println("...now stopped");
	}


	static Props props() {
		return Props.create(CounterActor.class);
	}

}
