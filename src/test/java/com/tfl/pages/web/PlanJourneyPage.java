package com.tfl.pages.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;
import com.tfl.pages.web.components.NavBar;

public class PlanJourneyPage extends BasePage<PlanJourneyPage> {

    @Name("NavBar")
    @Visible
    private NavBar navBar;

    @Name("From Field")
    @Visible
    @FindBy(css = "input#InputFrom")
    private WebElement fromField;

    @Name("To Field")
    @Visible
    @FindBy(css = "input#InputTo")
    private WebElement toField;

    @Name("Plan my Journey Button")
    @Visible
    @FindBy(css = "input.plan-journey-button")
    private WebElement planJourneyButton;


    @Step("Plan journey from {0} to {1}")
    public JourneyPlannerResultsPage planJourney(String from, String to)
    {
        fromField.sendKeys(from);
        toField.sendKeys(to);
        planJourneyButton.click();
        return PageFactory.newInstance(JourneyPlannerResultsPage.class);
    }
    
    public NavBar theNavBar() {
        return navBar;
    }
}
