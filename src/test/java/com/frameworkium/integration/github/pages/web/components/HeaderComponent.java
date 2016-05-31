package com.frameworkium.integration.github.pages.web.components;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.core.ui.pages.Visibility;
import com.frameworkium.core.ui.tests.BaseTest;
import com.frameworkium.integration.github.pages.web.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.*;

@Name("Github Header")
@FindBy(css = "header")
public class HeaderComponent extends HtmlElement {

    @Name("Home Logo/Link")
    @Visible
    @FindBy(css = "a.header-logo-invertocat")
    private Link homeLink;

    @Name("Search Box")
    @FindBy(name = "q")
    private TextInput searchBox;

    @Name("Explore Link")
    @Visible
    @FindBy(css = "header nav a.nav-item-explore")
    private Link exploreLink;

    @Step("Go Home")
    public HomePage clickLogo() {
        homeLink.click();
        return PageFactory.newInstance(HomePage.class);
    }

    @Step("Go to the explore page")
    public ExplorePage clickExplore() {
        exploreLink.click();
        return PageFactory.newInstance(ExplorePage.class);
    }

    @Step("Search for the text '{0}'")
    public SearchResultsPage search(String searchText) {
        searchBox.sendKeys(searchText);
        searchBox.sendKeys(Keys.ENTER);
        return PageFactory.newInstance(SearchResultsPage.class);
    }

    @Step("Testing Visibility.forceVisible()")
    public void testForceVisible() {
        Wait<WebDriver> wait = BaseTest.newDefaultWait();

        WebElement link = homeLink.getWrappedElement();
        // hide the home link
        BaseTest.getDriver().executeScript("arguments[0].style.visibility='hidden';", link);
        wait.until(ExpectedConditions.not(ExpectedConditions.visibilityOf(link)));
        // test force visible works
        new Visibility().forceVisible(link);
        wait.until(ExpectedConditions.visibilityOf(link));
    }
}
