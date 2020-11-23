package StreamPipeline;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.util.ArrayList;
import java.util.Random;

public class Main {

    final static int numberOfInstances = 4;
    final static int numberOfMessages = 10000;
    final static int numberOfKeys1 = 200;

    public static void main(String[] args) {

        final ActorSystem sys = ActorSystem.create("System");
        Random r = new Random();

        ArrayList<ActorRef> firstOperatorList = new ArrayList<>();
        for (int i = 0; i < numberOfInstances; i++) {
            firstOperatorList.add(sys.actorOf(AddActorOperator.props(), "AddOperator" + i));
        }

        for (int i = 0; i < numberOfMessages; i++) {
            DataMessage msg = new DataMessage(r.nextInt(numberOfKeys1), r.nextInt());
            firstOperatorList.get(msg.getKey() % numberOfInstances).tell(msg, ActorRef.noSender());
        }

    }
}
