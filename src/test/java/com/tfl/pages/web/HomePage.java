package com.tfl.pages.web;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Link;

import com.frameworkium.pages.internal.BasePage;
import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;

public class HomePage extends BasePage<HomePage> {

    @Name("Plan A Journey Link")
    @Visible
    @FindBy(css = "li.plan-journey a")
    private Link planJourneyLink;

    @Name("Tube Line Status Board")
    @Visible
    @FindBy(css = "div.board-wrapper")
    private WebElement statusBoard;
    

    @Step("Navigate to the TFL homepage")
    public static HomePage open() {
        return PageFactory.newInstance(HomePage.class, "http://tfl.gov.uk");
    }

    
    @Step("Go to the plan journey page")
    public PlanJourneyPage clickPlanJourneyLink() {
        planJourneyLink.click();
        return PageFactory.newInstance(PlanJourneyPage.class);
    }

    
}
