package com.kdeyne.doc.matcher;

import com.kdeyne.doc.matcher.model.InvocationMatch;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;
import spoon.reflect.code.CtInvocation;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface InvocationMatcher {

    boolean match(CtInvocation<?> invocation);

    InvocationMatch parseValue(Map<String, File> fileMap, CtInvocation<?> invocation);

    Pattern PATTERN = Pattern.compile("\\w+");

    /**
     * Loaded objects are loaded as double quoted, so we remove them
     * @param obj Object ready to toString and remove double quotes
     * @return The final clean value
     */
    default String valueParse(Object obj) {
        final String expression = obj.toString();

        Set<String> variables = new HashSet<>();
        Matcher m = PATTERN.matcher(expression);
        while(m.find()) {
            final String group = m.group();
            if(Character.isLetter(group.charAt(0))) {
                variables.add(group);
            }
        }

        return evalCode(expression, variables.toArray(new String[0]));
    }

    default String evalCode(String originalString, String ... params) {
        try{
            Object[] values = new String[params.length];
            Class<?>[] classes = new Class[params.length];
            for (int i = 0; i < params.length; i++) {
                values[i] = "{" + params[i] + "}";
                classes[i] = String.class;
            }

            IExpressionEvaluator ee = CompilerFactoryFactory.getDefaultCompilerFactory().newExpressionEvaluator();
            ee.setExpressionType(String.class);
            ee.setParameters(params, classes);
            ee.cook(originalString);
            return ee.evaluate(values).toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
