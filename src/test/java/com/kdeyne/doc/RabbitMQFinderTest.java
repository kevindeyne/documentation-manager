package com.kdeyne.doc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.util.Arrays;
import java.util.Collections;


class RabbitMQFinderTest extends AbstractTest {

    @Test
    void testIsAbleToFindRabbitMQCall() {
        CtType model = Launcher.parseClass(readFile("RabbitMQTestFile1.txt"));
        String foundExchangeName = null;
        for(CtInvocation invocation : model.getElements(new TypeFilter<>(CtInvocation.class))) {
            if(RabbitMQInvocationMatcher.match(invocation)) {
                foundExchangeName = parseValue(model, invocation);
            }
        }
        Assertions.assertEquals("exchangeName-123s23445622344524", foundExchangeName);
    }

    @Test
    void testIsAbleToFindRabbitMQCall2() {
        CtType model = Launcher.parseClass(readFile("RabbitMQTestFile2.txt"));
        String foundExchangeName = null;
        for(CtInvocation invocation : model.getElements(new TypeFilter<>(CtInvocation.class))) {
            if(RabbitMQInvocationMatcher.match(invocation)) {
                foundExchangeName = parseValue(model, invocation);
            }
        }
        Assertions.assertEquals("exchangeName-123s23445622344524", foundExchangeName);
    }

    @Test
    void testIsAbleToFindRabbitMQCall3() {
        CtType model = Launcher.parseClass(readFile("RabbitMQTestFile3.txt"));
        String foundExchangeName = null;
        for(CtInvocation invocation : model.getElements(new TypeFilter<>(CtInvocation.class))) {
            if(RabbitMQInvocationMatcher.match(invocation)) {
                foundExchangeName = parseValue(model, invocation);
            }
        }
        Assertions.assertEquals("exchangeName-123s23445622344524", foundExchangeName);
    }

    private String parseValue(CtType model, CtInvocation invocation) {
        return RabbitMQInvocationMatcher.parseValue(Main.buildLookupModel(Collections.singletonList(model)), invocation);
    }
}
