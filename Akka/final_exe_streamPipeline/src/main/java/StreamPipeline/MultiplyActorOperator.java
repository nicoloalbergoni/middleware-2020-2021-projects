package StreamPipeline;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import java.util.ArrayList;
import java.util.Random;

public class MultiplyActorOperator extends AbstractActor {

    private final int maxWindowSize = 10;
    final static int numberOfKeys3 = 50;
    private ArrayList<Integer> buffer;
    public static ArrayList<ActorRef> nextStep;

    public MultiplyActorOperator() {
        this.buffer = new ArrayList<>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DataMessage.class, this::onMessage).build();
    }

    void onMessage(DataMessage msg) {
        this.buffer.add(msg.getValue());
        if (this.buffer.size() == maxWindowSize) {
            Integer result = this.buffer.stream().reduce(1, (subtotal, next) -> subtotal * next);
            DataMessage newMsg = new DataMessage(new Random().nextInt(numberOfKeys3), result);
            nextStep.get(msg.getKey() % Main.numberOfInstances).tell(newMsg, this.getSelf());
            System.out.println("Sending result: " + newMsg.getValue() + " to Next AverageActor with key: " + newMsg.getKey());
            this.buffer.clear();
            System.out.println("Clearing " + this.getSelf() + " Buffer");
        }
    }

    static Props props() {
        return Props.create(MultiplyActorOperator.class);
    }
}
