package com.mailRemote;

import akka.actor.AbstractActor;
import akka.actor.Props;

import java.util.HashMap;

public class ServerActor extends AbstractActor {

    private HashMap<String, String> list;

    ServerActor() {
        this.list = new HashMap<String, String>();
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(PutMessage.class, this::onPutMessage)
                .match(GetMessage.class, this::onGetMessage)
                .build();
    }

    void onPutMessage(PutMessage msg) {
        list.put(msg.getName(), msg.getEmail());
        System.out.println("Inserted mail: " + msg.getEmail() + " Name: " + msg.getName());
    }

    void onGetMessage (GetMessage msg) {
         System.out.println("Received get request for name: " + msg.getName());
         if (list.containsKey(msg.getName())) {
             String email = list.get(msg.getName());
             sender().tell(new ReplyMessage(email), self());
         } else {
             System.out.println("Requested a name that does not exists");
             sender().tell(new ReplyMessage("The name is not in the list"), self());
         }
    }

    static Props props() {
        return Props.create(ServerActor.class);
    }

}
