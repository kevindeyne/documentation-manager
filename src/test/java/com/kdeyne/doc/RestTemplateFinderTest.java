package com.kdeyne.doc;

import com.kdeyne.doc.matcher.RestTemplateInvocationMatcher;
import com.kdeyne.doc.matcher.model.RestTemplateInvocationMatch;
import com.kdeyne.doc.model.HTTPMethod;
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
import java.util.HashMap;
import java.util.Map;

@Execution(ExecutionMode.CONCURRENT)
class RestTemplateFinderTest extends AbstractTest {

    private final RestTemplateInvocationMatcher matcher = new RestTemplateInvocationMatcher();

    @ParameterizedTest
    @ValueSource(strings = {"matcher/resttemplate/RestTemplate1.txt"})
    void testIsAbleToFindRestTemplate1(String fileName) {
        final CtType<?> model = Launcher.parseClass(readFile(fileName));
        final Map<String, File> fileMap = Collections.singletonMap(model.getQualifiedName(), getFile(fileName));

        RestTemplateInvocationMatch foundRestTemplateInvocationMatch = null;
        for (CtInvocation<?> invocation : model.getElements(new TypeFilter<>(CtInvocation.class))) {
            if (matcher.match(invocation)) {
                foundRestTemplateInvocationMatch = matcher.parseValue(fileMap, new HashMap<>(), invocation);
                break;
            }
        }

        Assertions.assertNotNull(foundRestTemplateInvocationMatch);
        Assertions.assertEquals("http://10.194.186.222:9601/bpm-platform/runtime/tasks/?mark=page", foundRestTemplateInvocationMatch.getUrl());
        Assertions.assertEquals(HTTPMethod.GET, foundRestTemplateInvocationMatch.getHttpMethod());
    }

    @ParameterizedTest
    @ValueSource(strings = {"matcher/resttemplate/RestTemplate2.txt"})
    void testIsAbleToFindRestTemplate2(String fileName) {
        final CtType<?> model = Launcher.parseClass(readFile(fileName));
        final Map<String, File> fileMap = Collections.singletonMap(model.getQualifiedName(), getFile(fileName));

        RestTemplateInvocationMatch foundRestTemplateInvocationMatch = null;
        for (CtInvocation<?> invocation : model.getElements(new TypeFilter<>(CtInvocation.class))) {
            if (matcher.match(invocation)) {
                foundRestTemplateInvocationMatch = matcher.parseValue(fileMap, new HashMap<>(), invocation);
                break;
            }
        }

        Assertions.assertNotNull(foundRestTemplateInvocationMatch);
        Assertions.assertEquals("http://10.194.186.222:9601/bpm-platform/runtime/tasks/?mark=page&assignee={user}&size={size}&page={page}", foundRestTemplateInvocationMatch.getUrl());
        Assertions.assertEquals(HTTPMethod.POST, foundRestTemplateInvocationMatch.getHttpMethod());
    }
}
