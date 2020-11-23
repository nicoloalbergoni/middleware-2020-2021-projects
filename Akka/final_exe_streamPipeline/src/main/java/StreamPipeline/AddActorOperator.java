package StreamPipeline;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.ArrayList;

public class AddActorOperator extends AbstractActor {

    private final int maxWindowSize = 10;
    private ArrayList<DataMessage> buffer = new ArrayList<>();


    @Override
    public Receive createReceive() {
        return receiveBuilder().match(DataMessage.class, this::onMessage).build();
    }

    void onMessage(DataMessage msg) {
        if (buffer.size() == maxWindowSize) {

        }
    }

    void Aggregate(ArrayList buffer) {
        
    }

    static Props props() {
        return Props.create(AddActorOperator.class);
    }
}
