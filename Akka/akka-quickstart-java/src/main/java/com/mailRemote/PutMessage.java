package com.mailRemote;

public class PutMessage {

    private String email;
    private String name;

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public PutMessage(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
