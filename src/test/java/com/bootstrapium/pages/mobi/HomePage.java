package com.bootstrapium.pages.mobi;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;

import com.bootstrapium.pages.internal.BasePage;
import com.bootstrapium.pages.internal.PageFactory;
import com.bootstrapium.pages.internal.Visible;

public class HomePage extends BasePage<HomePage> {

    @Visible
    @Name("For Sale button")
    private Button forSaleButton;

    @Name("Recent Searches")
    @FindBy(css = "a[class^='viewRSresults'] div h3")
    private List<HtmlElement> recentSearchLinks;

    @Step("Navigate to http://www.rightmove.co.uk")
    public static HomePage open() {
        return PageFactory.newInstance(HomePage.class,
                "http://www.rightmove.co.uk");
    }

    @Step("Click For Sale")
    public ForSaleFilterPage clickForSale() {
        forSaleButton.click();
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
