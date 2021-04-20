package com.kdeyne.doc;

import com.kdeyne.doc.matcher.InvocationMatcher;
import com.kdeyne.doc.matcher.RabbitMQInvocationMatcher;
import com.kdeyne.doc.matcher.RestTemplateInvocationMatcher;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Matchers {

    public static final Set<InvocationMatcher> INVOCATION_MATCHERS = new HashSet<>(Arrays.asList(new RabbitMQInvocationMatcher(), new RestTemplateInvocationMatcher()));

}
