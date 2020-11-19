package com.counter;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;

public class Counter {

	private static final int numThreads = 16;
	private static final int numMessages = 1000;

	public static void main(String[] args) {

		final ActorSystem sys = ActorSystem.create("System");
		final ActorRef counter = sys.actorOf(CounterActor.props(), "counter");

		// Send messages from multiple threads in parallel
		final ExecutorService exec = Executors.newFixedThreadPool(numThreads);

		Random r = new Random();
		for (int i = 0; i < numMessages; i++) {
			if (r.nextBoolean()) {
				exec.submit(() -> counter.tell(new IncrementMessage(), ActorRef.noSender()));
			} else {
				exec.submit(() -> counter.tell(new DecrementMessage(), ActorRef.noSender()));
			}
		}
		
		// Wait for all messages to be sent and received
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		exec.shutdown();
		sys.terminate();

	}

}
