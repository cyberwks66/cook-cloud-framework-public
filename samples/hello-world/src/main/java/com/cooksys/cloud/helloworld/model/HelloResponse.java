package com.cooksys.cloud.helloworld.model;

public class HelloResponse {

    private String greeting;
    private String name;
    private String version;

    public String getVersion() {
        return version;
    }

    public HelloResponse setVersion(String version) {
        this.version = version;
        return this;
    }

    public HelloResponse() {
    }

    public String getGreeting() {
        return greeting;
    }

    public void setGreeting(String greeting) {
        this.greeting = greeting;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}