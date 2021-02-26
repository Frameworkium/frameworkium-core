package com.frameworkium.integration.theinternet.pages;

import com.frameworkium.core.htmlelements.element.Link;
import com.frameworkium.core.ui.annotations.ForceVisible;
import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import io.qameta.allure.Step;
import org.openqa.selenium.support.FindBy;

import java.time.Duration;

import static java.time.temporal.ChronoUnit.SECONDS;

public class WelcomePage extends BasePage<WelcomePage> {

    @Visible
    @FindBy(linkText = "Checkboxes")
    private Link checkboxesLink;

    // ForceVisible not strictly required, just testing it doesn't error
    @ForceVisible
    @FindBy(linkText = "Drag and Drop")
    private Link dragAndDropLink;

    @FindBy(linkText = "Dynamic Loading")
    private Link dynamicLoadingLink;

    @FindBy(linkText = "Hovers")
    private Link hoversLink;

    @FindBy(linkText = "JavaScript Alerts")
    private Link javascriptAlertsLink;

    @FindBy(linkText = "Key Presses")
    private Link keyPressesLink;

    @Step("Navigate to https://the-internet.herokuapp.com")
    public static WelcomePage open() {
        return PageFactory.newInstance(
                WelcomePage.class, "https://the-internet.herokuapp.com");
    }

    /**
     * Static factory method for this page object with specified timeout.
     *
     * @param timeout timeout in seconds
     * @return An instance of this page object with specified wait timeout
     */
    @Step("Navigate to 'The Internet'")
    public static WelcomePage open(long timeout) {
        return PageFactory.newInstance(
                WelcomePage.class,
                "https://the-internet.herokuapp.com",
                Duration.of(timeout, SECONDS));
    }

    @Step("Click the Checkboxes link")
    public CheckboxesPage clickCheckboxesLink() {
        checkboxesLink.click();
        return PageFactory.newInstance(
                CheckboxesPage.class, Duration.of(15, SECONDS));
    }

    @Step("Click the Drag And Drop link")
    public DragAndDropPage clickDragAndDropLink() {
        dragAndDropLink.click();
        return PageFactory.newInstance(DragAndDropPage.class);
    }

    @Step("Click the Hovers link")
    public HoversPage clickHoversLink() {
        hoversLink.click();
        return PageFactory.newInstance(HoversPage.class);
    }

    @Step("Click the JavaScript Alerts link")
    public JavaScriptAlertsPage clickJavascriptAlertsLink() {
        javascriptAlertsLink.click();
        return PageFactory.newInstance(JavaScriptAlertsPage.class);
    }

    @Step("Click the Key Presses link")
    public KeyPressesPage clickKeyPressesLink() {
        keyPressesLink.click();
        return PageFactory.newInstance(KeyPressesPage.class);
    }
}
