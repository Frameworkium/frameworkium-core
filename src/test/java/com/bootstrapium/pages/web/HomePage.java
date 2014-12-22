package com.bootstrapium.pages.web;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.Keys;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.element.TextInput;

import com.bootstrapium.pages.internal.BasePage;
import com.bootstrapium.pages.internal.PageFactory;
import com.bootstrapium.pages.internal.Visible;

public class HomePage extends BasePage<HomePage> {

    @Visible
    @Name("Search field")
    @FindBy(id = "searchLocation")
    private TextInput searchField;

    @Name("Recent Search links")
    @FindBy(css = "a[id^='recent-search-']")
    private List<HtmlElement> recentSearchLinks;

    @Step("Navigate to http://www.rightmove.co.uk")
    public static HomePage open() {
        return PageFactory.newInstance(HomePage.class,
                "http://www.rightmove.co.uk");
    }

    @Step("Search properties for sale near \"{0}\".")
    public ForSaleFilterPage searchPropertiesForSale(String location) {
        // Clear field then perform search
        searchField.clear();
        searchField.sendKeys(location + Keys.RETURN);
        return PageFactory.newInstance(ForSaleFilterPage.class);
    }

    @Step("Get the recent searches.")
    public List<String> getRecentSearches() {
        List<String> recentSearches = new ArrayList<String>();
        for (HtmlElement link : recentSearchLinks) {
            recentSearches.add(link.getText());
        }
        return recentSearches;
    }
}
