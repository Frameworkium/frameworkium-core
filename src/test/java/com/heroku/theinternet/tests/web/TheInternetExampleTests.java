package com.heroku.theinternet.tests.web;

import static com.google.common.truth.Truth.assertThat;

import com.frameworkium.tests.internal.BaseTest;
import com.heroku.theinternet.pages.web.WelcomePage;

import org.testng.annotations.Test;

public class TheInternetExampleTests extends BaseTest {

    @Test
    public void basicAuth() {

        assertThat(WelcomePage.open().then().getTitle()).isEqualTo("The Internet");
    }
}