package com.lazerycode.selenium.pages.mobi;

import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import ru.yandex.qatools.allure.annotations.Step;
import ru.yandex.qatools.htmlelements.annotations.Name;
import ru.yandex.qatools.htmlelements.element.Button;
import ru.yandex.qatools.htmlelements.element.HtmlElement;
import ru.yandex.qatools.htmlelements.loader.HtmlElementLoader;

import com.lazerycode.selenium.pages.internal.BasePage;
import com.lazerycode.selenium.pages.internal.PageFactory;

public class HomePage extends BasePage<HomePage> {

    @Name("For Sale button")
    private Button forSaleButton;

    @Name("Recent Searches")
    @FindBy(css = "a[class^='viewRSresults'] div h3")
    private List<HtmlElement> recentSearchLinks;

    public HomePage get() {
        HtmlElementLoader.populatePageObject(this, driver);
        wait.until(ExpectedConditions.presenceOfElementLocated(By
                .id("forSaleButton")));
        return this;
    }

    @Override
    public HomePage get(String url) {
        driver.get(url);
        return get();
    }

    @Step("Navigate to http://www.rightmove.co.uk")
    public static HomePage open() {
        return PageFactory.newInstance(HomePage.class,
                "http://www.rightmove.co.uk");
    }

    @Step("Click For Sale")
    public ForSaleFilterPage clickForSale() {
        forSaleButton.click();
        
        return PageFactory.getInstance(ForSaleFilterPage.class);
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
