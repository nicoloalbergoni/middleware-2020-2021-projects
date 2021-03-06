package StreamPipeline;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;

import java.util.ArrayList;
import java.util.Random;

public class AddActorOperator extends AbstractActor {

    private final int maxWindowSize = 2;
    final static int numberOfKeys2 = 100;
    private ArrayList<Integer> buffer;
    public static ArrayList<ActorRef> nextStep;

    public AddActorOperator() {
        this.buffer = new ArrayList<Integer>();
    }


    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DataMessage.class, this::onMessage).build();
    }

    void onMessage(DataMessage msg) {
        this.buffer.add(msg.getValue());
        if (buffer.size() == maxWindowSize) {
            Integer result = this.buffer.stream().reduce(0, Integer::sum);
            DataMessage newMsg = new DataMessage(new Random().nextInt(numberOfKeys2), result);
            nextStep.get(msg.getKey() % Main.numberOfInstances).tell(newMsg, this.getSelf());
            System.out.println("Sending result: " + newMsg.getValue() + " to Next MultiplyActor with key: " + newMsg.getKey());
            this.buffer.clear();
            System.out.println("Clearing " + this.getSelf() + " Buffer");
        }
    }

    static Props props() {
        return Props.create(AddActorOperator.class);
    }
}
