package com.mailRemote;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainClient {
    private static final int numThreads = 16;
    private static final int numMessages = 100;

    public static void main(String[] args) {
        Config conf = ConfigFactory.parseFile(new File("clientConfig.conf"));
        final ActorSystem sys = ActorSystem.create("ClientSystem", conf);
        final ActorRef clientActor1 = sys.actorOf(ClientActor.props(), "clientActor1");

        System.out.println("created client actor: " + clientActor1.toString());

        String serverAddr = "akka.tcp://Server@10.0.0.1:2552/user/serverActor";
        //String serverAddr = "akka://Server/user/serverActor";
        ActorSelection server = sys.actorSelection(serverAddr);
        System.out.println("created client actor: " + server.toString());

        // Send messages from multiple threads in parallel
        final ExecutorService exec = Executors.newFixedThreadPool(numThreads);
        Random r = new Random();
        for (int i = 0; i < numMessages; i++) {
            String rnd = Integer.toString(r.nextInt(110));
            exec.submit(() -> clientActor1.tell(new PutMessage(rnd + "@email.it", rnd), ActorRef.noSender()));
            //exec.submit(() -> server.tell(new PutMessage(rnd + "@email.it", rnd), clientActor1));
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
