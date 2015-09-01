package com.heroku.theinternet.tests.web;

import com.frameworkium.tests.internal.BaseTest;
import com.heroku.theinternet.pages.web.WelcomePage;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class TheInternetExampleTest extends BaseTest {

    @Test
    public void smokeTest() {

        assertThat(WelcomePage.open().then().getTitle()).isEqualTo("The Internet");
    }
}
