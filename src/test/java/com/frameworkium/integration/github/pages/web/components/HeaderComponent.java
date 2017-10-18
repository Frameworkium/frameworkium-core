package com.frameworkium.integration.github.pages.web.components;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.core.ui.pages.Visibility;
import com.frameworkium.core.ui.tests.BaseTest;
import com.frameworkium.integration.github.pages.web.ExplorePage;
import com.frameworkium.integration.github.pages.web.SearchResultsPage;
import org.openqa.selenium.*;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.*;

import static org.openqa.selenium.support.ui.ExpectedConditions.visibilityOf;

@Name("Github Header")
@FindBy(css = "header")
public class HeaderComponent extends HtmlElement {

    @Visible
    @Name("Home Logo/Link")
    @FindBy(css = "a.header-logo-invertocat")
    private Link homeLink;

    @FindBy(name = "q")
    private TextInput searchBox;

    @FindBy(className = "btn-link")
    private Button hamburgerButton;

    @FindBy(css = "nav")
    private WebElement headerMenu;

    @FindBy(css = "nav a[href='/explore']")
    private Link exploreLink;

    private void showHeaderMenuIfCollapsed() {
        Wait<WebDriver> wait = BaseTest.newDefaultWait();

        // If browser is opened with width of 960 pixels or less
        // then the Header Menu is not displayed and a 'Hamburger' button is displayed instead. 
        // This button needs to be clicked to display the Header Menu.
        if (!headerMenu.isDisplayed()) {
            if (!hamburgerButton.isDisplayed()) {
                // Sometimes the Hamburger button is not initially displayed
                // so click the Header element to display the button 
                this.click();
            }
            hamburgerButton.click();

            // Ensure the Header Menu is displayed before attempting to click a link
            wait.until(ExpectedConditions.visibilityOf(headerMenu));
        }
    }

    @Step("Go to the explore page")
    public ExplorePage clickExplore() {
        showHeaderMenuIfCollapsed();
        exploreLink.click();
        return PageFactory.newInstance(ExplorePage.class);
    }

    @Step("Search for the text '{0}'")
    public SearchResultsPage search(String searchText) {
        showHeaderMenuIfCollapsed();
        searchBox.sendKeys(searchText + Keys.ENTER);
        return PageFactory.newInstance(SearchResultsPage.class);
    }

    @Step("Testing Visibility.forceVisible()")
    public void testForceVisible() {
        Wait<WebDriver> wait = BaseTest.newDefaultWait();

        WebElement link = homeLink.getWrappedElement();
        // hide the home link
        BaseTest.getDriver().executeScript(
                "arguments[0].style.visibility='hidden';", link);
        wait.until(ExpectedConditions.not(visibilityOf(link)));
        // test force visible works
        new Visibility().forceVisible(link);
        wait.until(visibilityOf(link));
    }
}
