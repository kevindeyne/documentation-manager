package com.kdeyne.doc;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtTypeAccessImpl;

import java.util.Map;
import java.io.File;

public class RabbitMQInvocationMatcher {

    public static boolean match(CtInvocation invocation) {
        return invocation.getTarget() != null &&
                invocation.getTarget().getType() != null &&
                invocation.getExecutable() != null &&
                "org.springframework.amqp.rabbit.core.RabbitTemplate".equals(invocation.getTarget().getType().getQualifiedName()) &&
                invocation.getExecutable().getSignature().toLowerCase().contains("send");
    }

    public static String parseValue(Map<String, File> model, CtInvocation invocation) {
        CtExpression exchangeArgument = (CtExpression) invocation.getArguments().get(0);
        return resolveValue(model, exchangeArgument);
    }

    private static String resolveValue(Map<String, File> model, CtExpression exchangeArgument) {
        if(exchangeArgument instanceof CtLiteralImpl) {
            return valueParse(exchangeArgument);
        } else if (exchangeArgument instanceof CtFieldReadImpl) {
            CtFieldReadImpl arg = (CtFieldReadImpl) exchangeArgument;
            return valueParse(arg.getVariable().getFieldDeclaration().getDefaultExpression());
        } else if (exchangeArgument instanceof CtTypeAccessImpl) {
            CtTypeAccessImpl arg = (CtTypeAccessImpl) exchangeArgument;
            final String foreignKey = arg.getAccessedType().getPackage().getQualifiedName();
            final CtType foreignModel = findBestMatch(model, foreignKey);
            if(foreignModel == null) throw new IllegalArgumentException();

            final CtField foreignField = foreignModel.getField(arg.getAccessedType().getSimpleName());
            return valueParse(foreignField.getDefaultExpression());
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static CtType findBestMatch(Map<String, File> model, String foreignKey) {
        for(Map.Entry<String, File> entry : model.entrySet()) {
            if(entry.getKey().endsWith(foreignKey)) {
                File relevantFile = entry.getValue();
                if(relevantFile == null) return null;
                return Main.getCtTypes(relevantFile).get(0);
            }
        }
        return null;
    }

    private static String valueParse(Object obj) {
        return obj.toString().replace("\"", "");
    }
}
