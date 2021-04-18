package com.kdeyne.doc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;


class RabbitMQFinderTest extends AbstractTest {

    @Test
    void testIsAbleToFindRabbitMQCall() {
        CtType model = Launcher.parseClass(readFile("RabbitMQTestFile1.txt"));
        String foundExchangeName = null;
        for(CtInvocation invocation : model.getElements(new TypeFilter<>(CtInvocation.class))) {
            if(RabbitMQInvocationMatcher.match(invocation)) {
                foundExchangeName = RabbitMQInvocationMatcher.parseValue(invocation);
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
                foundExchangeName = RabbitMQInvocationMatcher.parseValue(invocation);
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
                foundExchangeName = RabbitMQInvocationMatcher.parseValue(invocation);
            }
        }
        Assertions.assertEquals("exchangeName-123s23445622344524", foundExchangeName);
    }


}
