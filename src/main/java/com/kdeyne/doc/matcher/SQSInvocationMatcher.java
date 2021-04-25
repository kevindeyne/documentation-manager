package com.kdeyne.doc.matcher;

import com.kdeyne.doc.matcher.model.SQSInvocationMatch;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtInvocation;
import spoon.support.reflect.code.CtFieldReadImpl;

import java.io.File;
import java.util.Map;

public class SQSInvocationMatcher implements InvocationMatcher {

    /**
     * Checks if this invocation in question is relevant
     * In this case, is it an SQS SendMessageRequest instance being called with a method that contains 'withQueueUrl'
     * @param invocation The invocation
     * @return Boolean, matches or not
     */
    public boolean match(CtInvocation<?> invocation) {
        return invocation.getTarget() != null &&
                invocation.getTarget().getType() != null &&
                invocation.getExecutable() != null &&
                "com.amazonaws.services.sqs.model.SendMessageRequest".equals(invocation.getTarget().getType().getQualifiedName()) &&
                invocation.getExecutable().getSignature().toLowerCase().contains("withqueueurl");
    }

    /**
     * Defines what argument we are looking for
     * Current impl just looks for the exchange, which is argument 0
     * @param fileMap For potential lookups
     * @param invocation The reference the matcher found
     * @return The resolved value of the argument found
     */
    public SQSInvocationMatch parseValue(Map<String, File> fileMap, Map<String, String> propertiesMap, CtInvocation<?> invocation) {
        SQSInvocationMatch invocationMatch = new SQSInvocationMatch();
        invocationMatch.setQueue(resolveValue(invocation.getArguments().get(0), propertiesMap));
        return invocationMatch;
    }

    /**
     * Found the argument, but this could be a literal value, a reference to a variable, a reference to a variable in another class, or a property
     * This tries to figure that out based on what kind of class the argument is
     * @param exchangeArgument The argument found
     * @return String value
     */
    private String resolveValue(CtExpression<?> exchangeArgument, Map<String, String> propertiesMap) {
        if (exchangeArgument instanceof CtFieldReadImpl) {
            CtFieldRead<?> arg = (CtFieldReadImpl<?>) exchangeArgument;

            String url = null;
            if(arg.getVariable().getFieldDeclaration().getAnnotations().isEmpty()) {
                url = valueParse(arg.getVariable().getFieldDeclaration().getDefaultExpression());
            } else {
                url = valueAnnotationParse(propertiesMap, arg);
            }
            return url.substring(url.lastIndexOf('/')+1);
        } else {
            throw new IllegalArgumentException();
        }
    }
}
