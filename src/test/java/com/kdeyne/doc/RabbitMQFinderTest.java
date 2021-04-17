package com.kdeyne.doc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import spoon.Launcher;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;


class RabbitMQFinderTest extends AbstractTest {

    @Test
    void testIsAbleToFindRabbitMQCall() {
        CtType model = Launcher.parseClass(readFile("RabbitMQTestFile1.txt"));

        String foundExchangeName = null;

        for(CtInvocation invocation : model.getElements(new TypeFilter<>(CtInvocation.class))) {
            if(invocation.getTarget() != null &&
                "org.springframework.amqp.rabbit.core.RabbitTemplate".equals(invocation.getTarget().getType().getQualifiedName()) &&
                "convertAndSend(java.lang.String)".equals(invocation.getExecutable().getSignature())) {
                CtExpression exchangeArgument = (CtExpression) invocation.getArguments().get(0);
                foundExchangeName = resolveValue(model, exchangeArgument);
            }
        }
        Assertions.assertEquals("exchangeName-123s23445622344524", foundExchangeName);
    }

    private String resolveValue(CtType model, CtExpression exchangeArgument) {
        final String fieldName = exchangeArgument.toString();
        return model.getField(fieldName).getDefaultExpression().toString().replace("\"", "");
    }
}
