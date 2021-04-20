package com.kdeyne.doc.matcher;

import com.kdeyne.doc.matcher.model.InvocationMatch;
import spoon.reflect.code.CtInvocation;

import java.io.File;
import java.util.Map;
import java.util.regex.Pattern;

public interface InvocationMatcher {

    boolean match(CtInvocation<?> invocation);

    InvocationMatch parseValue(Map<String, File> fileMap, CtInvocation<?> invocation);

    /**
     * Loaded objects are loaded as double quoted, so we remove them
     * @param obj Object ready to toString and remove double quotes
     * @return The final clean value
     */
    //TODO this is messy ... we should somehow have to evaluate the expression before we try parsing it OR we just cut everything that's not clear
    default String valueParse(Object obj) {
        final String simpleParse = obj.toString().replace("\"", "");
        if(simpleParse.contains("(") && simpleParse.contains(")")) {
            StringBuilder builder = new StringBuilder();
            final String[] split = simpleParse.split("[(|)]");
            for(String part : split) {
                if(!part.isEmpty()) {
                    if(part.startsWith(" + &")) {
                        builder.append(part.replaceAll(Pattern.quote(" + "), ""));
                    } else if(part.contains(" + ")) {
                        builder.append(part.replaceAll(Pattern.quote(" + "), "{"));
                        builder.append("}");
                    } else {
                        builder.append(part);
                    }
                }
            }
            return builder.toString();
        } else {
            return simpleParse;
        }
    }
}
