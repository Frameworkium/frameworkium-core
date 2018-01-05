package com.frameworkium.core.ui.js;

import com.frameworkium.core.ui.ExtraExpectedConditions;
import com.frameworkium.core.ui.js.framework.Angular;
import com.frameworkium.core.ui.js.framework.AngularTwo;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Wait;

import java.util.Arrays;

/**
 * Frameworkium implementation of waiting for JS events on page-load.
 */
public class JavascriptWait {

    private final Wait<WebDriver> wait;
    private final JavascriptExecutor javascriptExecutor;

    private AbstractFramework detectedFramework;

    public JavascriptWait(WebDriver driver, Wait<WebDriver> wait) {
        this.wait = wait;
        this.javascriptExecutor = (JavascriptExecutor) driver;
    }

    /**
     * Default entry to {@link JavascriptWait}.
     * The following actions are waited for:
     * <ol>
     * <li>Document state to be ready</li>
     * <li>If page is using a supported JS framework, it will detect and wait</li>
     * </ol>
     */
    public void waitForJavascriptEventsOnLoad() {
        waitForDocumentReady();
        detectFramework();
        waitForJavascriptFramework();
    }

    /**
     * If a page is using a supported JS framework, it will wait until it's ready.
     */
    public void waitForJavascriptFramework() {
        if (detectedFramework != null) {
            detectedFramework.waitToBeReady(javascriptExecutor);
        }
    }

    private void waitForDocumentReady() {
        wait.until(ExtraExpectedConditions.documentBodyReady());
    }

    /**
     * Assumes, implicitly, only one framework is present.
     * If there are multiple we will pick an arbitrary one.
     */
    private void detectFramework() {
        detectedFramework = Arrays.stream(SupportedFramework.values())
                .map(SupportedFramework::getInstance)
                .filter(framework -> framework.isPresent(javascriptExecutor))
                .findFirst()
                .orElse(null);
    }

    /**
     * Supported JS Frameworks within Frameworkium and their class which
     * implement {@link AbstractFramework}.
     */
    private enum SupportedFramework {
        ANGULAR(Angular.class),
        ANGULAR_TWO(AngularTwo.class);

        private AbstractFramework frameworkInstance;

        SupportedFramework(Class<? extends AbstractFramework> frameworkClass) {
            try {
                this.frameworkInstance = frameworkClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }

        AbstractFramework getInstance() {
            return frameworkInstance;
        }
    }
}
