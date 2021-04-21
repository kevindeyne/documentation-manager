package com.kdeyne.doc.matcher;

import com.kdeyne.doc.matcher.model.InvocationMatch;
import spoon.reflect.code.CtInvocation;

import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
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
        try {
            final String expression = obj.toString();

            StringBuilder builder = new StringBuilder();
            Set<String> variables = new HashSet<>();
            Matcher m = PATTERN.matcher(expression);
            while(m.find()) {
                final String group = m.group();
                if(Character.isLetter(group.charAt(0))) {
                    variables.add(group);
                }
            }

            for(String variable : variables) {
                builder.append("var " + variable + " = '{" + variable + "}'; ");
            }

            builder.append(expression);
            return new ScriptEngineManager().getEngineByName("JavaScript").eval(builder.toString()).toString();
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }
}
