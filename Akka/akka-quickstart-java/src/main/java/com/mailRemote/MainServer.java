package com.mailRemote;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainServer {


    public static void main(String[] args)  {
        Config conf = ConfigFactory.parseFile(new File("serverConfig.conf"));
        final ActorSystem sys = ActorSystem.create("Server", conf);
        final ActorRef server = sys.actorOf(ServerActor.props(), "serverActor");
        System.out.println(server.path().address().host());
        System.out.println("created server actor: " + server.toString());
        // Wait for all messages to be sent and received
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            sys.terminate();
        }
    }
}
