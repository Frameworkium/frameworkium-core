package com.frameworkium.core.ui.truth;

import com.google.common.truth.FailureMetadata;
import com.google.common.truth.IterableSubject;
import com.google.common.truth.Subject;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public final class ListWebElementSubject extends Subject<ListWebElementSubject, List<? extends WebElement>> {

    public ListWebElementSubject(FailureMetadata metadata, @Nullable List<? extends WebElement> actual) {
        super(metadata, actual);
    }

    public void atLeastOneContains(String text) {
        if (actual().stream()
                .noneMatch(actual -> actual.getText().contains(text))) {
            named("WebElements").fail("at least one contains", text);
        }
    }

    public void atLeastOneContainsIgnoringCase(String text) {
        if (actual().stream()
                .noneMatch(actual -> StringUtils.containsIgnoreCase(actual.getText(), text))) {
            named("WebElements").fail("at least one contains, ignoring case", text);
        }
    }

    public IterableSubject text() {
        return check().that(actual().stream().map(WebElement::getText).collect(Collectors.toList()));
    }

    public IterableSubject attribute(String attribute) {
        return check().that(actual().stream().map(webElement -> webElement.getAttribute(attribute)).collect(Collectors.toList()));
    }
}

