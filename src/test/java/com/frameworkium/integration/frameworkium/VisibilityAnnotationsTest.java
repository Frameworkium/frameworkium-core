package com.frameworkium.integration.frameworkium;

import com.frameworkium.core.ui.tests.BaseTest;
import com.frameworkium.integration.frameworkium.pages.MultiVisibilityTypifiedElement;
import com.frameworkium.integration.frameworkium.pages.MultiVisibilityWebElement;
import org.testng.annotations.Test;

public class VisibilityAnnotationsTest extends BaseTest {

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Field webElement on .*MultiVisibilityWebElement has too many Visibility related Annotations")
    public void more_than_one_visibility_annotation_on_web_element_throws_exception() {
        MultiVisibilityWebElement.open();
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp =
                    "Field typifiedElement on .*MultiVisibilityTypifiedElement has too many Visibility related Annotations")
    public void more_than_one_visibility_annotation_on_typified_element_throws_exception() {
        MultiVisibilityTypifiedElement.open();
    }

}
