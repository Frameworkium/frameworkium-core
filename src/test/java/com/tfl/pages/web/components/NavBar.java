package com.tfl.pages.web.components;

import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;
import ru.yandex.qatools.htmlelements.element.TextInput;

import com.frameworkium.pages.internal.PageFactory;
import com.frameworkium.pages.internal.Visible;
import com.github.pages.web.HomePage;
import com.github.pages.web.SearchResultsPage;
import com.tfl.pages.web.PlanJourneyPage;

@Name("Tfl NavBar")
@FindBy(css = "div.mainnav")
public class NavBar extends HtmlElement {

    @Name("Home Logo/Link")
    @Visible
    @FindBy(css = "div.logo a")
    private Link homeLink;

    @Name("Plan A Journey Link")
    @Visible
    @FindBy(css = "li.plan-journey a")
    private Link planJourneyLink;

    @Name("Search Box")
    @FindBy(id = "q")
    private TextInput searchBox;

    
    @Step("Go Home")
    public HomePage clickLogo() {
        homeLink.click();
        return PageFactory.newInstance(HomePage.class);
    }

    @Step("Go to the plan journey page")
    public PlanJourneyPage clickPlanJourneyLink() {
        planJourneyLink.click();
        return PageFactory.newInstance(PlanJourneyPage.class);
    }

    @Step("Search for the text '{0}'")
    public SearchResultsPage search(String searchText) {
        searchBox.sendKeys(searchText);
        searchBox.sendKeys(Keys.ENTER);
        return PageFactory.newInstance(SearchResultsPage.class);
    }

}
