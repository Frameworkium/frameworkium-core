package com.frameworkium.pages.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TypifiedElement;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.google.inject.Inject;

public abstract class BasePage<T extends BasePage<T>> {

    @Inject
    protected WebDriver driver;

    @Inject
    protected WebDriverWait wait;

    protected final Logger logger = LogManager.getLogger(this);

    /** @return Returns the current page object. Useful for e.g. MyPage.get().then().doSomething(); */
    @SuppressWarnings("unchecked")
    public T then() {
        return (T) this;
    }

    /** @return Returns the current page object. Useful for e.g. MyPage.get().then().with().aComponent().clickHome(); */
    @SuppressWarnings("unchecked")
    public T with() {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    public T get() {
        HtmlElementLoader.populatePageObject(this, driver);
        try {
            waitForExpectedVisibleElements(this);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    public T get(String url) {
        driver.get(url);
        return get();
    }

    @SuppressWarnings("unchecked")
    private void waitForExpectedVisibleElements(Object pageObject) throws IllegalArgumentException,
            IllegalAccessException {
        for (Field field : pageObject.getClass().getDeclaredFields()) {
            for (Annotation annotation : field.getDeclaredAnnotations()) {
                if (annotation instanceof Visible) {
                    field.setAccessible(true);
                    Object obj = field.get(pageObject);

                    // This handles Lists of elements e.g. List<WebElement>
                    if (obj instanceof List) {
                        // We'll only check visibility of first match in list
                        obj = ((List<Object>) obj).get(0);
                    }

                    // Checks for @Visible tags inside the HtmlElement
                    if (obj instanceof HtmlElement) {
                        waitForExpectedVisibleElements(obj);
                    }

                    WebElement element = null;
                    if (obj instanceof TypifiedElement) {
                        element = ((TypifiedElement) obj).getWrappedElement();
                    } else if (obj instanceof WebElement) {
                        element = (WebElement) obj;
                    } else {
                        throw new IllegalArgumentException(
                                "Only elements of type TypifiedElement or WebElement are supported by @Visible.");
                    }

                    // Retries when an element is looked up before the previous page has unloaded or before doc ready
                    try {
                        wait.until(ExpectedConditions.visibilityOf(element));
                    } catch (StaleElementReferenceException serex) {
                        logger.info("Caught StaleElementReferenceException");
                        tryToEnsureWeHaveUnloadedOldPageAndNewPageIsReady();
                        wait.until(ExpectedConditions.visibilityOf(element));
                    }
                }
            }
        }
    }

    private void tryToEnsureWeHaveUnloadedOldPageAndNewPageIsReady() {
        for (int tries = 0; tries < 3; tries++) {
            Boolean documentReady = (Boolean) executeJS("return document.readyState == 'complete'");
            if (!documentReady) {
                logger.warn("Caught StaleElementReferenceException");
                tries--;
            }
        }
    }

    /**
     * @param javascript the Javascript to execute on the current page
     * @return Returns an Object returned by the Javascript provided
     */
    public Object executeJS(String javascript) {
        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
        return jsExecutor.executeScript(javascript);
    }

    /** @return Returns the title of the web page */
    public String getTitle() {
        return driver.getTitle();
    }

    /** @return Returns the source code of the current page */
    public String getSource() {
        return driver.getPageSource();
    }
}
