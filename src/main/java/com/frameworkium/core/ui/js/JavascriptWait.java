package com.frameworkium.core.ui.js;

import com.frameworkium.core.ui.ExtraExpectedConditions;
import com.frameworkium.core.ui.js.framework.Angular;
import com.frameworkium.core.ui.js.framework.AngularTwo;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;

import java.util.Arrays;

/**
 * Frameworkium implementation of waiting for JS events on page-load
 */
public class JavascriptWait {

    private final Wait<WebDriver> wait;
    private final JavascriptExecutor javascriptExecutor;

    private SupportedFramework detectedFramework;

    public JavascriptWait(WebDriver driver, Wait<WebDriver> wait) {
        this.wait = wait;
        this.javascriptExecutor = (JavascriptExecutor) driver;
    }

    /**
     * Default entry to {@link JavascriptWait}
     *
     * Following actions are waited on:
     * <ul>
     *     <li>Document state to be ready</li>
     *     <li>If page is using a supported JS framework, it will detect & wait</li>
     * </ul>
     */
    public void waitForJavascriptEventsOnLoad() {
        waitForDocumentReady();
        detectFramework();
        waitForJavascriptFramework();
    }

    /**
     * If a page is using a supported JS framework, it will wait until it's ready
     */
    public void waitForJavascriptFramework() {
        if (detectedFramework != null) getFrameworkClass(detectedFramework).waitToBeReady(javascriptExecutor);
    }

    private void waitForDocumentReady() {
        wait.until(ExtraExpectedConditions.documentBodyReady());
    }

    private void detectFramework() {
        detectedFramework = Arrays.stream(SupportedFramework.values())
                .filter(supportedFramework -> getFrameworkClass(supportedFramework).isPresent(javascriptExecutor))
                .findFirst()
                .orElse(null);
    }

    private AbstractFramework getFrameworkClass(SupportedFramework supportedFramework) {
        try {
            return ((AbstractFramework) supportedFramework.getFrameworkClass().newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * Supported JS Frameworks within Frameworkium and their class which implement {@link AbstractFramework}
     */
    private enum SupportedFramework {
        ANGULAR(Angular.class),
        ANGULAR_TWO(AngularTwo.class);

        private Class frameworkClass;

        SupportedFramework(Class<? extends AbstractFramework> frameworkClass) {
            this.frameworkClass = frameworkClass;
        }

        public Class getFrameworkClass() {
            return this.frameworkClass;
        }
    }
}
