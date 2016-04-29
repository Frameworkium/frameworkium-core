package com.heroku.theinternet.pages.web;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.driver.DriverSetup;
import com.frameworkium.core.ui.driver.DriverType;
import com.frameworkium.core.ui.pages.BasePage;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.core.ui.tests.BaseTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import java.util.ArrayList;
import java.util.List;


public class WelcomePage extends BasePage<WelcomePage> {

    @Visible
    @Name("Basic auth link")
    @FindBy(linkText = "Basic Auth")
    private Link basicAuthLink;

    @Step("Navigate to http://the-internet.herokuapp.com")
    public static WelcomePage open() {
        return PageFactory.newInstance(WelcomePage.class, "http://the-internet.herokuapp.com",30);
    }

    @Step("Navigate to http://the-internet.herokuapp.com")
    public static WelcomePage open2() {


        DriverType dt = DriverSetup.initialiseDesiredDriverType(DriverSetup.returnBrowserObject(DriverSetup.SupportedBrowsers.FIREFOX));
       // dt.instantiate2(new DesiredCapabilities.);




        return PageFactory.newInstance(WelcomePage.class, "http://the-internet.herokuapp.com",30);
    }


}
