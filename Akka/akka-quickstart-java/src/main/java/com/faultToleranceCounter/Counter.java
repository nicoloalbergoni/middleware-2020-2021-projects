package com.faultToleranceCounter;

import static akka.pattern.Patterns.ask;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.concurrent.TimeoutException;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

public class Counter {

	public enum Operation {
		INCREMENT_OP,
		DECREMENT_OP,
		FAULT_OP,
		STOP_OP,
		RESUME_OP
	}
	public static final int FAULTS = 1;

	public static void main(String[] args) {
		scala.concurrent.duration.Duration timeout = scala.concurrent.duration.Duration.create(5, SECONDS);

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef supervisor = sys.actorOf(CounterSupervisorActor.props(), "supervisor");

		ActorRef counter;
		try {
			scala.concurrent.Future<Object> waitingForCounter = ask(supervisor, Props.create(CounterActor.class), 5000);
			counter = (ActorRef) waitingForCounter.result(timeout, null);

			counter.tell(new DataMessage(Operation.INCREMENT_OP), ActorRef.noSender());

			for (int i = 0; i < FAULTS; i++)
				counter.tell(new DataMessage(Operation.FAULT_OP), ActorRef.noSender());

			counter.tell(new DataMessage(Operation.DECREMENT_OP), ActorRef.noSender());

			counter.tell(new DataMessage(Operation.STOP_OP), ActorRef.noSender());
			counter.tell(new DataMessage(Operation.RESUME_OP), ActorRef.noSender());

			sys.terminate();

		} catch (TimeoutException | InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

}
