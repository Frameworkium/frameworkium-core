package com.bootstrapium.tests.mobi;

import static com.google.common.truth.Truth.assertThat;

import org.testng.annotations.Test;

import com.bootstrapium.pages.web.DynamicLoadingExamplePage;
import com.bootstrapium.pages.web.WelcomePage;
import com.bootstrapium.tests.internal.BaseTest;

public class DynamicLoadingMobiTest extends BaseTest {

    @Test
    public final void testHiddenElement() {
        //Navigate to the dynamic loading hidden element page
        DynamicLoadingExamplePage dynamicLoadingExamplePage = WelcomePage.open().then().clickDynamicLoading()
                .then().clickExample1();
        
      //Assert that the element is hidden
        assertThat(dynamicLoadingExamplePage.isElementDisplayed()).isFalse();
        
        //Click start and wait for element to be displayed
        dynamicLoadingExamplePage.clickStart().then().waitForElementToBeDisplayed();
        
        //Assert that the element is indeed displayed
        assertThat(dynamicLoadingExamplePage.isElementDisplayed()).isTrue();
    }

    @Test
    public final void testElementRenderAfterTheFact() {
      //Navigate to the dynamic loading element not yet rendered page
        DynamicLoadingExamplePage dynamicLoadingExamplePage = WelcomePage.open().then().clickDynamicLoading()
                .then().clickExample2();
        
        //Assert that the element is not present
        assertThat(dynamicLoadingExamplePage.isElementPresent()).isFalse();
        
        //Click start and wait for element to be displayed
        dynamicLoadingExamplePage.clickStart().then().waitForElementToBeDisplayed();
        
        //Assert that the element is indeed present
        assertThat(dynamicLoadingExamplePage.isElementPresent()).isTrue();
    }
}
