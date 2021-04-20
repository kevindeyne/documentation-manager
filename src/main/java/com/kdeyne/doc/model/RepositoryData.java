package com.kdeyne.doc.model;

import com.kdeyne.doc.matcher.model.RabbitMQInvocationMatch;

import java.util.ArrayList;
import java.util.List;

public class RepositoryData {

    private String name;
    private List<RabbitMQInvocationMatch> rabbitMQConnections = new ArrayList<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RabbitMQInvocationMatch> getRabbitMQConnections() {
        return rabbitMQConnections;
    }

    public void setRabbitMQConnections(List<RabbitMQInvocationMatch> rabbitMQConnections) {
        this.rabbitMQConnections = rabbitMQConnections;
    }
}
