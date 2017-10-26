package com.github.xenteros.server;

import java.util.UUID;

public class Session {

    private UUID uuid;
    private String name;
    private Long startTime;

    public Session() {
        startTime = System.currentTimeMillis();
        uuid = UUID.randomUUID();
        name = "Anonymous";
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getStartTime() {
        return startTime;
    }

}
