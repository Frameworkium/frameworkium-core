package com.frameworkium.core.htmlelements.element;

import org.openqa.selenium.*;

import java.util.List;

/**
 * The base class to be used for making classes representing typified elements
 * i.e web page controls such as text inputs, buttons or more complex elements.
 * <p/>
 * There are several already written classes representing standard web page controls:
 * <ul>
 * <li>{@link Table}</li>
 * <li>{@link TextInput}</li>
 * <li>{@link CheckBox}</li>
 * <li>{@link Select}</li>
 * <li>{@link Link}</li>
 * <li>{@link Button}</li>
 * <li>{@link Radio}</li>
 * <li>{@link TextBlock}</li>
 * <li>{@link Image}</li>
 * <li>{@link Form}</li>
 * <li>{@link FileInput}</li>
 * </ul>
 */
public abstract class TypifiedElement implements WrapsElement, WebElement {
    private final WebElement wrappedElement;

    /**
     * Specifies wrapped {@link WebElement}.
     *
     * @param wrappedElement {@code WebElement} to wrap.
     */
    protected TypifiedElement(WebElement wrappedElement) {
        this.wrappedElement = wrappedElement;
    }

    /**
     * Determines whether or not this element exists on page.
     *
     * @return True if the element exists on page, false otherwise.
     */
    public boolean exists() {
        try {
            getWrappedElement().isDisplayed();
        } catch (NoSuchElementException ignored) {
            return false;
        }
        return true;
    }

    @Override
    public WebElement getWrappedElement() {
        return wrappedElement;
    }

    @Override
    public void click() {
        getWrappedElement().click();
    }

    @Override
    public void submit() {
        getWrappedElement().submit();
    }

    @Override
    public void sendKeys(CharSequence... keysToSend) {
        getWrappedElement().sendKeys(keysToSend);
    }

    @Override
    public void clear() {
        getWrappedElement().clear();
    }

    @Override
    public String getTagName() {
        return getWrappedElement().getTagName();
    }

    @Override
    public String getAttribute(String name) {
        return getWrappedElement().getAttribute(name);
    }

    @Override
    public boolean isSelected() {
        return getWrappedElement().isSelected();
    }

    @Override
    public boolean isEnabled() {
        return getWrappedElement().isEnabled();
    }

    @Override
    public String getText() {
        return getWrappedElement().getText();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return getWrappedElement().findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return getWrappedElement().findElement(by);
    }

    @Override
    public boolean isDisplayed() {
        return getWrappedElement().isDisplayed();
    }

    @Override
    public Point getLocation() {
        return getWrappedElement().getLocation();
    }

    @Override
    public Dimension getSize() {
        return getWrappedElement().getSize();
    }

    @Override
    public Rectangle getRect() {
        return getWrappedElement().getRect();
    }

    @Override
    public String getCssValue(String propertyName) {
        return getWrappedElement().getCssValue(propertyName);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> target) {
        return getWrappedElement().getScreenshotAs(target);
    }
}
