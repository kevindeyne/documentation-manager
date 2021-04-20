package com.kdeyne.doc.matcher;

import com.kdeyne.doc.Main;
import com.kdeyne.doc.matcher.model.RabbitMQInvocationMatch;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtType;
import spoon.support.reflect.code.CtFieldReadImpl;
import spoon.support.reflect.code.CtLiteralImpl;
import spoon.support.reflect.code.CtTypeAccessImpl;

import java.util.Map;
import java.io.File;

public class RabbitMQInvocationMatcher implements InvocationMatcher {

    /**
     * Checks if this invocation in question is relevant
     * In this case, is it a RabbitTemplate instance being called with a method that contains 'send'
     * @param invocation The invocation
     * @return Boolean, matches or not
     */
    public boolean match(CtInvocation<?> invocation) {
        return invocation.getTarget() != null &&
                invocation.getTarget().getType() != null &&
                invocation.getExecutable() != null &&
                "org.springframework.amqp.rabbit.core.RabbitTemplate".equals(invocation.getTarget().getType().getQualifiedName()) &&
                invocation.getExecutable().getSignature().toLowerCase().contains("send");
    }

    /**
     * Defines what argument we are looking for
     * Current impl just looks for the exchange, which is argument 0
     * @param fileMap For potential lookups
     * @param invocation The reference the matcher found
     * @return The resolved value of the argument found
     */
    public RabbitMQInvocationMatch parseValue(Map<String, File> fileMap, CtInvocation<?> invocation) {
        RabbitMQInvocationMatch invocationMatch = new RabbitMQInvocationMatch();
        invocationMatch.setExchange(resolveValue(fileMap, invocation.getArguments().get(0)));
        invocationMatch.setRoutingKey(resolveValue(fileMap, invocation.getArguments().get(1)));
        return invocationMatch;
    }

    /**
     * Found the argument, but this could be a literal value, a reference to a variable, a reference to a variable in another class, or a property
     * This tries to figure that out based on what kind of class the argument is
     * @param fileMap Used for lookups in other classes
     * @param exchangeArgument The argument found
     * @return String value
     */
    private String resolveValue(Map<String, File> fileMap, CtExpression<?> exchangeArgument) {
        if(exchangeArgument instanceof CtLiteralImpl) {
            return valueParse(exchangeArgument);
        } else if (exchangeArgument instanceof CtFieldReadImpl) {
            CtFieldRead<?> arg = (CtFieldReadImpl<?>) exchangeArgument;
            return valueParse(arg.getVariable().getFieldDeclaration().getDefaultExpression());
        } else if (exchangeArgument instanceof CtTypeAccessImpl) {
            CtTypeAccess<?> arg = (CtTypeAccessImpl<?>) exchangeArgument;
            final String foreignKey = arg.getAccessedType().getPackage().getQualifiedName();
            final CtType<?> foreignModel = findBestMatch(fileMap, foreignKey);
            if(foreignModel == null) throw new IllegalArgumentException();

            final CtField<?> foreignField = foreignModel.getField(arg.getAccessedType().getSimpleName());
            return valueParse(foreignField.getDefaultExpression());
        } else {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Lookup for another class. We use the foreign key (ie the name of the class referencing)
     * For example if the argument is ServiceA.SOME_FIELD, foreign key would be ServiceA
     * There is a qualified name in the fileModel that we try to match with
     * If found, we load that file in as a CtType so we can read the value of the SOME_FIELD
     * @param fileMap The map to do lookups in
     * @param foreignKey the name of the referencing class we're looking up
     * @return a CtType of the corresponding file
     */
    private CtType<?> findBestMatch(Map<String, File> fileMap, String foreignKey) {
        for(Map.Entry<String, File> entry : fileMap.entrySet()) {
            if(entry.getKey().endsWith(foreignKey)) {
                File relevantFile = entry.getValue();
                if(relevantFile == null) return null;
                return Main.getCtTypes(relevantFile).get(0);
            }
        }
        return null;
    }
}
