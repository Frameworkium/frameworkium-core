package com.frameworkium.core.ui.wait;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.FluentWait;

import java.time.Duration;

import static java.text.MessageFormat.format;

public class AsyncIOWait {
    private static final Logger logger = LogManager.getLogger();
    private Runnable runnable;
    private Duration polling;
    private Duration timeout;
    private long minimumRequestToWaitOn = 1L;
    private WebDriver driver;

    public AsyncIOWait(WebDriver driver) {
        this.driver = driver;
    }

    public AsyncIOWait runOnExecute(final Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    private long getRequestCount() {
        final String performanceEntryGetterScript = "return window.performance.getEntries().length";
        final long requestCount = (Long) ((JavascriptExecutor) driver).executeScript(performanceEntryGetterScript);
        logger.debug(() -> format("Found {0} request", requestCount));
        return requestCount;
    }

    public AsyncIOWait forAtLeast(long numberOfIO) {
        this.minimumRequestToWaitOn = numberOfIO;
        return this;
    }

    public AsyncIOWait pollingEvery(Duration polling) {
        this.polling = polling;
        return this;
    }

    public AsyncIOWait withTimeout(Duration timeout) {
        this.timeout = timeout;
        return this;
    }

    public void execute() {
        final FluentWait<WebDriver> wait = new FluentWait<>(driver);
        long initialRequestCount = getRequestCount();

        // execute
        if (runnable != null) {
            runnable.run();
        }

        // wait
        try {
            wait.pollingEvery(polling)
                    .withTimeout(timeout)
                    .until(d -> getRequestCount() - initialRequestCount >= minimumRequestToWaitOn);
        } catch (TimeoutException toe) {
            //ignore
            logger.warn(toe);
        }
    }
}
