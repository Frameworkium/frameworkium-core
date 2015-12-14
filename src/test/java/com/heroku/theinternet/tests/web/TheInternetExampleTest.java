package com.heroku.theinternet.tests.web;

import ru.yandex.qatools.allure.annotations.Issue;

import com.frameworkium.tests.internal.BaseTest;
import com.heroku.theinternet.pages.web.WelcomePage;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class TheInternetExampleTest extends BaseTest {

    @Test
    @Issue("TEST-1")
    public void smokeTest() {

        assertThat(WelcomePage.open().then().getTitle()).isEqualTo("The Internet");
    }
}
