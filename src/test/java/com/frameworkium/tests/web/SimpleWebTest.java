package com.frameworkium.tests.web;

import static com.google.common.truth.Truth.assertThat;

import org.testng.annotations.Test;

import com.frameworkium.annotations.Jira;
import com.frameworkium.pages.web.DynamicLoadingExamplePage;
import com.frameworkium.pages.web.WelcomePage;
import com.frameworkium.tests.internal.BaseTest;

public class SimpleWebTest extends BaseTest {

    @Jira("KT-3")
    @Test(description = "Test testy")
    public final void testTest() {
        //Navigate to the dynamic loading hidden element page
        DynamicLoadingExamplePage dynamicLoadingExamplePage = WelcomePage.open().then().clickDynamicLoading()
                .then().clickExample1();
    }
  
}
