package com.mail;

import akka.actor.AbstractActor;
import akka.actor.Props;
import com.counter.CounterActor;

public class ClientActor extends AbstractActor {

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(ReplyMessage.class, this::onReplyMessage)
                .build();
    }
    /*
    PutMessage sendPutMessage(String email, String name) {
        System.out.println("Sending a put message for mail: " + email + " name: " + name);
        return new PutMessage(email, name);
    }

    GetMessage sendGetMessage(String name) {
        System.out.println("Sending a get message for name: " + name);
        return new GetMessage(name);
    }

     */

    void onReplyMessage (ReplyMessage msg) {
        System.out.println("Received message reply, response: " + msg.getResponse());
    }

    static Props props() {
        return Props.create(ClientActor.class);
    }
}
