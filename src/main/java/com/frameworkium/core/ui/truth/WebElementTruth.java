package com.frameworkium.core.ui.truth;

import com.google.common.truth.CustomSubjectBuilder;
import org.openqa.selenium.WebElement;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.truth.Truth.assertAbout;
import static com.google.common.truth.Truth.assert_;

public class WebElementTruth {

    private static CustomSubjectBuilder.Factory<WebElementSubjectBuilder> elements() {
        return WebElementSubjectBuilder.elements();
    }

    public static WebElementSubject assertThat(@Nullable WebElement actual) {
        return assertAbout(elements()).that(actual);
    }

    public static ListWebElementSubject assertThat(@Nullable List<? extends WebElement> actual) {
        return assertAbout(elements()).that(actual);
    }

    public static WebElementSubjectBuilder assertWithMessage(String messageToPrepend) {
        return assert_().withMessage(messageToPrepend).about(elements());
    }
}
