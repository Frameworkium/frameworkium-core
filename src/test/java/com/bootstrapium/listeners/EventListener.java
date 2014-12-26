package com.bootstrapium.listeners;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.events.WebDriverEventListener;

public class EventListener implements WebDriverEventListener {

    static final Logger logger = LogManager.getLogger();
    
    @Override
    public void afterChangeValueOf(WebElement element, WebDriver driver) {
        logger.info("changed value of " + elementToString(element));
    }

    @Override
    public void afterClickOn(WebElement element, WebDriver driver) {
        logger.info("clicked " + elementToString(element));
    }

    @Override
    public void afterFindBy(By by, WebElement arg1, WebDriver arg2) {
        logger.info("found " + by);
    }

    @Override
    public void afterNavigateBack(WebDriver driver) {
        logger.info("after back");
    }

    @Override
    public void afterNavigateForward(WebDriver driver) {
        logger.info("after forward");
    }

    @Override
    public void afterNavigateTo(String url, WebDriver driver) {
        logger.info("navigated to " + url);
    }

    @Override
    public void afterScript(String script, WebDriver driver) {
        logger.info("ran script " + script);
    }

    @Override
    public void beforeChangeValueOf(WebElement element, WebDriver driver) {
        logger.info("change value of " + elementToString(element));
        
    }

    @Override
    public void beforeClickOn(WebElement element, WebDriver driver) {
        logger.info("click " + elementToString(element));
    }

    @Override
    public void beforeFindBy(By by, WebElement element, WebDriver arg2) {
        logger.info("find " + by);
    }

    @Override
    public void beforeNavigateBack(WebDriver driver) {
        logger.info("before back");
    }

    @Override
    public void beforeNavigateForward(WebDriver driver) {
        logger.info("before forward");
    }

    @Override
    public void beforeNavigateTo(String url, WebDriver driver) {
        logger.info("navigate to " + url);
    }

    @Override
    public void beforeScript(String script, WebDriver driver) {
        logger.info("running script " + script);
    }

    @Override
    public void onException(Throwable thrw, WebDriver driver) {
        logger.error(thrw.getMessage());
    }

    private String elementToString(WebElement element) {
        String str = element.toString();
        if(str.contains("-> ")) {
            str = str.substring(str.indexOf("-> ") + 3);
        }
        return str;
    }
}
