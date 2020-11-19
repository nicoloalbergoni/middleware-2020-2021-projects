package com.mailRemote;

import akka.actor.AbstractActor;
import akka.actor.ActorSelection;
import akka.actor.Props;

public class ClientActor extends AbstractActor {


    ActorSelection server;

    @Override
    public void preStart() throws Exception {
        super.preStart();
        String serverAddr = "akka.tcp://Server@127.0.0.1:2552/user/serverActor";
        //String serverAddr = "akka://System/user/serverActor";
        server = context().actorSelection(serverAddr);
        System.out.println("created client actor: " + server.toString());
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PutMessage.class, this::forwardPutMessage)
                .match(ReplyMessage.class, this::onReplyMessage)
                .build();
    }

    void forwardPutMessage(PutMessage msg) {
        server.tell(msg, getSelf());
    }

    void onReplyMessage (ReplyMessage msg) {
        System.out.println("Received message reply, response: " + msg.getResponse());
    }

    static Props props() {
        return Props.create(ClientActor.class);
    }
}
