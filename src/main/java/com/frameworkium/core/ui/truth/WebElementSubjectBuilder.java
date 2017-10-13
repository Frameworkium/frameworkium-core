package com.frameworkium.core.ui.truth;

import com.google.common.truth.CustomSubjectBuilder;
import com.google.common.truth.FailureMetadata;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.util.List;

public class WebElementSubjectBuilder extends CustomSubjectBuilder {

    private WebElementSubjectBuilder(FailureMetadata metadata) {
        super(metadata);
    }

    public final WebElementSubject that(@Nullable WebElement actual) {
        return new WebElementSubject(metadata(), actual);
    }

    public final ListWebElementSubject that(@Nullable List<? extends WebElement> actual) {
        return new ListWebElementSubject(metadata(), actual);
    }

    public static Factory<WebElementSubjectBuilder> elements() {
        return WebElementSubjectBuilder::new;
    }
}
