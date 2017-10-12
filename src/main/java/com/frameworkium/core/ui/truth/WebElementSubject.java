package com.frameworkium.core.ui.truth;


import com.google.common.truth.FailureMetadata;
import com.google.common.truth.StringSubject;
import com.google.common.truth.Subject;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;

public final class WebElementSubject extends Subject<WebElementSubject, WebElement> {

    WebElementSubject(FailureMetadata metadata, @Nullable WebElement actual) {
        super(metadata, actual);
    }

    public void isDisplayed() {
        if (!actual().isDisplayed()) {
            named("WebElement").fail("is displayed");
        }
    }

    public void isEnabled() {
        if (!actual().isEnabled()) {
            named("WebElement").fail("is enabled");
        }
    }

    public void isSelected() {
        if (!actual().isSelected()) {
            named("WebElement").fail("is selected");
        }
    }

    public StringSubject text() {
        return check().that(actual().getText());
    }

    public StringSubject attribute(String attribute) {
        return check().that(actual().getAttribute(attribute));
    }
}
