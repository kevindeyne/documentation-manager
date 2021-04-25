package com.kdeyne.doc.matcher;

import com.kdeyne.doc.matcher.model.InvocationMatch;
import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtAnnotation;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.code.CtExpressionImpl;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface InvocationMatcher {

    boolean match(CtInvocation<?> invocation);

    InvocationMatch parseValue(Map<String, File> fileMap, Map<String, String> propertiesMap, CtInvocation<?> invocation);

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
            return originalString;
        }
    }

    default String valueAnnotationParse(Map<String, String> propertiesMap, CtVariableRead<?> arg) {
        CtAnnotation<? extends Annotation> annotation = arg.getVariable().getDeclaration().getAnnotations().get(0);
        CtExpression<?> literal = annotation.getValues().values().stream().findFirst().orElse(new CtExpressionImpl() {
            @Override
            public void accept(CtVisitor visitor) {

            }
        });

        final String literalString = literal.toString();
        if(literalString.contains("{")) {
            String propertyKey = literalString.substring(literalString.indexOf("{")+1, literalString.indexOf("}"));
            if(propertyKey.contains(":")) {
                propertyKey = propertyKey.substring(0, propertyKey.indexOf(':'));
            }
            return propertiesMap.getOrDefault(propertyKey, propertyKey);
        } else {
            return valueParse(literal);
        }
    }
}
