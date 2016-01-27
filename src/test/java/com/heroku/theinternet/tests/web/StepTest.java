package com.heroku.theinternet.tests.web;

import com.frameworkium.tests.internal.BaseTest;
import org.testng.annotations.Test;
import ru.yandex.qatools.allure.annotations.Issue;
import ru.yandex.qatools.allure.annotations.Step;

import static com.google.common.truth.Truth.assertThat;

/**
 * Created by civie21 on 18/12/2015.
 */
public class StepTest extends BaseTest {

    @Issue("TEST-1")
    @Step("1")
    @Test(groups = {"blah"})

    public void test1(){
        assertThat(1).isEqualTo(1);

    }

    @Issue("TEST-1")
    @Step("2")
    @Test(groups = {"blah"})

    public void test2(){
        assertThat(1).isEqualTo(1);

    }

    @Issue("TEST-1")
    @Step("3")
    @Test(groups = {"blah"})

    public void test3(){
        assertThat(1).isEqualTo(2);

    }

    @Issue("TEST-2")
    @Step("1")
    @Test(groups = {"blah2"})

    public void test4(){
        assertThat(1).isEqualTo(2);

    }

}
