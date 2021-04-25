package com.kdeyne.doc;

import com.kdeyne.doc.matcher.RabbitMQInvocationMatcher;
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

@Execution(ExecutionMode.CONCURRENT)
class RabbitMQFinderTest extends AbstractTest {

    private final RabbitMQInvocationMatcher matcher = new RabbitMQInvocationMatcher();

    @ParameterizedTest
    @ValueSource(strings = {"matcher/rabbitmq/RabbitMQTestFile1.txt", "matcher/rabbitmq/RabbitMQTestFile2.txt", "matcher/rabbitmq/RabbitMQTestFile3.txt", "matcher/rabbitmq/RabbitMQTestFile4.txt"})
    void testIsAbleToFindRabbitMQCall(String fileName) {
        final CtType<?> model = Launcher.parseClass(readFile(fileName));
        final Map<String, File> fileMap = Collections.singletonMap(model.getQualifiedName(), getFile(fileName));

        String foundExchangeName = null;
        for (CtInvocation<?> invocation : model.getElements(new TypeFilter<>(CtInvocation.class))) {
            if (matcher.match(invocation)) {
                foundExchangeName = matcher.parseValue(fileMap, Collections.singletonMap("spring.exchange", "exchangeName-123s23445622344524"), invocation).getExchange();
            }
        }

        Assertions.assertEquals("exchangeName-123s23445622344524", foundExchangeName);
    }
}
