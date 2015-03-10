package com.frameworkium.tests.web;

import static com.google.common.truth.Truth.assertThat;

import org.testng.annotations.Test;

import ru.yandex.qatools.allure.annotations.Issue;

import com.frameworkium.pages.web.DynamicLoadingExamplePage;
import com.frameworkium.pages.web.WelcomePage;
import com.frameworkium.tests.internal.BaseTest;

public class SimpleWebTest extends BaseTest {

    @Issue("KT-3")
    @Test(description = "Test testy")
    public final void testTest() {
        // Navigate to the dynamic loading hidden element page
        DynamicLoadingExamplePage dynamicLoadingExamplePage =
                WelcomePage.open().then().clickDynamicLoading().then().clickExample1();

        // assert the title of the page, not a good assertion, improving this is an exercise for the reader
        assertThat(dynamicLoadingExamplePage.getTitle()).isEqualTo("The Internet");
    }
}
