package com.nidhinsai.mobile.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

/**
 * TestNG IAnnotationTransformer that automatically wires {@link RetryAnalyzer}
 * into every {@code @Test} method in the mobile test suite.
 *
 * Register in testng.xml:
 * <pre>
 *   &lt;listeners&gt;
 *     &lt;listener class-name="com.nidhinsai.mobile.utils.RetryTransformer"/&gt;
 *   &lt;/listeners&gt;
 * </pre>
 */
public class RetryTransformer implements IAnnotationTransformer {

    private static final Logger LOG = LogManager.getLogger(RetryTransformer.class);

    @Override
    public void transform(ITestAnnotation annotation,
                          Class testClass,
                          Constructor testConstructor,
                          Method testMethod) {
        if (annotation.getRetryAnalyzerClass() == null) {
            annotation.setRetryAnalyzer(RetryAnalyzer.class);
            if (testMethod != null) {
                LOG.trace("RetryAnalyzer wired into: {}.{}",
                        testMethod.getDeclaringClass().getSimpleName(), testMethod.getName());
            }
        }
    }
}
