package com.faultToleranceCounter;

import akka.actor.AbstractActor;
import akka.actor.OneForOneStrategy;
import akka.actor.Props;
import akka.actor.SupervisorStrategy;
import akka.japi.pf.DeciderBuilder;
import java.time.Duration;

public class CounterSupervisorActor extends AbstractActor {

	 // #strategy
    private static SupervisorStrategy strategy =
        new OneForOneStrategy(
            10,
            Duration.ofMinutes(1),
            DeciderBuilder.match(ResumeException.class, e -> SupervisorStrategy.restart())
					.match(StopException.class, e -> SupervisorStrategy.stop())
					.match(ResumeException.class, e -> SupervisorStrategy.resume())
                .build());

    @Override
    public SupervisorStrategy supervisorStrategy() {
      return strategy;
    }

	public CounterSupervisorActor() {
	}

	@Override
	public Receive createReceive() {
		return receiveBuilder()
		          .match(
		              Props.class,
		              props -> {
		                getSender().tell(getContext().actorOf(props), getSelf());
		              })
		          .build();
	}

	static Props props() {
		return Props.create(CounterSupervisorActor.class);
	}

}
