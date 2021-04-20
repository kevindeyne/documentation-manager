package com.kdeyne.doc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

class BracketsValueParserTest {

    private String fullString = "((((http://10.194.186.222:9601/bpm-platform/runtime/tasks/?mark=page&assignee= + user) + &size=) + size) + &page=) + page + /test?123=123";

    @Test
    void testParser() {
        StringBuffer builder = new StringBuffer();
        final String[] split = fullString.split("[(|)]");
        for(String part : split) {
            if(!part.isEmpty()) {
                if(part.startsWith(" + &") || part.startsWith(" + /")) {
                    builder.append(part.replaceAll(Pattern.quote(" + "), ""));
                } else if(part.contains(" + ")) {
                    builder.append(part.replaceAll(Pattern.quote(" + "), "{"));
                    builder.append("}");
                } else {
                    builder.append(part);
                }
            }
        }
        Assertions.assertEquals("http://10.194.186.222:9601/bpm-platform/runtime/tasks/?mark=page&assignee={user}&size={size}&page={page}/test?123=123", builder.toString());
    }

}
