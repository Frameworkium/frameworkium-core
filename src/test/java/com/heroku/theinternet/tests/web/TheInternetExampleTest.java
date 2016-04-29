package com.heroku.theinternet.tests.web;

import com.frameworkium.core.ui.tests.BaseTest;
import ru.yandex.qatools.allure.annotations.Issue;

import com.heroku.theinternet.pages.web.WelcomePage;
import org.testng.annotations.Test;

import static com.google.common.truth.Truth.assertThat;

public class TheInternetExampleTest extends BaseTest {

    @Test
    //@Issue("TEST-1")
    public void smokeTest() {

        assertThat(WelcomePage.open2().then().getTitle()).isEqualTo("The Internet");
    }
}
