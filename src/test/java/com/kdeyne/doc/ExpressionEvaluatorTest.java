package com.kdeyne.doc;

import org.codehaus.commons.compiler.CompilerFactoryFactory;
import org.codehaus.commons.compiler.IExpressionEvaluator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class ExpressionEvaluatorTest {

    @Test
    void testEvaluation() throws Exception {
        Assertions.assertEquals("http://10.194.186.222:9601/bpm-platform/runtime/tasks/?mark=page&assignee={user}&size={size}",
                evalMe(
                "(((\"http://10.194.186.222:9601/bpm-platform/runtime/tasks/?mark=page&assignee=\" + user) + \"&size=\") + size)",
                "user",
                "size"
        ));
    }

    String evalMe(String originalString, String ... params) throws Exception {
        String[] values = new String[params.length];
        Class[] classes = new Class[params.length];
        for (int i = 0; i < params.length; i++) {
            values[i] = "{" + params[i] + "}";
            classes[i] = String.class;
        }

        IExpressionEvaluator ee = CompilerFactoryFactory.getDefaultCompilerFactory().newExpressionEvaluator();
        ee.setExpressionType(String.class);
        ee.setParameters(params, classes);
        ee.cook(originalString);
        String result = ee.evaluate(values).toString();
        System.out.println(result);
        return result;
    }
}
