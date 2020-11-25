package StreamPipeline;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.ArrayList;

public class AverageActorOperator extends AbstractActor {

    private final int maxWindowSize = 10;
    private ArrayList<Integer> buffer;

    public AverageActorOperator() {
        this.buffer = new ArrayList<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DataMessage.class, this::onMessage).build();
    }

    void onMessage(DataMessage msg) {
        this.buffer.add(msg.getValue());
        if(this.buffer.size() == maxWindowSize) {
            Double result = this.buffer.stream().mapToDouble(i -> i).average().orElse(0.0);
            System.out.println("Average actor " + this.getSelf() + " result is: " + result + " for key: " + msg.getKey());
            this.buffer.clear();
            System.out.println("Clearing " + this.getSelf() + " Buffer");
        }
    }

    static Props props() {
        return Props.create(AverageActorOperator.class);
    }
}
