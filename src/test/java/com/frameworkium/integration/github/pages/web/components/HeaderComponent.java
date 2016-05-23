package com.frameworkium.integration.github.pages.web.components;

import com.frameworkium.core.ui.annotations.Visible;
import com.frameworkium.core.ui.pages.PageFactory;
import com.frameworkium.integration.github.pages.web.ExplorePage;
import com.frameworkium.integration.github.pages.web.HomePage;
import com.frameworkium.integration.github.pages.web.SearchResultsPage;
import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;
import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.Link;
import ru.yandex.qatools.htmlelements.element.TextInput;

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
}
