package com.kdeyne.doc;

import com.kdeyne.doc.matcher.SQSInvocationMatcher;
import com.kdeyne.doc.matcher.model.SQSInvocationMatch;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import spoon.Launcher;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtType;
import spoon.reflect.visitor.filter.TypeFilter;

import java.io.File;
import java.util.Collections;
import java.util.Map;

//https://github.com/bogdantanase92/searcher-weather/tree/ef4383ec64664f973ff1bdfb43af6c045c62b0e7
@Execution(ExecutionMode.CONCURRENT)
class SQSFinderTest extends AbstractTest {

    private final SQSInvocationMatcher matcher = new SQSInvocationMatcher();

    @ParameterizedTest
    @ValueSource(strings = {"matcher/sqs/SQSTestFile1.txt"})
    void testIsAbleToFindSQS(String fileName) {
        final CtType<?> model = Launcher.parseClass(readFile(fileName));
        final Map<String, File> fileMap = Collections.singletonMap(model.getQualifiedName(), getFile(fileName));

        SQSInvocationMatch sqsInvocationMatch = null;
        for (CtInvocation<?> invocation : model.getElements(new TypeFilter<>(CtInvocation.class))) {
            if (matcher.match(invocation)) {
                sqsInvocationMatch = matcher.parseValue(fileMap, invocation);
                break;
            }
        }

        Assertions.assertNotNull(sqsInvocationMatch);
        Assertions.assertEquals("weather", sqsInvocationMatch.getQueue());
    }

}
