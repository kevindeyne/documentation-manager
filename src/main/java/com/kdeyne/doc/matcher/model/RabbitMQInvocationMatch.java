package com.kdeyne.doc.matcher.model;

public class RabbitMQInvocationMatch implements InvocationMatch {

    private String exchange;
    private String routingKey;

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    @Override
    public void printResult() {
        System.out.println("- Exchange name: " + getExchange());
        System.out.println("- Routing key: " + getRoutingKey());
        System.out.println();
    }
}
