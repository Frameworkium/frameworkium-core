package com.frameworkium.core.ui.listeners;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.support.events.WebDriverEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoggingListener implements WebDriverEventListener {

    private static final Logger logger = LogManager.getLogger();

    private static String getLocatorFromElement(WebElement element) {
        String str = element.toString();
        Pattern p = Pattern.compile("->\\s(.*)(?=\\])");
        Matcher m = p.matcher(str);
        return m.find() && m.groupCount() > 0
                ? m.group(1)
                : str;
    }

    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysSent) {
        logger.debug(() -> "changed value of element with " + getLocatorFromElement(element));
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {
        logger.debug(() -> "clicked element with " + getLocatorFromElement(element));
    }

    @Override
    public void afterFindBy(By by, WebElement element, WebDriver driver) {
        logger.debug("found element {}", by);
    }

    @Override
    public void afterNavigateBack(WebDriver driver) {
        logger.debug("after back");
    }

    @Override
    public void afterNavigateForward(WebDriver driver) {
        logger.debug("after forward");
    }

    @Override
    public void beforeNavigateRefresh(WebDriver webDriver) {
        logger.debug("before Navigate Refresh");
    }

    @Override
    public void afterNavigateRefresh(WebDriver webDriver) {
        logger.debug("after Navigate Refresh");
    }

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {
        logger.debug("navigated to {}", url);
    }

    @Override
    public void afterScript(String script, WebDriver driver) {
        // Only log part of a long script
        // We log more of script in beforeScript
        logger.debug(() -> "ran script " + StringUtils.abbreviate(script, 128));
    }

    @Override
    public void beforeSwitchToWindow(String windowName, WebDriver driver) {
        logger.debug("before switch to window " + windowName);
    }

    @Override
    public void afterSwitchToWindow(String windowName, WebDriver driver) {
        logger.debug("after switch to window " + windowName);
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver, CharSequence[] keysToSend) {
        logger.debug(() -> "change value of element with " + getLocatorFromElement(element));
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        logger.debug(() -> "click element with " + getLocatorFromElement(element));
    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver driver) {
        logger.debug("before find element by {}", by);
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        logger.debug("before back");
    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        logger.debug("before forward");
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        logger.debug("navigate to " + url);
    }

    @Override
    public void beforeScript(String script, WebDriver driver) {
        // Only log part of a long script
        logger.debug("running script " + StringUtils.abbreviate(script, 512));
    }

    @Override
    public void onException(Throwable thrw, WebDriver driver) {
        // Lots of caught exceptions being logged here
        logger.trace("Event listener onException().", thrw);
    }

    @Override
    public <X> void beforeGetScreenshotAs(OutputType<X> outputType) {
        logger.trace("Before get screenshot as");
    }

    @Override
    public <X> void afterGetScreenshotAs(OutputType<X> outputType, X x) {
        logger.trace("After get screenshot as");
    }

    @Override
    public void beforeGetText(WebElement webElement, WebDriver webDriver) {
        logger.trace("Before get text");
    }

    @Override
    public void afterGetText(WebElement webElement, WebDriver webDriver, String s) {
        logger.trace("After get text");
    }

    @Override
    public void beforeAlertAccept(WebDriver webDriver) {
        logger.debug("before alert accept");
    }

    @Override
    public void afterAlertAccept(WebDriver webDriver) {
        logger.debug("after alert accept");
    }

    @Override
    public void beforeAlertDismiss(WebDriver webDriver) {
        logger.debug("before alert dismiss");
    }

    @Override
    public void afterAlertDismiss(WebDriver webDriver) {
        logger.debug("after alert accept");
    }
}
