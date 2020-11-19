package com.mail;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;


import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainApp {
    private static final int numThreads = 16;
    private static final int numMessages = 100;

    public static void main(String[] args)  {

        final ActorSystem sys = ActorSystem.create("System");
        final ActorRef serverActor = sys.actorOf(ServerActor.props(), "server");
        final ActorRef clientActor1 = sys.actorOf(ClientActor.props(), "client1");
        final ActorRef clientActor2 = sys.actorOf(ClientActor.props(), "client2");

        // Send messages from multiple threads in parallel
        final ExecutorService exec = Executors.newFixedThreadPool(numThreads);
        Random r = new Random();


        for (int i = 0; i < numMessages; i++) {
            String rnd = Integer.toString(r.nextInt(110));
            exec.submit(() -> serverActor.tell(new PutMessage(rnd + "@email.it", rnd), r.nextBoolean() ? clientActor1 : clientActor2));
            exec.submit(() -> serverActor.tell(new GetMessage(rnd), r.nextBoolean() ? clientActor1 : clientActor2));
        }


        // Wait for all messages to be sent and received
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            exec.shutdown();
            sys.terminate();
        }
    }
}
