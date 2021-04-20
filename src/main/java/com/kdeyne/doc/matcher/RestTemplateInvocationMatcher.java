package com.kdeyne.doc.matcher;

import com.kdeyne.doc.matcher.model.RestTemplateInvocationMatch;
import com.kdeyne.doc.model.HTTPMethod;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;

import java.io.File;
import java.util.Map;

public class RestTemplateInvocationMatcher implements InvocationMatcher {

    /**
     * Checks if this invocation in question is relevant
     * In this case, is it a RabbitTemplate instance being called with a method that contains 'send'
     * @param invocation The invocation
     * @return Boolean, matches or not
     */
    public boolean match(CtInvocation<?> invocation) {
        return invocation.getTarget() != null &&
                invocation.getTarget().getType() != null &&
                invocation.getArguments() != null &&
                !invocation.getArguments().isEmpty() &&
                "org.springframework.web.client.RestTemplate".equals(invocation.getTarget().getType().getQualifiedName()) &&
                "url".equals(((CtVariableRead<?>)invocation.getArguments().get(0)).getVariable().getSimpleName());
    }

    /**
     * Defines what argument we are looking for
     * Current impl just looks for the exchange, which is argument 0
     * @param fileMap For potential lookups
     * @param invocation The reference the matcher found
     * @return The resolved value of the argument found
     */
    public RestTemplateInvocationMatch parseValue(Map<String, File> fileMap, CtInvocation<?> invocation) {
        RestTemplateInvocationMatch invocationMatch = new RestTemplateInvocationMatch();

        CtVariableRead<?> uriVariable = (CtVariableRead<?>)invocation.getArguments().get(0);
        String url = valueParse(uriVariable.getVariable().getDeclaration().getDefaultExpression());
        invocationMatch.setUrl(url);
        invocationMatch.setHttpMethod(findMatchingHttpMethod(invocation));
        return invocationMatch;
    }

    private HTTPMethod findMatchingHttpMethod(CtInvocation<?> invocation) {
        final String methodRepresentation = invocation.toString();
        for(HTTPMethod method : HTTPMethod.values()) {
            if(methodRepresentation.contains(method.name().toLowerCase())) {
                return method;
            }
        }
        return null;
    }
}
