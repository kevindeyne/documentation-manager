package com.kdeyne.doc;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtLiteralImpl;

public class RabbitMQInvocationMatcher {

    public static boolean match(CtInvocation invocation) {
        return invocation.getTarget() != null &&
                "org.springframework.amqp.rabbit.core.RabbitTemplate".equals(invocation.getTarget().getType().getQualifiedName()) &&
                "convertAndSend(java.lang.String)".equals(invocation.getExecutable().getSignature());
    }

    public static String parseValue(CtInvocation invocation) {
        CtExpression exchangeArgument = (CtExpression) invocation.getArguments().get(0);
        return resolveValue(exchangeArgument);
    }

    private static String resolveValue(CtExpression exchangeArgument) {
        if(exchangeArgument instanceof CtLiteralImpl) {
            return valueParse(exchangeArgument);
        } else if (exchangeArgument instanceof CtFieldReadImpl) {
            CtFieldReadImpl arg = (CtFieldReadImpl) exchangeArgument;
            return valueParse(arg.getVariable().getFieldDeclaration().getDefaultExpression());
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static String valueParse(Object obj) {
        return obj.toString().replace("\"", "");
    }
}
