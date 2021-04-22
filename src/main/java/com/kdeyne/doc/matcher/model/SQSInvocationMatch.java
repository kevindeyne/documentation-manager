package com.kdeyne.doc.matcher.model;

public class SQSInvocationMatch implements InvocationMatch {

    private String queue;

    public String getQueue() {
        return queue;
    }

    public void setQueue(String queue) {
        this.queue = queue;
    }

    @Override
    public void printResult() {
        System.out.println("- Queue name: " + getQueue());
        System.out.println();
    }
}
