package StreamPipeline;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {

    final static int numberOfInstances = 4;
    final static int numberOfMessages = 10000;
    final static int numberOfKeys1 = 200;
    public final static ExecutorService exec = Executors.newFixedThreadPool(8);

    public static void main(String[] args) throws InterruptedException {

        final ActorSystem sys = ActorSystem.create("System");
        Random r = new Random();

        ArrayList<ActorRef> AddOperatorList = new ArrayList<>();
        ArrayList<ActorRef> MultiplyOperatorList = new ArrayList<>();
        ArrayList<ActorRef> AverageOperatorList = new ArrayList<>();

        for (int i = 0; i < numberOfInstances; i++) {
            AddOperatorList.add(sys.actorOf(AddActorOperator.props(), "AddOperator" + i));
            MultiplyOperatorList.add(sys.actorOf(MultiplyActorOperator.props(), "MultiplyOperator" + i));
            AverageOperatorList.add(sys.actorOf(AverageActorOperator.props(), "AverageOperator" + i));
        }

        AddActorOperator.nextStep = MultiplyOperatorList;
        MultiplyActorOperator.nextStep = AverageOperatorList;

        for (int i = 0; i < numberOfMessages; i++) {
            DataMessage msg = new DataMessage(r.nextInt(numberOfKeys1), r.nextInt(500));
            exec.submit(() -> AddOperatorList.get(msg.getKey() % numberOfInstances).tell(msg, ActorRef.noSender()));
            //Thread.sleep(1000);
        }

    }
}
